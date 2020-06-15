package com.aashishgodambe.weatherapp.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aashishgodambe.weatherapp.ApiStatus
import com.aashishgodambe.weatherapp.WeatherRepository
import com.aashishgodambe.weatherapp.models.Weather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

class SearchViewModel : ViewModel() {

    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _weather = MutableLiveData<Weather>()
    val weather: LiveData<Weather>
        get() = _weather

    private var viewModelJob = Job()

    val repository: WeatherRepository = WeatherRepository()

    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun getWeather(zipCode: String) {
        coroutineScope.launch {
            try {
                _status.value = ApiStatus.LOADING
                val weather = repository.getWeather(zipCode)
                if (weather.today != null){
                    _weather.postValue(weather)
                    _status.value = ApiStatus.DONE
                }else{
                    _status.value = ApiStatus.NOTFOUND
                }
            }catch (e: Exception){
                Log.e("HomeViewmodel",e.toString())
                _status.value = ApiStatus.ERROR
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
