
package uk.co.explose.schminder.android.repo

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface RepositoryBase<T> {
    val data: StateFlow<Resource<T>?>

    suspend fun loadData(context: Context): Resource<T>
    fun getCachedData(): Resource<T>?
    fun setCachedData(newRes: Resource<T>): Resource<T>?
    suspend fun refreshData(context: Context): Resource<T>
}
