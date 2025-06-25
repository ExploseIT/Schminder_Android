

package uk.co.explose.schminder.android.repo

sealed class Resource<T> {
    class Empty<T> : Resource<T>()
    class Loading<T> : Resource<T>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val exception: Exception? = null) : Resource<T>()

    /*
    fun isSuccess(): Boolean = data != null && error == null
    fun getData(): T? = data
    fun getErrorMessage(): String? = error ?: exception?.localizedMessage
    fun getException(): Exception? = exception
     */
}

