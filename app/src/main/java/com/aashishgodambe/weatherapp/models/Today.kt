package com.aashishgodambe.weatherapp.models

data class Today(
        var temperature: String?,
        val temperatureDesc: String?,
        var comfort: String?,
        var highTemperature: String?,
        var lowTemperature: String?,
        var humidity: String?,
        var dewPoint: String?,
        var barometerPressure: String?,
        var visibility: String?,
        val iconLink: String?,
        val country: String?,
        val state: String?,
        val city: String?,
        var utcTime: String?
        )
