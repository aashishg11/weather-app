package com.aashishgodambe.weatherapp

import android.util.Log
import com.aashishgodambe.weatherapp.models.Weather
import com.aashishgodambe.weatherapp.models.WeatherRequest
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.roundToInt


class WeatherRepository {

    suspend fun getWeather(zipcode: String) : Weather{
        val weather = WeatherApi.retrofitService.getWeather(WeatherRequest(zipcode))

        weather.today?.let {today ->
            today.utcTime = getDate(today.utcTime!!)
            today.highTemperature = today.highTemperature?.toDouble()?.roundToInt().toString()
            today.lowTemperature = today.lowTemperature?.toDouble()?.roundToInt().toString()
            today.temperature = today.temperature?.toDouble()?.roundToInt().toString()
            today.comfort = today.comfort?.toDouble()?.roundToInt().toString()
            today.humidity = today.humidity?.toDouble()?.roundToInt().toString()
            today.visibility = today.visibility?.toDouble()?.roundToInt().toString()
            today.dewPoint = today.dewPoint?.toDouble()?.roundToInt().toString()
            today.barometerPressure = today.barometerPressure?.toDouble()?.roundToInt().toString()
        }

        if(!weather.hourly.isNullOrEmpty()){
            for (hourly in weather.hourly){
                hourly.localTime = hourly.localTime?.toDate(hourly.localTimeFormat,TimeZone.getDefault())?.formatTo("hh a")
                hourly.temperature = hourly.temperature?.toDouble()?.roundToInt().toString()
            }
        }

        if(!weather.daily.isNullOrEmpty()){
            for (i in weather.daily.indices){
                val date = getDate(weather.daily[i].utcTime!!)
                val day = weather.daily[i].weekday!!
                weather.daily[i].weekday = "$day, $date"
                weather.daily[i].highTemperature = weather.daily[i].highTemperature?.toDouble()?.roundToInt().toString()
                weather.daily[i].lowTemperature = weather.daily[i].lowTemperature?.toDouble()?.roundToInt().toString()
            }
        }

        return weather
    }

    fun getDate(utcTime: String): String{
        val odt = OffsetDateTime.parse(utcTime)
        val month = odt.month.name.toLowerCase().capitalize()
        val formatted = "$month ${odt.dayOfMonth}"
        Log.d("Date",formatted)
        return formatted
    }

    fun String.toDate(dateFormat: String, timeZone: TimeZone): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

}