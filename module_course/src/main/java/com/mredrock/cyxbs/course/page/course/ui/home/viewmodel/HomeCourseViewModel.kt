package com.mredrock.cyxbs.course.page.course.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mredrock.cyxbs.course.page.course.data.AffairData
import com.mredrock.cyxbs.course.page.course.data.StuLessonData
import com.mredrock.cyxbs.course.page.course.data.toStuLessonData
import com.mredrock.cyxbs.course.page.course.model.StuLessonRepository
import com.mredrock.cyxbs.course.page.link.model.LinkRepository
import com.mredrock.cyxbs.course.page.link.room.LinkStuEntity
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/27 17:12
 */
class HomeCourseViewModel : BaseViewModel() {
  
  val homeWeekData: LiveData<Map<Int, HomePageResult>> get() = _homeWeekData
  private val _homeWeekData = MutableLiveData<Map<Int, HomePageResult>>()
  
  val linkStu: LiveData<LinkStuEntity> get() = _linkStu
  private val _linkStu = MutableLiveData<LinkStuEntity>()
  
  val refreshEvent: SharedFlow<Boolean> get() = _refresh
  private val _refresh = MutableSharedFlow<Boolean>()
  
  /**
   * 改变关联人的可见性
   */
  fun changeLinkStuVisible(isShowLink: Boolean) {
    LinkRepository.changeLinkStuVisible(isShowLink)
    // 这里更新后，所有观察关联的地方都会重新发送新数据
  }
  
  /**
   * 重新请求数据，相当于强制刷新，建议测试使用
   */
  fun retryObserveHomeWeekData() {
    mRetryObservable.onNext(Unit)
  }
  
  /**
   * Rxjava 中类似于 LiveData 的东西，用于重新请求数据
   */
  private val mRetryObservable = BehaviorSubject.create<Unit>()
  
  init {
    initObserve()
  }
  
  private fun initObserve() {
    // 自己课的观察流
    val selfLessonObservable = StuLessonRepository.observeSelfLesson()
      .map { it.toStuLessonData() }
  
    // 关联人课的观察流
    val linkLessonObservable = LinkRepository.observeLinkStudent()
      .doOnNext { _linkStu.postValue(it) }
      .distinctUntilChanged { t1, t2 ->
        // 当自身学号以及关联人学号未发生改变时就不通知下游
        t1.selfNum == t2.selfNum && t1.linkNum == t2.linkNum
      }.switchMap {
        if (it.isNull()) Observable.just(emptyList()) // 没得关联人时发送空数据
        else StuLessonRepository.observeLesson(it.linkNum)
      }.map { it.toStuLessonData() }
  
    // 事务的观察流
    val affairObservable = Observable.just<List<AffairData>>(emptyList()) // TODO 事务待完成
  
    // 合并观察流
    Observable.combineLatest(
      selfLessonObservable,
      linkLessonObservable,
      affairObservable
    ) { self, link, affair ->
      HomePageResultImpl.getMap(self, link, affair)
    }.safeSubscribeBy {
      _homeWeekData.postValue(it)
    }
    
    // 刷新课表的观察
    mRetryObservable
      .safeSubscribeBy {
        LinkRepository.getLinkStudent()
          .flatMapCompletable {
            if (it.isNotNull()) {
              // 直接调用网络刷新，请求成功后会修改数据库，然后上面的观察流会重新发送新的值
              val self = StuLessonRepository.refreshLesson(it.selfNum)
              val link = StuLessonRepository.refreshLesson(it.linkNum)
              // TODO 刷新事务待完成
              Single.mergeDelayError(self, link) // 使用 mergeDelayError() 延迟异常
                .flatMapCompletable { Completable.complete() }
            } else Completable.error(IllegalStateException("关联人数据为空！"))
          }.doOnError {
            viewModelScope.launch {
              _refresh.emit(false)
            }
          }.safeSubscribeBy {
            viewModelScope.launch {
              _refresh.emit(true)
            }
          }
      }
  }
  
  /**
   * 测试使用
   */
  private fun getLesson(week: Int, hashDay: Int, beginLesson: Int, period: Int, title: String): StuLessonData {
    return StuLessonData(
      "", week, beginLesson, "", title, "",
      "", hashDay, period, "", "", "")
  }
  
  interface HomePageResult {
    val selfLesson: List<StuLessonData>
    val linkLesson: List<StuLessonData>
    val affair: List<AffairData>
    
    companion object EMPTY : HomePageResult {
      override val selfLesson: List<StuLessonData>
        get() = emptyList()
      override val linkLesson: List<StuLessonData>
        get() = emptyList()
      override val affair: List<AffairData>
        get() = emptyList()
    }
  }
  
  data class HomePageResultImpl(
    override val selfLesson: MutableList<StuLessonData>,
    override val linkLesson: MutableList<StuLessonData>,
    override val affair: MutableList<AffairData>
  ) : HomePageResult {
    companion object {
      fun getMap(
        self: List<StuLessonData>,
        link: List<StuLessonData>,
        affair: List<AffairData>
      ) : Map<Int, HomePageResultImpl> {
        return buildMap {
          self.forEach {
            getOrPut(it.week) { HomePageResultImpl(arrayListOf(), arrayListOf(), arrayListOf()) }
              .selfLesson.add(it)
          }
          link.forEach {
            getOrPut(it.week) { HomePageResultImpl(arrayListOf(), arrayListOf(), arrayListOf()) }
              .linkLesson.add(it)
          }
          affair.forEach {
            getOrPut(it.week) { HomePageResultImpl(arrayListOf(), arrayListOf(), arrayListOf()) }
              .affair.add(it)
          }
        }
      }
    }
  }
}