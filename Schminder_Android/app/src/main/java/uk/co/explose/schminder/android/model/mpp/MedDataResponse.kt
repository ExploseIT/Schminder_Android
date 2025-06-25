
package uk.co.explose.schminder.android.model.mpp

import retrofit2.Response
import uk.co.explose.schminder.android.repo.Resource

data class MedDataResponse(
    var mdrIndivList: Resource<Response<MedIndivInfoResponse>> = Resource.Empty(),
    var mdrMed: Resource<List<Med>> = Resource.Empty(),
    var mdrMedScheduled: Resource<List<MedScheduled>> = Resource.Empty()
)

