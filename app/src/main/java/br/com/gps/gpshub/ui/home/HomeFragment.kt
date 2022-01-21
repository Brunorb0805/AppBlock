package br.com.gps.gpshub.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import br.com.gps.gpshub.EventObserver
import br.com.gps.gpshub.R
import br.com.gps.gpshub.databinding.FragmentHomeBinding
import br.com.gps.gpshub.extensions.navTo
import br.com.gps.gpshub.extensions.toast
import br.com.gps.gpshub.extensions.viewBinding
import br.com.gps.gpshub.ui.apps.AppsListAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModels()
    private val binding by viewBinding(FragmentHomeBinding::bind)

//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!

    private lateinit var listAdapter: HomeListAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewmodel = homeViewModel
            lifecycleOwner = this@HomeFragment.viewLifecycleOwner
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {}

        setHasOptionsMenu(true)

        setup()
    }

    /** Exemplo de fragmento em tela sem actionbar */
//    override fun onResume() {
//        super.onResume()
////        (activity as AppCompatActivity).supportActionBar?.hide()
//    }

//    override fun onStop() {
//        super.onStop()
//        (activity as AppCompatActivity).supportActionBar?.show()
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            navTo(R.id.action_homeFragment_to_appsFragment)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setup() {
        setupList()
        setupObservers()
    }

    private fun setupList() {
        binding.viewmodel?.let {
            listAdapter = HomeListAdapter(it)
            binding.favouriteAppsList.adapter = listAdapter
        } ?: {
//            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupObservers() {
        homeViewModel.openAppEvent.observe(viewLifecycleOwner, EventObserver {
            toast("Abrir App: ${it.appName}")
        })
    }
}
