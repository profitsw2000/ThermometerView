package ru.profitsw2000.mainfragment.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.profitsw2000.data.model.SensorModel
import ru.profitsw2000.mainfragment.databinding.SensorsTemperatureRvItemBinding

class SensorsTemperatureListAdapter : RecyclerView.Adapter<SensorsTemperatureListAdapter.ViewHolder>() {

    private var data: List<SensorModel> = arrayListOf()

    fun setData(data: List<SensorModel>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SensorsTemperatureListAdapter.ViewHolder {
        val binding = SensorsTemperatureRvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SensorsTemperatureListAdapter.ViewHolder, position: Int) {
        val sensorModel = data[position]

        holder.sensorId.text = sensorModel.sensorId.toString()
        holder.sensorLetter.text = sensorModel.sensorLetter
        holder.sensorTemperature.text = sensorModel.sensorTemperature.toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(binding: SensorsTemperatureRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val sensorId = binding.sensorIdTextView
        val sensorLetter = binding.sensorLetterTextView
        val sensorTemperature = binding.sensorTemperatureTextView
    }
}