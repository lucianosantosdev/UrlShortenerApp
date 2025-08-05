package dev.lssoftware.urlshortenerapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lssoftware.urlshortenerapp.R
import dev.lssoftware.urlshortenerapp.model.ShortenUrl
import dev.lssoftware.urlshortenerapp.ui.theme.UrlShortenerAppTheme

const val URL_TEXT_FIELD_TAG = "URL_TEXT_FIELD_TAG"
const val SHORTEN_BUTTON_TAG = "SHORTEN_BUTTON_TAG"
const val SHORTENED_URL_LIST_TAG = "SHORTENED_URL_LIST_TAG"
const val SHORTENED_URL_LIST_ITEM_TAG = "SHORTENED_URL_LIST_ITEM_TAG"

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
        shortenedUrls = uiState.shortenedUrls,
        onShortenUrl = { url ->
            viewModel.shortenUrl(url)
        },
    )
}

@Composable
fun MainScreenContent(
    shortenedUrls: List<ShortenUrl>,
    onShortenUrl: (String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        UrlInput {
            onShortenUrl(it)
        }
        RecentlyShortenedUrlList(
            shortenedUrls
        )
    }
}

@Composable
fun UrlInput(
    modifier: Modifier = Modifier,
    onShortenUrl: (String) -> Unit = {}
) {
    var url by remember { mutableStateOf("") }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            modifier = Modifier.testTag(URL_TEXT_FIELD_TAG),
            value = url,
            onValueChange = { url = it },
            label = { Text(stringResource(R.string.url_input_label)) },
            placeholder = { Text(stringResource(R.string.url_input_placeholder)) }
        )
        Button(
            modifier = Modifier.testTag(SHORTEN_BUTTON_TAG),
            onClick = { onShortenUrl(url) },
            enabled = url.isNotEmpty()
        ) {
            Text(stringResource(R.string.shorten_button_label))
        }
    }
}

@Composable
fun RecentlyShortenedUrlList(
    shortenedUrls: List<ShortenUrl> = emptyList()
) {
    LazyColumn(
        modifier = Modifier.testTag(SHORTENED_URL_LIST_TAG),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(shortenedUrls) { index, url ->
            ListItem(
                modifier = Modifier
                    .padding(8.dp)
                    .testTag("${SHORTENED_URL_LIST_ITEM_TAG}_$index"),
                headlineContent = { Text(url.originalUrl) },
                supportingContent = { Text(url.shortenedUrl) },
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun MainScreenPreview() {
    UrlShortenerAppTheme {
        MainScreenContent(
            shortenedUrls = listOf(
                ShortenUrl("https://example.com/long-url-1", "https://short.ly/1"),
                ShortenUrl("https://example.com/long-url-2", "https://short.ly/2"),
                ShortenUrl("https://example.com/long-url-3", "https://short.ly/3")
            ),
            onShortenUrl = {}
        )
    }
}