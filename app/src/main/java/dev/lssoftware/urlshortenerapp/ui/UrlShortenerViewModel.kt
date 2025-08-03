package dev.lssoftware.urlshortenerapp.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.lssoftware.urlshortenerapp.R
import dev.lssoftware.urlshortenerapp.data.UrlShortenerRepository
import dev.lssoftware.urlshortenerapp.model.ShortenUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UrlShortenerViewModel(
    private val urlShortenerRepository: UrlShortenerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    data class UiState(
        val shortenedUrls: List<ShortenUrl> = emptyList(),
        @StringRes val errorMessage:  Int? = null
    )

    suspend fun shortenUrl(originalUrl: String) {
        val result = urlShortenerRepository.shortUrl(originalUrl)
        result.onSuccess { shortenedUrl ->
            _uiState.update {
                it.copy(
                    shortenedUrls = it.shortenedUrls + ShortenUrl(originalUrl, shortenedUrl),
                    errorMessage = null
                )
            }
        }.onFailure { error ->
            println("Error shortening URL: ${error.message}")
            // TODO: mapping error to a user-friendly message
            _uiState.update {
                it.copy(
                    errorMessage = R.string.url_shortening_error
                )
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    class Factory(
        private val repository: UrlShortenerRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UrlShortenerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UrlShortenerViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}