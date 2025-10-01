package ru.profitsw2000.memoryfragment.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.data.model.state.MemoryScreenState
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
        observeData()
    }

    private fun initViews() {
        binding.clearMemoryButton.setOnClickListener {
            memoryViewModel.clearMemory(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun observeData() {
        val observer = Observer<MemoryScreenState> { renderData(it) }
        memoryViewModel.memoryInfoLiveData.observe(viewLifecycleOwner, observer)
    }

    private fun renderData(memoryScreenState: MemoryScreenState) {
        when(memoryScreenState) {
            MemoryScreenState.Blank -> memoryViewModel.getMemoryInfo(viewLifecycleOwner.lifecycleScope)
            is MemoryScreenState.Error -> TODO()
            MemoryScreenState.MemoryClearExecution -> TODO()
            MemoryScreenState.MemoryClearSuccess -> TODO()
            is MemoryScreenState.MemoryDataAnswer -> TODO()
            is MemoryScreenState.MemoryDataRequest -> TODO()
            MemoryScreenState.MemoryDataSuccess -> TODO()
            MemoryScreenState.MemoryInfoLoad -> TODO()
            is MemoryScreenState.MemoryInfoSuccess -> TODO()
            is MemoryScreenState.ServiceDataAnswer -> TODO()
            is MemoryScreenState.ServiceDataRequest -> TODO()
            MemoryScreenState.TimeoutError -> TODO()
        }
    }
}