package dev.lssoftware.urlshortenerapp.ui

import dev.lssoftware.urlshortenerapp.MainDispatcherRule
import dev.lssoftware.urlshortenerapp.R
import dev.lssoftware.urlshortenerapp.data.FakeUrlShortenerRepositoryImpl
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
        assertEquals(R.string.url_shortening_error_generic, viewModel.uiState.value.errorMessage)
        assert(viewModel.uiState.value.shortenedUrls.isEmpty())
        assertEquals(false, viewModel.uiState.value.isLoading)
    }
}