package uk.co.explose.schminder.android.core

suspend fun <T : HasIntId> insertAndReturn(daoInsert: suspend (T) -> Long, entity: T): T {
    val newId = daoInsert(entity)
    return entity.copyWithId(newId.toInt()) as T
}

