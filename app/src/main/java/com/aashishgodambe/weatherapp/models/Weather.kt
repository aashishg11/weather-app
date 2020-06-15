package com.aashishgodambe.weatherapp.models

data class Weather(
        val today: Today?,
        val hourly: List<HourlyWeather>?,
        val daily: List<DailyWeather>?
        )
