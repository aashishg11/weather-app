package com.aashishgodambe.weatherapp.ui.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.aashishgodambe.weatherapp.ApiStatus
import com.aashishgodambe.weatherapp.R
import com.aashishgodambe.weatherapp.adapters.DailyWeatherAdapter
import com.aashishgodambe.weatherapp.adapters.HourlyWeatherAdapter
import com.aashishgodambe.weatherapp.models.Weather
import com.aashishgodambe.weatherapp.ui.home.HomeViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.loading_dialog.view.*

class SearchFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyWeatherAdapter
    private lateinit var dailyAdapter: DailyWeatherAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        hourlyAdapter = HourlyWeatherAdapter()
        dailyAdapter = DailyWeatherAdapter()

         view.findViewById<RecyclerView>(R.id.rv_hourly_weather).apply {
             adapter = hourlyAdapter
         }
        view.findViewById<RecyclerView>(R.id.rv_daily_weather).apply {
            adapter = dailyAdapter
        }

        searchView = view.findViewById(R.id.search_view)
        val loading = view.findViewById<FrameLayout>(R.id.loading)
        val scrollView = view.findViewById<ScrollView>(R.id.scroll_view_search)

        searchView.apply {
            queryHint = context.getString(R.string.query_hint)
            isActivated = true
            isIconified = false
            clearFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(zipcode: String): Boolean {
                viewModel.getWeather(zipcode)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        viewModel.weather.observe(viewLifecycleOwner, Observer {
            Log.d("SearchFragment",it.today.toString())
            updateViews(it,view)
            it.daily?.let {
                dailyAdapter.data = it
            }
            it.hourly?.let {
                hourlyAdapter.data = it
            }
        })

        viewModel.status.observe(viewLifecycleOwner, Observer {status ->
            when (status) {
                ApiStatus.LOADING -> {
                    loading.tv_info_msg.text = getString(R.string.loading_info_msg)
                    loading.status_image.visibility = View.VISIBLE
                    loading.visibility = View.VISIBLE
                    scrollView.visibility = View.GONE
                }
                ApiStatus.ERROR -> {
                    loading.visibility = View.VISIBLE
                    loading.status_image.setImageResource(R.drawable.ic_connection_error)
                    loading.tv_info_msg.text = getString(R.string.connection_error)

                }
                ApiStatus.DONE -> {
                    scrollView.visibility = View.VISIBLE
                    loading.visibility = View.GONE

                }
                ApiStatus.NOTFOUND -> {
                    scrollView.visibility = View.GONE
                    loading.status_image.visibility = View.GONE
                    loading.tv_info_msg.text = getString(R.string.not_found_info_msg)
                }
            }
        })
        return view
    }

    override fun onResume() {
        super.onResume()
        searchView.clearFocus()
    }

    private fun updateViews(weather: Weather,view: View) {
        val today = weather.today
        today?.let {
            val city_state = today.city + ", " + today.state
            view.findViewById<TextView>(R.id.tv_city).text = city_state
            view.findViewById<TextView>(R.id.tv_date_time).text = today.utcTime
            view.findViewById<TextView>(R.id.tv_day_temp).text = resources.getString(R.string.temp_degree,today.highTemperature)
            view.findViewById<TextView>(R.id.tv_night_temp).text = resources.getString(R.string.temp_degree,today.lowTemperature)
            view.findViewById<TextView>(R.id.tv_temp_now).text = today.temperature
            view.findViewById<TextView>(R.id.tv_feels_temp).text = resources.getString(R.string.temp_degree,today.comfort)
            view.findViewById<TextView>(R.id.tv_temp_desc).text = today.temperatureDesc
            view.findViewById<TextView>(R.id.tv_visibility).text = resources.getString(R.string.visibility,today.visibility)
            view.findViewById<TextView>(R.id.tv_dewpoint).text = resources.getString(R.string.dew_point,today.dewPoint)
            view.findViewById<TextView>(R.id.tv_pressure).text = resources.getString(R.string.pressure,today.barometerPressure)
            view.findViewById<TextView>(R.id.tv_humidity).text = resources.getString(R.string.humidity,today.humidity)
            val tempImg = view.findViewById<ImageView>(R.id.iv_icon)
            Picasso.get().load(today.iconLink)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(tempImg)
        }
    }
}
