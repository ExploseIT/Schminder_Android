package uk.co.explose.schminder.android.core

interface HasIntId {
    val id: Int
    fun copyWithId(newId: Int): HasIntId
}
