package com.aashishgodambe.weatherapp.models

data class HourlyWeather(
        var temperature: String?,
        val iconLink: String?,
        var localTime: String?,
        val localTimeFormat: String,
        var utcTime: String?
        )
