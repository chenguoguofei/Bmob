package com.example.bmob.fragments.login.findPassword

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.bmob.v3.BmobUser
import com.example.bmob.R
import com.example.bmob.databinding.FragmentUsernameBinding
import com.example.bmob.utils.showMsg
import com.example.bmob.viewmodels.BmobUserViewModel

class UsernameFragment : Fragment() {
    private lateinit var binding:FragmentUsernameBinding
    private val userViewMode:BmobUserViewModel by activityViewModels()
    private var username = EMPTY_USERNAME
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsernameBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userViewMode.isLogin()){
            username = BmobUser.getCurrentUser().username.toString()
            binding.phoneNumber = BmobUser.getCurrentUser().username.toString()
        }
        binding.nextBtn.setOnClickListener {
            val text = binding.editText.text
            if (TextUtils.isEmpty(text)){
                showMsg(requireContext(),"请输入账号")
            }else{
                val navDirections =
                    UsernameFragmentDirections.actionUsernameFragmentToPhoneNumberFragment(text.toString())
                findNavController().navigate(navDirections)
            }
        }
    }
}

private const val EMPTY_USERNAME = ""