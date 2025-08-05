package dev.lssoftware.urlshortenerapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lssoftware.urlshortenerapp.R
import dev.lssoftware.urlshortenerapp.model.ShortenUrl
import dev.lssoftware.urlshortenerapp.ui.theme.UrlShortenerAppTheme

const val URL_TEXT_FIELD_TAG = "URL_TEXT_FIELD_TAG"
const val SHORTEN_BUTTON_TAG = "SHORTEN_BUTTON_TAG"
const val SHORTENED_URL_LIST_TAG = "SHORTENED_URL_LIST_TAG"
const val SHORTENED_URL_LIST_ITEM_TAG = "SHORTENED_URL_LIST_ITEM_TAG"
const val LOADING_INDICATOR_TAG = "LOADING_INDICATOR_TAG"

@Composable
fun MainScreen(
    viewModel: UrlShortenerViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = LocalSnackbarHostState.current
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMessage ->
            snackbarHostState.showSnackbar(
                message = context.getString(errorMessage),
                duration = SnackbarDuration.Long
            )
            viewModel.clearErrorMessage()
        }
    }

    MainScreenContent(
        isLoading = uiState.isLoading,
        shortenedUrls = uiState.shortenedUrls,
        onShortenUrl = { url ->
            viewModel.shortenUrl(url.trim())
        },
    )
}

@Composable
fun MainScreenContent(
    isLoading: Boolean,
    shortenedUrls: List<ShortenUrl>,
    onShortenUrl: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        UrlInput(
            isLoading = isLoading
        ) {
            onShortenUrl(it)
        }
        RecentlyShortenedUrlList(
            shortenedUrls
        )
    }
}

@Composable
fun UrlInput(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onShortenUrl: (String) -> Unit = {}
) {
    var url by rememberSaveable { mutableStateOf("") }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier
                .weight(1f)
                .testTag(URL_TEXT_FIELD_TAG),
            value = url,
            onValueChange = { url = it },
            label = { Text(stringResource(R.string.url_input_label)) },
            placeholder = { Text(stringResource(R.string.url_input_placeholder)) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    onShortenUrl(url)
                }
            ),
            enabled = !isLoading
        )
        Button(
            modifier = Modifier.testTag(SHORTEN_BUTTON_TAG),
            onClick = { onShortenUrl(url) },
            enabled = url.isNotEmpty() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = modifier
                        .size(24.dp)
                        .testTag(LOADING_INDICATOR_TAG)
                )
            } else {
                Text(stringResource(R.string.shorten_button_label))
            }
        }
    }
}

@Composable
fun RecentlyShortenedUrlList(
    shortenedUrls: List<ShortenUrl> = emptyList()
) {
    Text(
        text = stringResource(R.string.recently_shortened_urls_section_label),
        modifier = Modifier.padding(bottom = 8.dp)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SHORTENED_URL_LIST_TAG),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        itemsIndexed(shortenedUrls) { index, url ->
            ShortenedUrlItem(
                modifier = Modifier.animateItem(),
                url = url,
                index = index
            )
        }
    }
}

@Composable
fun ShortenedUrlItem(
    url: ShortenUrl,
    index: Int,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier.testTag("${SHORTENED_URL_LIST_ITEM_TAG}_$index"),
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        headlineContent = { Text(url.originalUrl) },
        supportingContent = { Text(url.shortenedUrl) },
    )
}

@Composable
@Preview(showBackground = true)
fun ShortenedUrlItemPreview() {
    UrlShortenerAppTheme {
        ShortenedUrlItem(
            url = ShortenUrl(
                originalUrl = "https://example.com/long-url",
                shortenedUrl = "https://short.ly/1"
            ),
            index = 0
        )
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun MainScreenPreviewShortList() {
    UrlShortenerAppTheme {
        MainScreenContent(
            shortenedUrls = listOf(
                ShortenUrl("https://example.com/long-url-1", "https://short.ly/1"),
            ),
            onShortenUrl = {},
            isLoading = true
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun MainScreenPreviewLongList() {
    UrlShortenerAppTheme {
        MainScreenContent(
            shortenedUrls = listOf(
                ShortenUrl("https://example.com/long-url-1", "https://short.ly/1"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-3", "https://short.ly/3")
            ),
            onShortenUrl = {},
            isLoading = true
        )
    }
}