

package com.aashishgodambe.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aashishgodambe.weatherapp.R
import com.aashishgodambe.weatherapp.models.HourlyWeather
import com.squareup.picasso.Picasso

class HourlyWeatherAdapter :
    RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>() {

    var data = listOf<HourlyWeather>()
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
        holder.time.text = item.localTime
        holder.temp.text = holder.temp.resources.getString(R.string.temp_degree,item.temperature)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class HourlyViewHolder(itemView: View):
            RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.tv_hourly_time)
        val icon: ImageView = itemView.findViewById(R.id.iv_hourly_icon)
        val temp: TextView = itemView.findViewById(R.id.tv_hourly_temp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_hourly, parent, false)
        return HourlyViewHolder(view)
    }
}
