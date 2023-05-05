package com.ganeshgfx.projectmanagement.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ganeshgfx.projectmanagement.MainActivity
import com.ganeshgfx.projectmanagement.R
import com.ganeshgfx.projectmanagement.Utils.log
import com.ganeshgfx.projectmanagement.databinding.FragmentCalenderBinding
import com.ganeshgfx.projectmanagement.viewModels.CalenderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalenderFragment : Fragment() {

    private var _binding: FragmentCalenderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalenderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calender, container, false)

        binding.toolbar.setupWithNavController(findNavController())

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        //GridLayoutManager(requireContext(), 7)

        binding.calender.layoutManager = layoutManager

        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val activity = requireActivity() as MainActivity
        viewModel.getTasks(activity.viewModel.currentProjectId)

        viewModel.adapter.setOnClickListener { day ->
            day?.let {
                //log(it)
                findNavController().navigate(CalenderFragmentDirections.actionCalenderFragmentToTasksListsFragment())
            }
        }

        binding.calender.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                viewModel.lastItem?.let {
                    recyclerView.post {
                        viewModel.newPageLast(it)
                    }
                }
                viewModel.firstItem?.let {
                    recyclerView.post {
                        //log("firstItem",it)
                        viewModel.newPageFirst(it)
                    }
                }
//                when (newState) {
//                    RecyclerView.SCROLL_STATE_IDLE -> {
//                        viewModel.lastItem?.let {
//                            log(it)
//                            viewModel.newPageLast(it)
//                        }
//                    }
//                    RecyclerView.SCROLL_STATE_DRAGGING -> {
//
//                    }
//                    RecyclerView.SCROLL_STATE_SETTLING -> {
//
//                    }
//                }
            }
        })

        return binding.root
    }

}