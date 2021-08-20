package com.ahmed.jazirahwifiapp.ui.admin.panel.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmed.jazirahwifiapp.R
import com.ahmed.jazirahwifiapp.adapters.UserAdapter
import com.ahmed.jazirahwifiapp.databinding.FragmentUsersBinding
import com.ahmed.jazirahwifiapp.model.Permission
import com.ahmed.jazirahwifiapp.utils.Resource

class UsersFragment : Fragment() {
    private val TAG = "UsersFragment"
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding
    private lateinit var usersViewModel: UsersViewModel
    private lateinit var userAdapter: UserAdapter
    private var userCollections: MutableList<Permission>? = mutableListOf<Permission>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        usersViewModel =
            ViewModelProvider(requireActivity()).get(UsersViewModel::class.java)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        usersViewModel
            .queryUsers()
        observer()
    }


    private fun observer() {
        usersViewModel.getUsers().observe(requireActivity(), Observer { response ->
            if (response is Resource.Success) {
                userCollections?.clear()
                val data = response.data as MutableList<Permission>
                userCollections?.addAll(data)
                userAdapter.differ.submitList((userCollections))
            }
        })
    }

    private fun setUpRecyclerView() {
        userAdapter = UserAdapter()
        binding?.rvUser?.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
        userAdapter.setOnItemSelectListener {
            val action =  UsersFragmentDirections.actionNavUserToAppUserPermissionEdit(it)
            Navigation.findNavController(requireActivity().findViewById(R.id.nav_host_fragment_content_admin))
                .navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}