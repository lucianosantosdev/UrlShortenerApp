package dev.lssoftware.urlshortenerapp.data

import dev.lssoftware.urlshortenerapp.MainDispatcherRule
import dev.lssoftware.urlshortenerapp.network.UrlShortenerAPI
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class UrlShortenerRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: UrlShortenerAPI
    private lateinit var repository: UrlShortenerRepositoryImpl

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(UrlShortenerAPI::class.java)
        repository = UrlShortenerRepositoryImpl(api)
    }

    @Test
    fun `shortenUrl returns success when API returns valid alias`() = runTest {
        // Given
        val mockResponse = MockResponse(
            code = 200,
            body = """
                {
                    "alias": "760432549",
                    "_links": {
                        "self": "https://example.com",
                        "short": "https://url-shortener-server.onrender.com/api/alias/760432549"
                    }
                }
            """.trimIndent()
        )
        mockWebServer.enqueue(mockResponse)
        // When
        val result = repository.shortenUrl("https://example.com")
        // Then
        // Validate that the request was made to the correct endpoint and payload
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/alias", recordedRequest.url.encodedPath)
        assertEquals("POST", recordedRequest.method)
        assertEquals("application/json; charset=utf-8", recordedRequest.headers.get("Content-Type"))
        assertEquals("""{"url":"https://example.com"}""", recordedRequest.body!!.utf8())

        // Validate that the result is successful and contains the expected shortened URL
        assertTrue(result.isSuccess)
        assertEquals(
            "760432549",
            result.getOrNull()
        )
    }

    @Test
    fun `shortenUrl returns failure when API returns error`() = runTest {
        // Given
        val mockResponse = MockResponse(
            code = 500,
            body = "Bad Request"
        )
        mockWebServer.enqueue(mockResponse)
        // When
        val result = repository.shortenUrl("invalid-url")
        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `shortenUrl returns failure when API returns unexpected response`() = runTest {
        // Given
        val mockResponse = MockResponse(
            code = 200,
            body = """
                {
                    "unexpected_field": "unexpected_value"
                }
            """.trimIndent()
        )
        mockWebServer.enqueue(mockResponse)
        // When
        val result = repository.shortenUrl("https://example.com")
        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `shortenUrl returns failure when network error occurs`() = runTest {
        // Given
        mockWebServer.close()
        // When
        val result = repository.shortenUrl("https://example.com")
        // Then
        assertTrue(result.isFailure)
    }
}
