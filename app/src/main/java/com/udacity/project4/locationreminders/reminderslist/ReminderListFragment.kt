package com.udacity.project4.locationreminders.reminderslist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.authentication.LoginViewModel
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import com.udacity.project4.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    override val _viewModel: RemindersListViewModel by viewModel()
    private lateinit var binding: FragmentRemindersBinding
    private val viewModel by viewModels<LoginViewModel>()
    private val runningQor = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_reminders, container, false
        )
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { _viewModel.getReminders() }

        return binding.root
    }//end onCreateView()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()
        binding.addReminderFAB.setOnClickListener {
            navigateToAddReminder()
        }//end setOnClickListener
    }//end onViewCreated()

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        _viewModel.getReminders()
    }//end onResume()

    private fun navigateToAddReminder() {
        //use the navigationCommand live data to navigate between the fragments
        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }//end navigateToAddReminder()

    private fun navigateBack() {
        val intent = Intent(activity, AuthenticationActivity::class.java)
        startActivity(intent)
    }//end navigateBack()

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {}

//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }//end setup()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }//end onCreateOptionsMenu()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                // adding layout impelementation
                viewModel.authenticationState.observe(viewLifecycleOwner,
                    Observer { authenticationState ->
                        when (authenticationState) {
                            LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                                AuthUI.getInstance().signOut(requireContext())
                                navigateBack()
                            }
                            else -> {}
                        }
                    })
            }
        }//end when()
        return super.onOptionsItemSelected(item)
    }//end onOptionsItemSelected()
}//end class
