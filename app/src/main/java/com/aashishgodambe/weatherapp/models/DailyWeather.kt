package com.aashishgodambe.weatherapp.models

import com.squareup.moshi.Json

data class DailyWeather(
        var highTemperature: String?,
        var lowTemperature: String?,
        val iconLink: String?,
        @Json(name = "skyDescription")
        var temperatureDesc: String?,
        var weekday: String?,
        val utcTime: String?
        )
