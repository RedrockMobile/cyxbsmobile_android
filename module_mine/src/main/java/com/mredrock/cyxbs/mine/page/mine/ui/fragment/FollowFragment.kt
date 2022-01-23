package com.mredrock.cyxbs.mine.page.mine.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.api.account.IAccountService
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.toast
import com.mredrock.cyxbs.mine.R
import com.mredrock.cyxbs.mine.network.NetworkState
import com.mredrock.cyxbs.mine.page.mine.adapter.DataBindingAdapter
import com.mredrock.cyxbs.mine.page.mine.binder.*
import com.mredrock.cyxbs.mine.page.mine.ui.activity.HomepageActivity
import com.mredrock.cyxbs.mine.page.mine.viewmodel.FollowViewModel
import kotlinx.android.synthetic.main.mine_fragment_follow.*

/**
 * @class
 * @author YYQF
 * @data 2021/9/16
 * @description
 **/
class FollowFragment : BaseViewModelFragment<FollowViewModel>() {

    private var redId = ""
    private lateinit var userAdapter: DataBindingAdapter
    private var isSelf: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            redId = it.getString("redid").orEmpty()
        }

        if (redId ==
            ServiceManager.getService(IAccountService::class.java).getUserService().getRedid()){
            isSelf = true
        }

        return inflater.inflate(R.layout.mine_fragment_follow, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initObserve()
        initRecycler()
    }

    private fun initObserve() {
        viewModel.followList.observe(viewLifecycleOwner, {
            userAdapter.notifyAdapterChanged(mutableListOf<BaseDataBinder<*>>().apply {
                when (it.size) {
                    0 -> add(EmptyFollowBinder(isSelf))
                    else -> for (fan in it) {
                        add(FollowBinder(fan,isSelf,
                        onFocusClick = { view, user ->
                            viewModel.changeFocusStatus(user.redid)
                            (view as TextView).apply {
                                if (text == "+关注") {
                                    background = ContextCompat
                                        .getDrawable(
                                            requireContext(),
                                            R.drawable.mine_shape_tv_focused
                                        )
                                    text = if (user.isFocus) "互相关注" else " 已关注"
                                    context.toast(R.string.mine_person_focus_success)
                                }else{
                                    background = ContextCompat
                                        .getDrawable(
                                            requireContext(),
                                            R.drawable.mine_shape_tv_unfocus
                                        )
                                    text = "+关注"
                                    context.toast(R.string.mine_person_unfocus_success)

                                }
                            }
                        },
                        onAvatarClick = {
                            HomepageActivity.startHomePageActivity(it,requireActivity())
                        }))
                    }
                }
            })
        })

        viewModel.followNetWorkState.observe(viewLifecycleOwner,{
            if (it == NetworkState.FAILED){
                userAdapter.notifyAdapterChanged(mutableListOf<BaseDataBinder<*>>().apply {
                    add(EmptyFollowBinder(isSelf))
                })
            }

            if (it != NetworkState.LOADING){
                mine_follow_srl.isRefreshing = false
            }
        })

        mine_follow_srl.setOnRefreshListener {
            viewModel.getFollows(redId)
        }
    }

    private fun initRecycler() {
        userAdapter = DataBindingAdapter(LinearLayoutManager(requireContext()))

        mine_follow_rv.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.getFollows(redId)
    }
}