package com.mredrock.cyxbs.lib.course.helper.affair

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.lib.course.helper.affair.expose.IBoundary
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchAffairItem
import com.mredrock.cyxbs.lib.course.helper.affair.expose.ITouchCallback
import com.mredrock.cyxbs.lib.course.helper.base.ILongPressTouchHandler
import com.mredrock.cyxbs.lib.course.internal.view.course.ICourseViewGroup
import com.mredrock.cyxbs.lib.utils.utils.VibratorUtil
import com.ndhzs.netlayout.draw.ItemDecoration
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent
import com.ndhzs.netlayout.touch.multiple.event.IPointerEvent.Action.*
import kotlin.math.max
import kotlin.math.min

/**
 * 整个核心机制是：
 * onPointerTouchEvent() -> mScrollRunnable -> invalidate() -> mItemDecoration -> refreshTouchAffairView()
 *
 * @author 985892345
 * @date 2022/9/20 17:11
 */
class CreateAffairHandler(
  val course: ICourseViewGroup,
  val iTouch: ITouchCallback,
  val iBoundary: IBoundary,
  val touchAffairItem: ITouchAffairItem
) : ILongPressTouchHandler {
  
  private var mTopRow = 0 // Move 事件中选择区域的开始行数
  private var mBottomRow = 0 // Move 事件中选择区域的结束行数
  private var mInitialRow = 0 // Down 事件中触摸的初始行数
  private var mInitialColumn = 0 // Down 事件中触摸的初始列数
  
  private var mTouchRow = 0 // 当前触摸的行数
  private var mUpperRow = 0 // 选择区域的上限
  private var mLowerRow = course.rowCount - 1 // 选择区域的下限
  
  private var mPointerId = 0
  
  // 用于刷新 TouchAffairView 长度的回调，在每一帧时进行回调
  // 不单独使用 view.postOnAnimation() 的原因在于其他地方触发刷新时会导致自身无法刷新
  private val mItemDecoration = object : ItemDecoration {
    
    private val refreshRunnable = Runnable { refreshTouchAffairView() }
    
    override fun onDrawBelow(canvas: Canvas, view: View) {
      view.post(refreshRunnable) // 使用 post 防止绘制卡顿
    }
  }
  
  
  private var mInitialX = 0
  private var mInitialY = 0
  
  private var mLastMoveX = 0
  private var mLastMoveY = 0
  
  // 是否正处于触摸中
  private var mIsInTouch = false
  
  override fun onPointerTouchEvent(event: IPointerEvent, view: ViewGroup) {
    val x = event.x.toInt()
    val y = event.y.toInt()
    when (event.action) {
      DOWN -> {
        mIsInTouch = true // 开始
        mInitialX = x // 重置
        mInitialY = y // 重置
        mLastMoveX = x // 重置
        mLastMoveY = y // 重置
        mInitialRow = course.getRow(y) // 重置
        mInitialColumn = course.getColumn(x) // 重置
        mPointerId = event.pointerId // 重置
        mTopRow = mInitialRow // 重置
        mBottomRow = mInitialRow // 重置
        mTouchRow = mInitialRow // 重置
        mUpperRow = 0 // 重置
        mLowerRow = course.rowCount - 1 // 重置
      }
      MOVE -> {
        // 核心代码
        mScrollRunnable.startIfCan() // 触发 mScrollRunnable 中的核心代码
        course.invalidate()
        mLastMoveX = x
        mLastMoveY = y
      }
      UP, CANCEL -> {
        mIsInTouch = false // 结束
        mScrollRunnable.cancel()
        course.removeItemDecoration(mItemDecoration)
        iTouch.onTouchEnd(mPointerId, mInitialRow, mInitialColumn, mTouchRow, mTopRow, mBottomRow)
      }
    }
  }
  
  override fun onLongPressStart() {
    // 禁止父布局拦截
    course.getParent().requestDisallowInterceptTouchEvent(true)
    VibratorUtil.start(36) // 长按被触发来个震动提醒
    touchAffairItem.show(mTopRow, mBottomRow, mInitialColumn)
    iTouch.onLongPressStart(mPointerId, mInitialRow, mInitialColumn)
    course.addItemDecoration(mItemDecoration)
  }
  
  private fun refreshTouchAffairView() {
    val y = course.getAbsoluteY(mPointerId) + course.getScrollCourseY()
    val nowTouchRow = course.getRow(y) // 当前触摸的行数
    if (nowTouchRow < mTouchRow) {
      // 手指往上移动
      if (nowTouchRow < mInitialRow) {
        mUpperRow = max(iBoundary.getUpperRow(course, mInitialRow, nowTouchRow, mInitialColumn), 0)
      }
    } else if (nowTouchRow > mTouchRow) {
      // 手指往下移动
      if (nowTouchRow > mInitialRow) {
        mLowerRow = min(
          iBoundary.getLowerRow(course, mInitialRow, nowTouchRow, mInitialColumn),
          course.rowCount - 1
        )
      }
    }
    
    var topRow: Int
    var bottomRow: Int
    // 根据当前触摸的行数与初始行数比较，得到 topRow、bottomRow
    if (nowTouchRow > mInitialRow) {
      topRow = mInitialRow
      bottomRow = nowTouchRow
    } else {
      topRow = nowTouchRow
      bottomRow = mInitialRow
    }
    if (topRow < mUpperRow) topRow = mUpperRow // 根据上限再次修正 topRow
    if (bottomRow > mLowerRow) bottomRow = mLowerRow // 根据下限再次修正 bottomRow
    
    if (mTouchRow != nowTouchRow) {
      mTouchRow = nowTouchRow
      iTouch.onTouchMove(mPointerId, mInitialRow, mInitialColumn, nowTouchRow, topRow, bottomRow)
    }
    
    if (topRow != mTopRow || bottomRow != mBottomRow) { // 避免不必要的刷新
      touchAffairItem.refresh(mTopRow, mBottomRow, topRow, bottomRow)
      mTopRow = topRow
      mBottomRow = bottomRow
    }
  }
  
  // 滑到显示区域顶部或者底部时，使 mCourseScrollView 滚动的 Runnable
  private val mScrollRunnable = object : Runnable {
    
    var isInScrolling = false // 是否处于滚动状态
      private set
    
    private var velocity = 0 // 滚动的速度
    
    override fun run() {
      if (isAllowScrollAndCalculateVelocity()) {
        // 核心运行代码
        course.scrollCourseBy(velocity) // 滚动
        course.invalidate() // 刷新，之后会回调 mItemDecoration
        course.postOnAnimation(this) // 一直循环下去
      } else {
        isInScrolling = false
        velocity = 0
      }
    }
    
    /**
     * 如果能开启滚动的话就开启滚动，注意：滚动是会自己取消的
     */
    fun startIfCan() {
      if (!isInScrolling) { // 防止重复允许 Runnable
        isInScrolling = true
        run()
      }
    }
    
    /**
     * 取消滚动
     */
    fun cancel() {
      if (isInScrolling) {
        isInScrolling = false
        course.removeCallbacks(this)
      }
    }
    
    /**
     * 是否允许滚动，如果允许，则计算滚动速度给 [velocity] 变量
     */
    private fun isAllowScrollAndCalculateVelocity(): Boolean {
      val absoluteY = course.getAbsoluteY(mPointerId)
      val moveBoundary = 100 // 移动的边界值
      val minV = 6 // 最小速度
      val maxV = 20 // 最大速度
      // 速度最小为 6，最大为 20，与边界的差值成线性关系
      
      // 向上滚动，即手指移到底部，需要显示下面的内容
      val isNeedScrollUp =
        absoluteY > course.getScrollHeight() - moveBoundary
          && mTouchRow <= mLowerRow // 当前触摸的行数在下限以上
          && course.canCourseScrollVertically(1) // 没有滑到底
      
      // 向下滚动，即手指移到顶部，需要显示上面的内容
      val isNeedScrollDown =
        absoluteY < moveBoundary
          && mTouchRow >= mUpperRow // 当前触摸的行数在上限以下
          && course.canCourseScrollVertically(-1) // 没有滑到顶
      val isAllowScroll = isNeedScrollUp || isNeedScrollDown
      if (isAllowScroll) {
        velocity = if (isNeedScrollUp) {
          min((absoluteY - (course.getScrollHeight() - moveBoundary)) / 2 + minV, maxV)
        } else {
          // 如果是向下滚动稍稍降低加速度，因为顶部手指可以移动出去，很容易满速
          -min(((moveBoundary - absoluteY) / 10 + minV), maxV)
        }
      }
      return isAllowScroll
    }
  }
}