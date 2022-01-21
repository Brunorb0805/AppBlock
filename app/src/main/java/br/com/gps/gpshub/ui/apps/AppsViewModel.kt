package br.com.gps.gpshub.ui.apps

import androidx.lifecycle.*
import br.com.gps.gpshub.data.Result
import br.com.gps.gpshub.data.local.datasource.apps.AppsRepository
import br.com.gps.gpshub.data.local.datasource.apps.FilterType
import br.com.gps.gpshub.model.entity.Apps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AppsViewModel @Inject constructor(private val repository: AppsRepository) : ViewModel() {

    private val _items: LiveData<List<Apps>> = repository.observeApps().distinctUntilChanged()
        .switchMap { filterApps(it) }// repository.selectApps(SelectCondition.ALL)
    val items: LiveData<List<Apps>> = _items


    fun lockApp(app: Apps, completed: Boolean) = viewModelScope.launch {
        repository.updateLocked(app.packageName, completed)
//        showSnackbarMessage( if (completed) R.string.task_marked_complete else R.string.task_marked_active)
    }

    fun favApp(app: Apps, completed: Boolean) = viewModelScope.launch {
        repository.updateFavourite(app.packageName, completed)
//        showSnackbarMessage( if (completed) R.string.task_marked_complete else R.string.task_marked_active)
    }


    private fun filterApps(appsResult: Result<List<Apps>>): LiveData<List<Apps>> {
        // TODO: This is a good case for liveData builder. Replace when stable.
        val result = MutableLiveData<List<Apps>>()

        if (appsResult is Result.Success) {
//            isDataLoadingError.value = false
            viewModelScope.launch {
//                result.value = ArrayList<Apps>().apply { addAll(appsResult.data) }
                result.value = filterItems(appsResult.data)

            }
        } else {
            result.value = emptyList()
//            showSnackbarMessage(R.string.loading_tasks_error)
//            isDataLoadingError.value = true
        }

        return result
    }

    private fun filterItems(
        listApps: List<Apps>,
        filteringType: FilterType = FilterType.ALL
    ): List<Apps> {
        val appsToShow = ArrayList<Apps>()
        // We filter the tasks based on the requestType
        for (app in listApps) {
            when (filteringType) {
                FilterType.ALL -> {
                    appsToShow.addAll(listApps)
                    break
                }
                FilterType.FAVOURITE -> if (app.isFavouriteApp) {
                    appsToShow.add(app)
                }
                FilterType.DEVICE -> if (app.isDevice) {
                    appsToShow.add(app)
                }
            }
        }
        return appsToShow
    }

}
