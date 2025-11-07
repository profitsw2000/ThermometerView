package ru.profitsw2000.tabletab.presentation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.tabletab.R
import ru.profitsw2000.tabletab.databinding.FragmentTableBinding
import ru.profitsw2000.tabletab.presentation.view.adapter.SensorHistoryListAdapter
import ru.profitsw2000.tabletab.presentation.view.adapter.SensorHistoryListLoadStateAdapter
import ru.profitsw2000.tabletab.presentation.viewmodel.TableViewModel

class TableFragment : Fragment() {

    private var _binding: FragmentTableBinding? = null
    private val binding
        get() = _binding!!
    private val tableViewModel: TableViewModel by viewModel()
    private val adapter by lazy {
        SensorHistoryListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTableBinding.bind(inflater.inflate(R.layout.fragment_table, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeData()
    }

    private fun initViews() = with(binding) {
        temperatureHistoryListRecyclerView.adapter = adapter.withLoadStateFooter(
            SensorHistoryListLoadStateAdapter()
        )
    }

    private fun observeData() {
        val observer = Observer<PagingData<SensorHistoryDataModel>> { submitDataToAdapter(it) }
        tableViewModel.historyListPagedData.observe(viewLifecycleOwner, observer)
    }

    private fun submitDataToAdapter(pagingData: PagingData<SensorHistoryDataModel>) {
        lifecycleScope.launch {
            adapter.submitData(pagingData)
        }
    }
}