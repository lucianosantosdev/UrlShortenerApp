package dev.lssoftware.urlshortenerapp.model

sealed class UrlShortenerError : Exception() {
    object NetworkError : UrlShortenerError()
    data class ServerError(override val message: String) : UrlShortenerError()
    data class UnknownError(override val message: String) : UrlShortenerError()
}