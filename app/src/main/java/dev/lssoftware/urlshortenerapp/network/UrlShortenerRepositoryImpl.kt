package dev.lssoftware.urlshortenerapp.network

import android.util.Log
import dev.lssoftware.urlshortenerapp.data.UrlShortenerRepository
import dev.lssoftware.urlshortenerapp.model.UrlShortenerError

class UrlShortenerRepositoryImpl(
    private val urlShortenerService: UrlShortenerAPI
) : UrlShortenerRepository {
    override suspend fun shortenUrl(originalUrl: String): Result<String> {
        return try {
            val response = urlShortenerService.shortenUrl(AliasRequestDto(url = originalUrl))
            if (response.isSuccessful) {
                val shortenedUrl =
                    response.body()?.alias ?: throw Exception("Shortened URL not found")
                Result.success(shortenedUrl)
            } else {
                Result.failure(
                    UrlShortenerError.ServerError(
                        response.errorBody()?.string() ?: "Unknown server error"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("UrlShortenerRepositoryImpl", "Exception shortening URL", e)
            when (e) {
                is java.io.IOException -> Result.failure(UrlShortenerError.NetworkError)
                else -> Result.failure(
                    UrlShortenerError.UnknownError(
                        e.message ?: "Unknown error"
                    )
                )
            }
        }
    }
}