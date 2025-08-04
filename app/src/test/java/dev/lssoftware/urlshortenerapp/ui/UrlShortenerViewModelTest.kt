package dev.lssoftware.urlshortenerapp.ui

import dev.lssoftware.urlshortenerapp.MainDispatcherRule
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
        fakeUrlShortenerRepository.shortenedUrlResponse = fakeShortenedUrl
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

    }
}