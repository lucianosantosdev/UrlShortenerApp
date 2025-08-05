package dev.lssoftware.urlshortenerapp.data

import kotlinx.coroutines.CompletableDeferred

class FakeUrlShortenerRepositoryImpl() : UrlShortenerRepository {
    val calledUrls = mutableListOf<String>()
    var shortenedUrlResponse: ((String) -> Result<String>)? = null

    // Used to simulate long running operations
    var enableProceedSignal = false
    val proceedSignal = CompletableDeferred<Unit>()

    override suspend fun shortenUrl(originalUrl: String): Result<String> {
        calledUrls += originalUrl

        if (enableProceedSignal) {
            proceedSignal.await()
        }

        if (shortenedUrlResponse == null) {
            throw IllegalStateException("shortenedUrlResponse fake behavior is not set")
        }
        return shortenedUrlResponse!!(originalUrl)
    }
}