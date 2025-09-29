package ru.profitsw2000.memoryfragment.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.memoryfragment.R
import ru.profitsw2000.memoryfragment.databinding.FragmentMemoryBinding
import ru.profitsw2000.memoryfragment.presentation.viewmodel.MemoryViewModel

class MemoryFragment : Fragment() {

    private var _binding: FragmentMemoryBinding? = null
    private val binding get() = _binding!!
    private val memoryViewModel: MemoryViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMemoryBinding.bind(inflater.inflate(R.layout.fragment_memory, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.clearMemoryButton.setOnClickListener {
            memoryViewModel.clearMemory()
        }
    }
}