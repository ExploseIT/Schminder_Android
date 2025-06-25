package uk.co.explose.schminder.android.core

data class ApiResponse<T> (
    var apiSuccess: Boolean,
    var apiMessage: String,
    var apiData: T
    )

