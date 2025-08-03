package dev.lssoftware.urlshortenerapp.ui

import dev.lssoftware.urlshortenerapp.data.UrlShortenerRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals


class UrlShortenerViewModelTest {
 @OptIn(ExperimentalCoroutinesApi::class)
 private val testDispatcher = UnconfinedTestDispatcher()
 @get:Rule
 val mockkRule = MockKRule(this)

 @MockK
 lateinit var mockUrlShortenerRepository: UrlShortenerRepository

 val viewModel by lazy {
     UrlShortenerViewModel(mockUrlShortenerRepository)
 }

 @Test
 fun `When a valid URL is provided, then the URL should be shortened successfully`() = runTest {
     // Given
     val validUrl = "https://www.example.com"
     val fakeShortenedUrl = "https://short.url/abc123"
     coEvery { mockUrlShortenerRepository.shortenUrl(validUrl) } returns Result.success(fakeShortenedUrl)
     // When
     viewModel.shortenUrl(validUrl)
     // Then
     assert(viewModel.uiState.value.shortenedUrls.size == 1)
     val shortenedUrl = viewModel.uiState.value.shortenedUrls.first()
     assertEquals(validUrl, shortenedUrl.originalUrl)
     assertEquals(fakeShortenedUrl, shortenedUrl.shortenedUrl)
     coVerify { mockUrlShortenerRepository.shortenUrl(validUrl) }
 }
}