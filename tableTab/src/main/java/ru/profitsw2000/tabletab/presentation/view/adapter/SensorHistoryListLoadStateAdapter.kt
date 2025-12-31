package ru.profitsw2000.tabletab.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.profitsw2000.tabletab.databinding.HistoryTableLoadStateItemViewBinding

class SensorHistoryListLoadStateAdapter :
    LoadStateAdapter<SensorHistoryListLoadStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState
    ) {
        holder.binding.apply {
            pageLoadProgressBar.isVisible = loadState is LoadState.Loading
            loadErrorTextView.isVisible = loadState is LoadState.Error
            loadErrorTextView.text = if (loadState is LoadState.Error) loadErrorTextView.resources.getString(
                ru.profitsw2000.core.R.string.history_list_page_load_error_text,
                loadState.error.message
            ) else  loadErrorTextView.resources.getString(
                ru.profitsw2000.core.R.string.history_list_page_load_error_text,
                ""
            )
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadStateViewHolder {
        return LoadStateViewHolder(
            HistoryTableLoadStateItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class LoadStateViewHolder(val binding: HistoryTableLoadStateItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

}