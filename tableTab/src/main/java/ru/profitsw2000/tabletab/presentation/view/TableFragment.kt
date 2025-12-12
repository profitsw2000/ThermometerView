package ru.profitsw2000.tabletab.presentation.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.profitsw2000.core.utils.constants.TAG
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.navigator.Navigator
import ru.profitsw2000.tabletab.R
import ru.profitsw2000.tabletab.databinding.FragmentTableBinding
import ru.profitsw2000.tabletab.presentation.view.adapter.SensorHistoryListAdapter
import ru.profitsw2000.tabletab.presentation.view.adapter.SensorHistoryListLoadStateAdapter
import ru.profitsw2000.tabletab.presentation.viewmodel.TableViewModel
import kotlin.getValue

class TableFragment : Fragment() {

    private var _binding: FragmentTableBinding? = null
    private val binding
        get() = _binding!!
    private val tableViewModel: TableViewModel by viewModel()
    private val navigator: Navigator by inject()
    private val adapter by lazy {
        SensorHistoryListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.table_tab_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.time_period_selection -> {
                true
            }
            R.id.filter_history -> {
                navigator.navigateToFilterHistoryListFragment()
                true
            }
            R.id.sort_history -> {
                navigator.navigateToTableOrderBottomSheet()
                true
            }
            else -> true
        }
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