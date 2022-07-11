package com.example.bmob.fragments.student.oneThesis

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.bmob.R
import com.example.bmob.common.FragmentEventListener
import com.example.bmob.databinding.FragmentStudentThesisBinding
import com.example.bmob.utils.LOG_TAG
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.SetViewModel
import com.example.bmob.viewmodels.StudentThesisViewModel

class StudentThesisFragment : Fragment(),FragmentEventListener {
    private lateinit var binding:FragmentStudentThesisBinding
    private val setViewModel:SetViewModel by activityViewModels()
    private val viewModel:StudentThesisViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =FragmentStudentThesisBinding.inflate(inflater,container,false)
        val student = setViewModel.getUserByQuery().value
        if (student?.studentSelectState!!){
            binding.student = student
            binding.thesis = student.studentThesis
            Log.v(LOG_TAG,"student的thesis：${student.studentThesis}")
        }else{
            showMsg(requireContext(),"您还没有选择课题，点击选择选择课题")
        }
        setViewModel.isSelectTime(setViewModel.getUserByQuery().value!!){ isSelectTime, _ ->
            if (!isSelectTime){
                showMsg(requireContext(),"课题已过期")
                binding.selectThesisBtn.setBackgroundColor(R.color.grey_light)
                binding.selectThesisBtn.isEnabled = false
            }
        }
        setEventListener()
        return binding.root
    }

    override fun setEventListener() {
        binding.outButton.setOnClickListener {
            viewModel.studentOutThesis(setViewModel.getUserByQuery().value!!){isSuccess,student,message ->
                showMsg(requireContext(),message)
                if (isSuccess){
                    setViewModel.setUserByQuery(student!!)
                }
            }
        }

        binding.selectThesisBtn.setOnClickListener {
            findNavController().navigate(R.id.action_studentThesisFragment_to_browseFragment)
        }
    }
}