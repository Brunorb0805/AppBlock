package br.com.gps.gpshub.ui.home

import androidx.lifecycle.*
import br.com.gps.gpshub.Event
import br.com.gps.gpshub.data.Result
import br.com.gps.gpshub.data.local.datasource.apps.AppsRepository
import br.com.gps.gpshub.model.entity.Apps
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(repository: AppsRepository) : ViewModel() {

    private val _items =
        repository.observeAppsFav().distinctUntilChanged().switchMap { filterApps(it) }
    val items: LiveData<List<Apps>> = _items

    private val _openAppEvent = MutableLiveData<Event<Apps>>()
    val openAppEvent: LiveData<Event<Apps>> = _openAppEvent


    fun openApp(app: Apps) {
        _openAppEvent.value = Event(app)
    }


    private fun filterApps(appsResult: Result<List<Apps>>): LiveData<List<Apps>> {
        val result = MutableLiveData<List<Apps>>()

        if (appsResult is Result.Success) {
//            isDataLoadingError.value = false
            viewModelScope.launch {
                result.value =
                    ArrayList<Apps>().apply {
                        addAll(appsResult.data)
                    } //filterItems(appsResult.data, getSavedFilterType())
            }
        } else {
            result.value = emptyList()
//            showSnackbarMessage(R.string.loading_tasks_error)
//            isDataLoadingError.value = true
        }

        return result
    }

//    private fun getAllApps() {
//        viewModelScope.launch {
//            repository.observeAllApps().map { _items.value = it }
//        }
//    }

}
