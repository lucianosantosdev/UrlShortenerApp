package dev.lssoftware.urlshortenerapp.data

class FakeUrlShortenerRepositoryImpl() : UrlShortenerRepository {
    val calledUrls = mutableListOf<String>()
    var shortenedUrlResponse: String? = null
    override suspend fun shortenUrl(originalUrl: String): Result<String> {
        calledUrls.add(originalUrl)
        return if (shortenedUrlResponse == null) {
            Result.failure(Exception("Fake error"))
        } else {
            Result.success(shortenedUrlResponse!!)
        }
    }
}