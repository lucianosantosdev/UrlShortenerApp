package dev.lssoftware.urlshortenerapp.ui

import dev.lssoftware.urlshortenerapp.MainDispatcherRule
import dev.lssoftware.urlshortenerapp.R
import dev.lssoftware.urlshortenerapp.data.FakeUrlShortenerRepositoryImpl
import dev.lssoftware.urlshortenerapp.model.UrlShortenerError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UrlShortenerViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeUrlShortenerRepository = FakeUrlShortenerRepositoryImpl()

    val viewModel by lazy {
        UrlShortenerViewModel(fakeUrlShortenerRepository)
    }

    @Test
    fun `When a valid URL is provided, then the URL should be shortened successfully`() = runTest {
        // Given
        val validUrl = "https://www.example.com"
        val fakeShortenedUrl = "https://short.url/abc123"
        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            assertEquals(true, viewModel.uiState.value.isLoading)
            Result.success(fakeShortenedUrl)
        }
        // When
        viewModel.shortenUrl(validUrl)
        // Then
        // Verify that the repository was called with the correct URL
        assertEquals(validUrl, fakeUrlShortenerRepository.calledUrls.first())
        // Verify that the UI state has been updated with the shortened URL
        assert(viewModel.uiState.value.shortenedUrls.size == 1)
        val shortenedUrl = viewModel.uiState.value.shortenedUrls.first()
        assertEquals(validUrl, shortenedUrl.originalUrl)
        assertEquals(fakeShortenedUrl, shortenedUrl.shortenedUrl)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `When a duplicate URL is provided, then an error message should be shown`() = runTest {
        // Given
        val duplicateUrl = "https://www.duplicate.com"
        val fakeShortenedUrl = "https://short.url/dup123"
        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            assertEquals(true, viewModel.uiState.value.isLoading)
            Result.success(fakeShortenedUrl)
        }
        // When
        viewModel.shortenUrl(duplicateUrl)
        // Shorten the same URL again to trigger the duplicate error
        viewModel.shortenUrl(duplicateUrl)
        // Then
        // Verify that the repository was called only once
        assertEquals(1, fakeUrlShortenerRepository.calledUrls.size)
        // Verify that the UI state has an error message for duplicate URL
        assertEquals(R.string.url_shortening_error_duplicate, viewModel.uiState.value.errorMessage)
        assert(viewModel.uiState.value.shortenedUrls.size == 1)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `When shorten URL fail, then a generic error message should be shown`() {
        // Given
        val validUrl = "https://www.error.com"
        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            assertEquals(true, viewModel.uiState.value.isLoading)
            Result.failure(Exception("Network error"))
        }
        // When
        viewModel.shortenUrl(validUrl)
        // Then
        // Verify that the repository was called with the correct URL
        assertEquals(validUrl, fakeUrlShortenerRepository.calledUrls.first())
        // Verify that the UI state has an error message for generic error
        assertEquals(
            dev.lssoftware.urlshortenerapp.R.string.url_shortening_error_unknown,
            viewModel.uiState.value.errorMessage
        )
        assert(viewModel.uiState.value.shortenedUrls.isEmpty())
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `When a new URL is shortened, then it should be added to the top of the list`() = runTest {
        // Given
        val firstUrl = "https://www.first.com"
        val firstShortenedUrl = "https://short.url/first123"
        val secondUrl = "https://www.second.com"
        val secondShortenedUrl = "https://short.url/second123"

        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            if (url == firstUrl) Result.success(firstShortenedUrl)
            else Result.success(secondShortenedUrl)
        }

        // When
        viewModel.shortenUrl(firstUrl)
        viewModel.shortenUrl(secondUrl)

        // Then
        assertEquals(2, viewModel.uiState.value.shortenedUrls.size)
        // The second shortened URL should be at the top
        assertEquals(secondUrl, viewModel.uiState.value.shortenedUrls[0].originalUrl)
        assertEquals(secondShortenedUrl, viewModel.uiState.value.shortenedUrls[0].shortenedUrl)
        // The first shortened URL should be below it
        assertEquals(firstUrl, viewModel.uiState.value.shortenedUrls[1].originalUrl)
        assertEquals(firstShortenedUrl, viewModel.uiState.value.shortenedUrls[1].shortenedUrl)
    }

    @Test
    fun `When an invalid URL is provided, then an error message should be shown`() = runTest {
        // Given
        val invalidUrl = "invalid-url"
        // When
        viewModel.shortenUrl(invalidUrl)
        // Then
        // Verify that the repository was not called
        assert(fakeUrlShortenerRepository.calledUrls.isEmpty())
        // Verify that the UI state has an error message for invalid URL
        assertEquals(R.string.url_shortening_error_invalid, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `When server error occurs, then an error message should be shown`() = runTest {
        // Given
        val validUrl = "https://www.example.com"
        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            Result.failure(UrlShortenerError.ServerError("Server error"))
        }
        // When
        viewModel.shortenUrl(validUrl)
        // Then
        assertEquals(R.string.url_shortening_error_server, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `When network error occurs, then an error message should be shown`() = runTest {
        // Given
        val validUrl = "https://www.example.com"
        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            Result.failure(UrlShortenerError.NetworkError)
        }
        // When
        viewModel.shortenUrl(validUrl)
        // Then
        assertEquals(R.string.url_shortening_error_network, viewModel.uiState.value.errorMessage)
    }
}