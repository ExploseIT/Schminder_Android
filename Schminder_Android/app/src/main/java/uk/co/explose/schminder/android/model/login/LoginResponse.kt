package uk.co.explose.schminder.android.model.login

data class LoginResponse(
    val token: String,
    val expiresIn: Int // or Long depending on backend
)
