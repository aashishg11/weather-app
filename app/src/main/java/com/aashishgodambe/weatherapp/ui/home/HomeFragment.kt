package com.aashishgodambe.weatherapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.aashishgodambe.weatherapp.ApiStatus
import com.aashishgodambe.weatherapp.adapters.HourlyWeatherAdapter
import com.aashishgodambe.weatherapp.models.Weather
import com.squareup.picasso.Picasso
import com.aashishgodambe.weatherapp.R
import com.aashishgodambe.weatherapp.adapters.DailyWeatherAdapter
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.loading_dialog.view.*
import java.util.*


private const val PERMISSION_REQUEST = 10

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var hourlyAdapter: HourlyWeatherAdapter
    private lateinit var dailyAdapter: DailyWeatherAdapter
    private var locationPermissionsGranted = false
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        hourlyAdapter = HourlyWeatherAdapter()
        dailyAdapter = DailyWeatherAdapter()

        if(!viewModel.isDataLoaded()){
            getLocationPermission()
        }

        view.findViewById<RecyclerView>(R.id.rv_hourly_weather).apply {
            adapter = hourlyAdapter
        }
        view.findViewById<RecyclerView>(R.id.rv_daily_weather).apply {
            adapter = dailyAdapter
        }

        viewModel.weather.observe(viewLifecycleOwner, Observer {
            Log.d("HomeFragment", it.today.toString())
            updateViews(it, view)
            hourlyAdapter.data = it.hourly!!
            dailyAdapter.data = it.daily!!
        })

        val loading = view.findViewById<FrameLayout>(R.id.loading)
        val scrollView = view.findViewById<ScrollView>(R.id.scroll_view)
        viewModel.status.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                ApiStatus.LOADING -> {
                    loading.tv_info_msg.text = getString(R.string.loading_info_msg)
                    loading.visibility = View.VISIBLE
                    scrollView.visibility = View.GONE
                }
                ApiStatus.ERROR -> {
                    loading.visibility = View.VISIBLE
                    loading.status_image.setImageResource(R.drawable.ic_connection_error)
                    loading.tv_info_msg.text = getString(R.string.connection_error)
                }
                ApiStatus.DONE -> {
                    loading.visibility = View.GONE
                    scrollView.visibility = View.VISIBLE
                }
                ApiStatus.NOTFOUND -> {
                    scrollView.visibility = View.GONE
                    loading.status_image.visibility = View.GONE
                    loading.tv_info_msg.text = getString(R.string.not_found_info_msg)
                }
            }
        })
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    loading.tv_info_msg.text = getString(R.string.permission_decline_info_msg)
                    loading.status_image.visibility = View.GONE
                }
            }
            if (allSuccess) {
                getLocation()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionsGranted = true
            getLocation()
        } else {
            requestPermissions(
                permissions, PERMISSION_REQUEST
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 1000
        locationRequest.numUpdates = 1

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            getZipcode(locationResult.lastLocation)
        }
    }

    private fun getZipcode(location: Location){
        val geoCoder = Geocoder(context, Locale.getDefault())
        val address = geoCoder.getFromLocation(location.latitude,location.longitude,1)[0]
        val zipcode = address.postalCode
        viewModel.getWeather(zipcode)
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    private fun updateViews(weather: Weather, view: View) {
        val today = weather.today
        today?.let {
            val city_state = today.city + ", " + today.state
            view.findViewById<TextView>(R.id.tv_city).text = city_state
            view.findViewById<TextView>(R.id.tv_date_time).text = today.utcTime
            view.findViewById<TextView>(R.id.tv_day_temp).text =
                resources.getString(R.string.temp_degree, today.highTemperature)
            view.findViewById<TextView>(R.id.tv_night_temp).text =
                resources.getString(R.string.temp_degree, today.lowTemperature)
            view.findViewById<TextView>(R.id.tv_temp_now).text = today.temperature
            view.findViewById<TextView>(R.id.tv_feels_temp).text =
                resources.getString(R.string.temp_degree, today.comfort)
            view.findViewById<TextView>(R.id.tv_temp_desc).text = today.temperatureDesc
            view.findViewById<TextView>(R.id.tv_visibility).text =
                resources.getString(R.string.visibility, today.visibility)
            view.findViewById<TextView>(R.id.tv_dewpoint).text =
                resources.getString(R.string.dew_point, today.dewPoint)
            view.findViewById<TextView>(R.id.tv_pressure).text =
                resources.getString(R.string.pressure, today.barometerPressure)
            view.findViewById<TextView>(R.id.tv_humidity).text =
                resources.getString(R.string.humidity, today.humidity)
            val tempImg = view.findViewById<ImageView>(R.id.iv_icon)
            Picasso.get().load(today.iconLink)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .into(tempImg)
        }
    }
}
