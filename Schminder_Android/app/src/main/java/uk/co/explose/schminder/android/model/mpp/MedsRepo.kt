
package uk.co.explose.schminder.android.model.mpp

import android.content.Context

import uk.co.explose.schminder.android.network.RetrofitClient

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response
import uk.co.explose.schminder.android.core.AppDb
import uk.co.explose.schminder.android.core.insertAndReturn
import uk.co.explose.schminder.android.mapper.MedScheduledDisplayItem
import uk.co.explose.schminder.android.model.profile.UserProfileResponse
import uk.co.explose.schminder.android.model.user.UserRepo

import uk.co.explose.schminder.android.repo.RepositoryBase
import uk.co.explose.schminder.android.repo.Resource
import uk.co.explose.schminder.android.utils.dtObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.text.insert
import kotlin.toString


object MedsRepo : RepositoryBase<MedIndivInfoResponse> {
    private val _resp = MutableStateFlow<Resource<MedIndivInfoResponse>>(Resource.Empty())
    override val data: StateFlow<Resource<MedIndivInfoResponse>> = _resp

    override suspend fun loadData(context: Context): Resource<MedIndivInfoResponse> {
        _resp.value = Resource.Loading()

        return runCatching {
            val response = RetrofitClient.api.getMedsIndivList()

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null && responseBody.apiSuccess) {
                    Resource.Success(responseBody)
                } else {
                    Resource.Error(responseBody?.apiMessage ?: "Unknown server error")
                }
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Server Error"
                Resource.Error(errorMessage)
            }
        }.getOrElse { e ->
            val exception = if (e is Exception) e else Exception(e.message ?: "Unknown error")
            Resource.Error("Network error: Please check your internet connection.", exception)
        }.also {
            _resp.value = it
        }
    }

    override fun setCachedData(newRes: Resource<MedIndivInfoResponse>): Resource<MedIndivInfoResponse>? {
        _resp.value = newRes
        return _resp.value
    }

    suspend fun readMedScheduledDisplayItemById(context: Context, medId: Int): MedScheduledDisplayItem? {
        val scheduled = AppDb.getInstance(context).medsScheduledDao().readById(medId) ?: return null
        val med = AppDb.getInstance(context).medsDao().getMedById(scheduled.medIdSchedule) ?: return null
        return scheduled.toMedScheduledDisplayItem(med)
    }

    suspend fun readMedScheduledDisplayItemNextByDT(context: Context, after: LocalDateTime, skipMedId: Int): MedScheduledDisplayItem? {
        val nextScheduled = AppDb.getInstance(context).medsScheduledDao()
            .getNextScheduledAfterExcluding(after, skipMedId)
        val nextMed = nextScheduled?.let {
            AppDb.getInstance(context).medsDao().getMedById(it.medIdSchedule)
        }
        return if (nextScheduled != null && nextMed != null)
            nextScheduled.toMedScheduledDisplayItem(nextMed)
        else null
    }




    suspend fun doMedIndivActionTx(medIndivInfo: MedIndivActionTx): MedIndivActionRx? {
        return try {
            val response = RetrofitClient.api.doMedIndivActionTx(medIndivInfo)
            if (response.isSuccessful) {
                response.body()
            } else {
                null // handle error (optional: log or throw)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun doMedSearch(med_search: med_search_tx): medInfo? {
        return try {
            val response = RetrofitClient.api.doMedsSearch(med_search)
            if (response.isSuccessful) {
                response.body()
            } else {
                null // handle error (optional: log or throw)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun convertIndivToMeds(medsIndiv: List<MedIndiv>): List<Med> {
        return medsIndiv.map { indiv ->
            Med(
                medName = indiv.medName,
                medInfo = "",
                medScheduled = false,
                medPid = 0,
                medId = 0,
                medDateStart = LocalDate.now()
            )
        }
    }

    suspend fun convertIndivDtoToIndiv(medsIndiv: List<MedIndivDto>): List<MedIndiv> {
        return medsIndiv.map { indiv ->
            MedIndiv(
                medName = indiv.medName,
                medDTEntered = dtObject().dtoNow
            )
        }
    }

    private suspend fun loadMedsScheduled(context: Context): List<MedScheduledWithMed> {
        return AppDb.getInstance(context).medsScheduledDao().listAll()
    }

    suspend fun insertMedIntoMedScheduled(context: Context, med: Med, today: LocalDate, bInsert: Boolean) : MedScheduled {
        val dao = AppDb.getInstance(context).medsScheduledDao()
        var newEntry = MedScheduled(
            medPid = med.medPid,
            medIdSchedule = med.medId,
            medName = med.medName,
            medInfo = med.medInfo,
            medDTSchedule = today.atTime(med.medTimeofday),
            medDTTaken = null,
            medDTNotifyLast = null
        )
        if (bInsert) {
            newEntry = insertAndReturn(dao::insert, newEntry)
        }
        return newEntry
    }

    suspend fun loadMedsAndSchedule(context: Context, lMeds: List<Med>, today: LocalDate, bInsert: Boolean) : List<MedScheduled> {
        var ret = mutableListOf<MedScheduled>()
        lMeds.forEach { med ->
            var medScheduled = AppDb.getInstance(context).medsScheduledDao().exists(med.medId, today.atTime(med.medTimeofday))
            if (medScheduled == null) {
                medScheduled = insertMedIntoMedScheduled(context, med, today, bInsert)
            }
            ret.add(medScheduled)
        }
        return ret
    }

    suspend fun loadMedsSchedule(context: Context): List<Med> {
        return AppDb.getInstance(context).medsDao().medListAll()
    }

    suspend fun saveMedLastNotified(context: Context, med: MedScheduledDisplayItem) {
        if (med.medId > 0) {
            AppDb.getInstance(context).medsScheduledDao()
                .updateNotifyLastById(med.medId, med.medDTNotifyLast!!)
        }
    }

    suspend fun readMedById(context: Context, medId: Int) {
        if (medId > 0) {
            AppDb.getInstance(context).medsScheduledDao()
                .readById(medId)
        }
    }

    suspend fun medIndivMedListAll(context: Context): List<MedIndivMed> {
        return AppDb.getInstance(context).medsIndivDao().MedIndivMedListAll()
    }

    suspend fun medIndivListAll(context: Context): List<MedIndiv> {
        return AppDb.getInstance(context).medsIndivDao().medIndivList()
    }

    suspend fun medIndivDtoListAll(context: Context): List<MedIndivDto> {
        return AppDb.getInstance(context).medsIndivDao().medIndivList()
            .map { MedIndivDto(medName = it.medName) }
    }

    suspend fun medIndivDtoInsertAll(context: Context, medsIndiv: List<MedIndivDto>): List<Long> {
        val meds = convertIndivDtoToIndiv(medsIndiv)
        return AppDb.getInstance(context).medsIndivDao().medIndivInsertAll(meds)
    }

    suspend fun medIndivInsertAll(context: Context, meds: List<MedIndiv>): List<Long> {
        return AppDb.getInstance(context).medsIndivDao().medIndivInsertAll(meds)
    }

    suspend fun medIndivDeleteByName(context: Context, medName: String) {
        return AppDb.getInstance(context).medsIndivDao().medDeleteByName(medName)
    }

    suspend fun medListAll(context: Context): List<Med> {
        return AppDb.getInstance(context).medsDao().medListAll()
    }

    suspend fun medInsert(context: Context, med: Med): Long {
        return AppDb.getInstance(context).medsDao().medInsert(med)
    }

    suspend fun medReadById(context: Context, id: Int): Med? {
        return AppDb.getInstance(context).medsDao().getMedById(id)
    }


    suspend fun getScheduledForDay(context: Context, meds: List<Med>, day: LocalDate): List<MedScheduledDisplayItem> {
        val dao = AppDb.getInstance(context).medsScheduledDao()
        val today = LocalDate.now()
        val result = mutableListOf<MedScheduledDisplayItem>()

        for (med in meds.sortedBy { it.getTod() }) {
            val dt = day.atTime(med.medTimeofday)

            if ((day <= today && med.medRepeatType != MedRepeatTypeEnum.Now)
                || (day == today && med.medRepeatType == MedRepeatTypeEnum.Now)) {

                var scheduled = dao.exists(med.medId, dt)
                if (scheduled == null) {
                    scheduled = dao.insertAndReturn(
                        MedScheduled(
                            medPid = med.medPid,
                            medIdSchedule = med.medId,
                            medName = med.medName,
                            medInfo = med.medInfo,
                            medDTSchedule = dt
                        )
                    )
                }
                result.add(createDisplayItemFrom(med, scheduled, dt))
            } else {
                result.add(createDisplayItemFrom(med, null, dt))
            }
        }

        return result
    }

    suspend fun markMedicationAsTaken(context: Context, medId: Int) {
        val dao = AppDb.getInstance(context).medsScheduledDao()
        val now = dtObject().dtoNow.withSecond(0).withNano(0)
        dao.updateDTTakenById(medId, now)
    }

    suspend fun getNextMedScheduledDisplayItemFrom(
        context: Context,
        allMeds: List<Med>,
        currentMed: MedScheduledDisplayItem,
        day: LocalDate
    ): MedScheduledDisplayItem? {
        val dao = AppDb.getInstance(context).medsScheduledDao()

        val sortedMeds = allMeds.sortedBy { it.medTimeofday }

        val nextToday = sortedMeds.firstOrNull { it.medTimeofday > currentMed.medTimeOfDay }
        val nextMed = nextToday ?: sortedMeds.firstOrNull() ?: return null
        val targetDay = if (nextToday != null) day else day.plusDays(1)

        val dt = targetDay.atTime(nextMed.medTimeofday)

        var scheduled = dao.exists(nextMed.medId, dt)
        if (scheduled == null) {
            scheduled = dao.insertAndReturn(
                MedScheduled(
                    medPid = nextMed.medPid,
                    medIdSchedule = nextMed.medId,
                    medName = nextMed.medName,
                    medInfo = nextMed.medInfo,
                    medDTSchedule = dt
                )
            )
        }

        return createDisplayItemFrom(nextMed, scheduled, dt)
    }

    private fun createDisplayItemFrom(
        med: Med,
        scheduled: MedScheduled?,
        dt: LocalDateTime
    ): MedScheduledDisplayItem {
        val derivedTod = if (med.medRepeatType == MedRepeatTypeEnum.Now)
            dtObject().dtoTod.withSecond(0).withNano(0)
        else
            med.medTimeofday

        val derivedDT = if (med.medRepeatType == MedRepeatTypeEnum.Now)
            dtObject().dtoNow.withSecond(0).withNano(0)
        else
            dt

        return MedScheduledDisplayItem(
            medId = scheduled?.medId ?: 0,
            medPid = med.medPid,
            medName = scheduled?.medName ?: med.medName,
            medInfo = scheduled?.medInfo ?: med.medInfo,
            medDTSchedule = scheduled?.medDTSchedule ?: dt,
            medDTTaken = scheduled?.medDTTaken,
            medRepeatType = med.medRepeatType,
            medRepeatCount = med.medRepeatCount,
            medRepeatInterval = med.medRepeatInterval,
            medTimeOfDay = med.medTimeofday,
            medDTNotifyLast = scheduled?.medDTNotifyLast,
            medDTDerived = derivedDT,
            medTodDerived = derivedTod
        )
    }

    override fun getCachedData(): Resource<MedIndivInfoResponse> {
        return _resp.value
    }

    override suspend fun refreshData(context: Context): Resource<MedIndivInfoResponse> {
        TODO("Not yet implemented")
    }

}
