
package com.aashishgodambe.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aashishgodambe.weatherapp.R
import com.aashishgodambe.weatherapp.models.DailyWeather
import com.aashishgodambe.weatherapp.models.HourlyWeather
import com.squareup.picasso.Picasso

class DailyWeatherAdapter:
    RecyclerView.Adapter<DailyWeatherAdapter.HourlyViewHolder>() {

    var data = listOf<DailyWeather>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = data[position]
        val imageUrl = item.iconLink
        imageUrl?.let {
            Picasso.get().load(imageUrl)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(holder.icon)
        }
        holder.tempHigh.text = holder.tempHigh.resources.getString(R.string.temp_degree,item.highTemperature)
        holder.tempLow.text = holder.tempLow.resources.getString(R.string.temp_degree,item.lowTemperature)
        holder.tempDesc.text = item.temperatureDesc
        holder.date.text = item.weekday
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class HourlyViewHolder(itemView: View):
            RecyclerView.ViewHolder(itemView) {
        val tempHigh: TextView = itemView.findViewById(R.id.tv_daily_high_temp)
        val tempLow: TextView = itemView.findViewById(R.id.tv_daily_low_temp)
        val icon: ImageView = itemView.findViewById(R.id.iv_daily_icon)
        val tempDesc: TextView = itemView.findViewById(R.id.tv_daily_weather)
        val date: TextView = itemView.findViewById(R.id.tv_daily_day_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_daily, parent, false)
        return HourlyViewHolder(view)
    }
}
