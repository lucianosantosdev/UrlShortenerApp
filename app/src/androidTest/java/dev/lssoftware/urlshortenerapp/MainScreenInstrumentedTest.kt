package dev.lssoftware.urlshortenerapp

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.lssoftware.urlshortenerapp.data.FakeUrlShortenerRepositoryImpl
import dev.lssoftware.urlshortenerapp.ui.App
import dev.lssoftware.urlshortenerapp.ui.LOADING_INDICATOR_TAG
import dev.lssoftware.urlshortenerapp.ui.SHORTENED_URL_LIST_TAG
import dev.lssoftware.urlshortenerapp.ui.SHORTEN_BUTTON_TAG
import dev.lssoftware.urlshortenerapp.ui.URL_TEXT_FIELD_TAG
import dev.lssoftware.urlshortenerapp.ui.UrlShortenerViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainScreenInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeUrlShortenerRepository = FakeUrlShortenerRepositoryImpl()

    val viewModel by lazy {
        UrlShortenerViewModel(fakeUrlShortenerRepository)
    }

    @Test
    fun shouldStartWithEmptyList() {
        // Given
        composeTestRule.setContent {
            App(viewModel)
        }
        // Then
        composeTestRule.onNodeWithTag(SHORTENED_URL_LIST_TAG)
            .onChildren()
            .assertCountEquals(0)
    }

    @Test
    fun shouldAddShortenedUrlToTheList() {
        // Given
        composeTestRule.setContent {
            App(viewModel)
        }
        val inputUrl = "https://www.example.com"
        val fakeShortenedUrl = "https://short.url/abc123"
        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            Result.success(fakeShortenedUrl)
        }

        // When
        composeTestRule.onNodeWithTag(URL_TEXT_FIELD_TAG).performTextInput(inputUrl)
        composeTestRule.onNodeWithTag(SHORTEN_BUTTON_TAG).performClick()
        // Then
        // Verify that the repository was called with the correct URL
        assertEquals(inputUrl, fakeUrlShortenerRepository.calledUrls.first())
        // 1.Verify that the UI displays the shortened URL
        composeTestRule.onNodeWithTag(SHORTENED_URL_LIST_TAG)
            .onChildren()
            .assertCountEquals(1)
            .onFirst()
            .assert(
                hasText(fakeShortenedUrl)
            )
    }

    @Test
    fun shouldShowErrorMessageForInvalidUrl() {
        // Given
        val inputUrl = "invalid_url"
        composeTestRule.setContent {
            App(viewModel)
        }
        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            Result.failure(Exception("Invalid URL format"))
        }
        // When
        composeTestRule.onNodeWithTag(URL_TEXT_FIELD_TAG).performTextInput(inputUrl)
        composeTestRule.onNodeWithTag(SHORTEN_BUTTON_TAG).performClick()
        // Then
        // Verify that the repository was called with the correct URL
        assertEquals(inputUrl, fakeUrlShortenerRepository.calledUrls.first())
        // Verify that the UI does not display any shortened URLs
        composeTestRule.onNodeWithTag(SHORTENED_URL_LIST_TAG)
            .onChildren()
            .assertCountEquals(0)
        // Assert snackbar contains error text
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val errorMessage = context.getString(R.string.url_shortening_error_generic)
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun shouldShowErrorMessageForDuplicateUrl() {
        // Given
        composeTestRule.setContent {
            App(viewModel)
        }
        val inputUrl = "https://www.example.com"
        val fakeShortenedUrl = "https://short.url/abc123"
        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            Result.success(fakeShortenedUrl)
        }
        composeTestRule.onNodeWithTag(URL_TEXT_FIELD_TAG).performTextInput(inputUrl)
        composeTestRule.onNodeWithTag(SHORTEN_BUTTON_TAG).performClick()
        // When
        composeTestRule.onNodeWithTag(SHORTEN_BUTTON_TAG).performClick()
        // Then
        // 1.Verify that the repository was called with the correct URL only once
        assertEquals(1, fakeUrlShortenerRepository.calledUrls.size)
        assertEquals(inputUrl, fakeUrlShortenerRepository.calledUrls.first())
        // 2.Verify that the UI does not display any new shortened URLs
        composeTestRule.onNodeWithTag(SHORTENED_URL_LIST_TAG)
            .onChildren()
            .assertCountEquals(1)
        // 3.Assert snackbar contains error text
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val errorMessage = context.getString(R.string.url_shortening_error_duplicate)
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
    }

    @Test
    fun shouldShowLoadingIndicatorWhileShortening() {
        // Given
        composeTestRule.setContent {
            App(viewModel)
        }
        val inputUrl = "https://www.example.com"
        val fakeShortenedUrl = "https://short.url/abc123"
        fakeUrlShortenerRepository.shortenedUrlResponse = { url ->
            Result.success(fakeShortenedUrl)
        }
        fakeUrlShortenerRepository.enableProceedSignal = true
        // When
        composeTestRule.onNodeWithTag(URL_TEXT_FIELD_TAG).performTextInput(inputUrl)
        composeTestRule.onNodeWithTag(SHORTEN_BUTTON_TAG).performClick()
        // Then
        // Verify that the loading indicator is displayed
        composeTestRule.onNodeWithTag(LOADING_INDICATOR_TAG).assertIsDisplayed()
        fakeUrlShortenerRepository.proceedSignal.complete(Unit)
        composeTestRule.onNodeWithTag(LOADING_INDICATOR_TAG).assertDoesNotExist()
    }
}