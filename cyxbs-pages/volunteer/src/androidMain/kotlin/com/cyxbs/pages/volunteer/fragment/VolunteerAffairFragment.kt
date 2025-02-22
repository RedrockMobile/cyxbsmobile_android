package com.cyxbs.pages.volunteer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.cyxbs.pages.volunteer.R
import com.cyxbs.pages.volunteer.adapter.VolunteerAffairAdapter
import com.cyxbs.pages.volunteer.viewmodel.VolunteerAffairViewModel
import com.cyxbs.pages.volunteer.widget.VolunteerAffairBottomSheetDialog

/**
 * Created by yyfbe, Date on 2020/9/4.
 */
class VolunteerAffairFragment : BaseViewModelFragment<VolunteerAffairViewModel>() {
    private var volunteerAffairBottomSheetDialog: VolunteerAffairBottomSheetDialog? = null
    private val swl_volunteer_affair by R.id.swl_volunteer_affair.view<SwipeRefreshLayout>()
    private val rl_volunteer_affair by R.id.rl_volunteer_affair.view<RecyclerView>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context)
            .inflate(R.layout.volunteer_fragment_volunteer_affair, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getVolunteerAffair()
        swl_volunteer_affair.setOnRefreshListener {
            viewModel.getVolunteerAffair()
        }
        rl_volunteer_affair.layoutManager = LinearLayoutManager(context)
        viewModel.volunteerAffairs.observe {
            swl_volunteer_affair.isRefreshing = false
            val adapter = rl_volunteer_affair.adapter
            if (adapter == null) {
                rl_volunteer_affair.adapter =
                    VolunteerAffairAdapter(it ?: return@observe) { affair ->
                        if (context == null) return@VolunteerAffairAdapter
                        if (volunteerAffairBottomSheetDialog == null) {
                            volunteerAffairBottomSheetDialog =
                                VolunteerAffairBottomSheetDialog(requireContext())
                        }
                        volunteerAffairBottomSheetDialog?.show()
                        volunteerAffairBottomSheetDialog?.refresh(affair)
                    }
            } else {
                it?.let { list -> (adapter as VolunteerAffairAdapter).refreshData(list) }
            }
        }
    }
}