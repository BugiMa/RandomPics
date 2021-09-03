package com.example.randompics.ui.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.example.randompics.RandomPicsApplication
import com.example.randompics.data.remote.Pictures
import com.example.randompics.util.Constants.Companion.PIC_LIMIT
import com.example.randompics.util.Resource
import com.example.randompics.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import android.os.Build
import com.example.randompics.repo.IPicturesRepository
import com.example.randompics.util.Constants.Companion.CONVERSION_ERROR
import com.example.randompics.util.Constants.Companion.NETWORK_FAILURE
import com.example.randompics.util.Constants.Companion.NO_INTERNET
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.io.IOException

@HiltViewModel
class SharedViewModel @Inject constructor(
    app: Application,
    private val repository: IPicturesRepository
) : AndroidViewModel(app) {

    private val loadedPictures = Pictures()
    private val _pictures = SingleLiveEvent<Resource<Pictures>>()
    private val pictures: LiveData<Resource<Pictures>> get() = _pictures

    private val limit = PIC_LIMIT
    private var page = 1

    init {
        loadPics()
    }

    fun getPics(): LiveData<Resource<Pictures>> {
        return pictures
    }

   fun getLoadedPics(): Pictures {
        return loadedPictures
    }

    fun loadPics() = viewModelScope.launch(Dispatchers.IO) {
        safeApiCall()
    }

    private suspend fun safeApiCall() {
        _pictures.postValue(Resource.Loading())
        try {
            if (isNetworkAvailable()) {
                val response = repository.getPics(page, limit)
                _pictures.postValue(handleApiResponse(response))
            } else {
                _pictures.postValue(Resource.Error(NO_INTERNET))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _pictures.postValue(Resource.Error(NETWORK_FAILURE))
                else -> _pictures.postValue(Resource.Error(CONVERSION_ERROR))
            }
        }
    }

    private fun handleApiResponse(response: Response<Pictures>): Resource<Pictures> {
        if (response.isSuccessful) {
            response.body().let { resultResponse ->
                page++
                return Resource.Success(resultResponse!!)
            }
        }
        return Resource.Error(response.message())
    }

    private fun isNetworkAvailable(): Boolean {
        var result = false
        val connectivityManager =
            getApplication<RandomPicsApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }

        return result
    }
}