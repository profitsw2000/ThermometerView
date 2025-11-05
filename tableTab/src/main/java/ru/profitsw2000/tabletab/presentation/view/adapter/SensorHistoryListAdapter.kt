package ru.profitsw2000.tabletab.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.profitsw2000.core.utils.constants.getLetterFromCode
import ru.profitsw2000.data.model.SensorHistoryDataModel
import ru.profitsw2000.tabletab.databinding.HistoryTableItemViewBinding
import java.text.SimpleDateFormat
import java.util.Locale

class SensorHistoryListAdapter : PagingDataAdapter<SensorHistoryDataModel, SensorHistoryListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = HistoryTableItemViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val historyListModelItem = getItem(position)

        with(holder) {
            serialNumberText.text = serialNumberText.resources.getString(ru.profitsw2000.core.R.string.hex_string, historyListModelItem?.sensorId?.toString(16)?.uppercase(Locale.getDefault()))
            localIdText.text = localIdText.resources.getString(ru.profitsw2000.core.R.string.hex_string, historyListModelItem?.localId?.toString(16)?.uppercase(Locale.getDefault()))
            sensorLetterText.text = getLetterFromCode(historyListModelItem?.letterCode ?: 0)
            dateTimeText.text = SimpleDateFormat("yyyy.MM.dd   HH:mm").format(historyListModelItem?.date)
            temperatureText.text = temperatureText.resources.getString(ru.profitsw2000.core.R.string.sensor_temperature_text, historyListModelItem?.temperature.toString())

            serialNumberText.setTextColor(serialNumberText.resources.getColor(getMainFieldsTextColor(historyListModelItem?.localId)))
            temperatureText.setTextColor(temperatureText.resources.getColor(getMainFieldsTextColor(historyListModelItem?.localId)))
        }
    }

    private fun getMainFieldsTextColor(id: Int?): Int {
        return when(id?.rem(7)) {
            0 -> ru.profitsw2000.core.R.color.green_base
            1 -> ru.profitsw2000.core.R.color.blue_base
            2 -> ru.profitsw2000.core.R.color.magenta_base
            3 -> ru.profitsw2000.core.R.color.red_base
            4 -> ru.profitsw2000.core.R.color.yellow_base
            5 -> ru.profitsw2000.core.R.color.cyan_base
            6 -> ru.profitsw2000.core.R.color.black_base
            else -> ru.profitsw2000.core.R.color.black_base
        }
    }

    companion object {
        val  DIFF_CALLBACK = object : DiffUtil.ItemCallback<SensorHistoryDataModel>() {
            override fun areItemsTheSame(
                oldItem: SensorHistoryDataModel,
                newItem: SensorHistoryDataModel
            ): Boolean {
                return oldItem.sensorId == newItem.sensorId
                        && oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: SensorHistoryDataModel,
                newItem: SensorHistoryDataModel
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(binding: HistoryTableItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val serialNumberText = binding.serialNumberTextView
        val localIdText = binding.localIdTextView
        val sensorLetterText = binding.sensorLetterTextView
        val dateTimeText = binding.dateTimeTextView
        val temperatureText = binding.temperatureValueTextView
    }
}