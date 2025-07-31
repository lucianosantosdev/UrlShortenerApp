package dev.lssoftware.urlshortenerapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.lssoftware.urlshortenerapp.data.UrlShortenerRepository
import dev.lssoftware.urlshortenerapp.model.ShortenUrl
import kotlinx.coroutines.flow.MutableStateFlow

class UrlShortenerViewModel(
    private val urlShortenerRepository: UrlShortenerRepository
) : ViewModel() {
    private val _shortenedUrls = mutableListOf<ShortenUrl>()
    val shortenedUrls = MutableStateFlow(_shortenedUrls)

    suspend fun shortenUrl(originalUrl: String) {
        val result = urlShortenerRepository.shortUrl(originalUrl)
        result.onSuccess { shortenedUrl ->
            _shortenedUrls.add(ShortenUrl(originalUrl, shortenedUrl))
            shortenedUrls.value = _shortenedUrls
        }.onFailure {
            // Handle error (e.g., log it, show a message to the user)
            println("Error shortening URL: ${it.message}")
        }
    }
}