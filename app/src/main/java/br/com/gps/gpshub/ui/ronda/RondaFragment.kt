package br.com.gps.gpshub.ui.ronda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import br.com.gps.gpshub.databinding.FragmentRondaBinding
import br.com.gps.gpshub.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RondaFragment : Fragment() {

    private val rondaViewModel by viewModels<RondaViewModel>()
//    private lateinit var rondaViewModel: RondaViewModel


    private var _binding: FragmentRondaBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        rondaViewModel =
//            ViewModelProvider(this)[RondaViewModel::class.java]

        _binding = FragmentRondaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRonda
        rondaViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}