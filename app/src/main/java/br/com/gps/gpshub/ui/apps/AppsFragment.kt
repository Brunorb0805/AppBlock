package br.com.gps.gpshub.ui.apps

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import br.com.gps.gpshub.R
import br.com.gps.gpshub.databinding.FragmentAppsBinding
import br.com.gps.gpshub.extensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class AppsFragment : Fragment(R.layout.fragment_apps) {

    private val appsViewModel: AppsViewModel by viewModels()
    private val binding by viewBinding(FragmentAppsBinding::bind)

    @Inject
    lateinit var packageManager: PackageManager

    private lateinit var listAdapter: AppsListAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewmodel = appsViewModel
            lifecycleOwner = this@AppsFragment.viewLifecycleOwner
        }

        setup()
    }


    private fun setup() {
        setupList()
    }

    private fun setupList() {
        binding.viewmodel?.let {
            listAdapter = AppsListAdapter(it)
            binding.allAppsList.adapter = listAdapter
        } ?: {
//            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

}
