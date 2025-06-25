package uk.co.explose.schminder.android.core

import android.content.Context
import android.widget.Toast

class AppToast(private val context: Context)  {

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}