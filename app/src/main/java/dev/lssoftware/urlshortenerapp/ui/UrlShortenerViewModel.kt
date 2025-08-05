package dev.lssoftware.urlshortenerapp.ui

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.lssoftware.urlshortenerapp.R
import dev.lssoftware.urlshortenerapp.data.UrlShortenerRepository
import dev.lssoftware.urlshortenerapp.model.ShortenUrl
import dev.lssoftware.urlshortenerapp.model.UrlShortenerError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UrlShortenerViewModel(
    private val urlShortenerRepository: UrlShortenerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    data class UiState(
        val isLoading: Boolean = false,
        val shortenedUrls: List<ShortenUrl> = emptyList(),
        @param:StringRes val errorMessage: Int? = null
    )

    fun shortenUrl(originalUrl: String) {
        if (_uiState.value.shortenedUrls.any { it.originalUrl == originalUrl }) {
            setErrorMessage(R.string.url_shortening_error_duplicate)
            return
        }
        if (!isValidUrl(originalUrl)) {
            setErrorMessage(R.string.url_shortening_error_invalid)
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val result: Result<String> = urlShortenerRepository.shortenUrl(originalUrl)
            result.onSuccess { shortenedUrl ->
                _uiState.update {
                    it.copy(
                        shortenedUrls = listOf(
                            ShortenUrl(
                                originalUrl,
                                shortenedUrl
                            )
                        ) + it.shortenedUrls, // Prepend the new shortened URL
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                val errorMessage = if (error is UrlShortenerError) {
                    error.toMessageRes()
                } else {
                    R.string.url_shortening_error_unknown
                }
                setErrorMessage(errorMessage)
            }
        }
    }

    private fun UrlShortenerError.toMessageRes(): Int {
        return when (this) {
            is UrlShortenerError.NetworkError -> R.string.url_shortening_error_network
            is UrlShortenerError.ServerError -> R.string.url_shortening_error_server
            else -> R.string.url_shortening_error_unknown
        }
    }

    fun clearErrorMessage() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    private fun setErrorMessage(@StringRes message: Int) {
        _uiState.update {
            it.copy(
                errorMessage = message,
                isLoading = false
            )
        }
    }

    private val WEB_URL_REGEX =
        Regex("""^(https?:\/\/)?([\w\-]+\.)+[\w]{2,}(:\d+)?(\/\S*)?$""")

    private fun isValidUrl(input: String): Boolean {
        return WEB_URL_REGEX.matches(input)
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