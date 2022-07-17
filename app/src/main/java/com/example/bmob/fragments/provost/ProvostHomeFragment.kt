package com.example.bmob.fragments.provost

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bmob.R
import com.example.bmob.common.BannerAdapter
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.common.SearchRecyclerViewAdapter
import com.example.bmob.data.entity.IDENTIFICATION_DEAN
import com.example.bmob.data.entity.IDENTIFICATION_STUDENT
import com.example.bmob.data.entity.IDENTIFICATION_TEACHER
import com.example.bmob.databinding.FragmentProvostHomeBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.viewmodels.CommonHomeViewModel
import com.example.bmob.viewmodels.CommonHomeViewModel.Companion.ERROR
import com.example.bmob.viewmodels.SetViewModel
import com.youth.banner.indicator.CircleIndicator


class ProvostHomeFragment : Fragment(), FragmentEventListener {
    private lateinit var binding: FragmentProvostHomeBinding
    private var adapter: SearchRecyclerViewAdapter? = null
    private val viewModel: CommonHomeViewModel by viewModels()

    //activityViewModels相当于单例模式，此处用setViewModel是保证用户修改数据后同步数据到改界面
    private val setViewModel: SetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProvostHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.banner1.start()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventListener()
        viewModel.setFragment(this)
        binding.banner1.addBannerLifecycleObserver(this)
        setViewModel.getUserByQuery().observe(viewLifecycleOwner) {
            binding.user = it
        }
        //观测搜索结果
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.first != ERROR) {
                if (adapter == null && it.second.isNotEmpty()) {
                    viewModel.isShowRecyclerView(
                        binding.recyclerView1,
                        binding.contentLinearLayout,
                        true
                    )
                    adapter = SearchRecyclerViewAdapter(it.second) { thesis ->
                        val actionProvostHomeFragmentToShowThesisFragment =
                            ProvostHomeFragmentDirections.actionProvostHomeFragmentToShowThesisFragment(
                                thesis, false
                            )
                        findNavController().navigate(actionProvostHomeFragmentToShowThesisFragment)
                    }
                    binding.recyclerView1.adapter = adapter
                    binding.recyclerView1.layoutManager = LinearLayoutManager(
                        requireContext(),
                        RecyclerView.VERTICAL, false
                    )
                } else {
                    if (it.second.isNotEmpty() && viewModel.getNowSearch().value == it.first) {
                        viewModel.isShowRecyclerView(
                            binding.recyclerView1,
                            binding.contentLinearLayout,
                            true
                        )
                        adapter!!.setThesisList(it.second)
                    } else {
                        viewModel.isShowRecyclerView(
                            binding.recyclerView1,
                            binding.contentLinearLayout,
                            false
                        )
                    }
                }
            }
        }
        //观测banner数据
        viewModel.queryBannerData().observe(viewLifecycleOwner) {
            binding.banner1
                .setAdapter(BannerAdapter(it!!))
                .indicator = CircleIndicator(requireContext())
        }


        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ it->
            //通过的权限
            val grantedList = it.filterValues { it }.mapNotNull { it.key }
            //是否所有权限都通过
            val allGranted = grantedList.size == it.size
            val list = (it - grantedList.toSet()).map { it.key }
            //未通过的权限
            val deniedList = list.filter { ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it) }
            //拒绝并且点了“不再询问”权限
            val alwaysDeniedList = list - deniedList.toSet()
        }.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    override fun onStop() {
        super.onStop()
        binding.banner1.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.banner1.destroy()
    }

    override fun setEventListener() {
        binding.issueTime.setOnClickListener {
            findNavController().navigate(R.id.action_provostHomeFragment_to_provostSelectTimeFragment)
        }
        //初始化搜索框
        viewModel.setSearchViewListener(
            binding.searchView2,
            binding.recyclerView1,
            binding.contentLinearLayout
        ) {
            if (it) {
                Log.v(LOG_TAG, "输入的内容空")
                adapter = null
            }
        }

        //查看老师信息
        binding.teacherInfo.setOnClickListener {
            val actionProvostHomeFragmentToSkimFragment =
                ProvostHomeFragmentDirections.actionProvostHomeFragmentToSkimFragment(
                    IDENTIFICATION_TEACHER
                )
            findNavController().navigate(actionProvostHomeFragmentToSkimFragment)
        }

        //查看学生信息
        binding.studentInfo.setOnClickListener {
            val actionProvostHomeFragmentToSkimFragment =
                ProvostHomeFragmentDirections.actionProvostHomeFragmentToSkimFragment(
                    IDENTIFICATION_STUDENT
                )
            findNavController().navigate(actionProvostHomeFragmentToSkimFragment)
        }

        //查看系主任信息
        binding.deanInfo.setOnClickListener {
            val actionProvostHomeFragmentToSkimFragment =
                ProvostHomeFragmentDirections.actionProvostHomeFragmentToSkimFragment(
                    IDENTIFICATION_DEAN
                )
            findNavController().navigate(actionProvostHomeFragmentToSkimFragment)
        }
    }
}