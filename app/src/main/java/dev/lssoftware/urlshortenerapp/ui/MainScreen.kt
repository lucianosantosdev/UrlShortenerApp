package dev.lssoftware.urlshortenerapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.lssoftware.urlshortenerapp.model.ShortenUrl
import dev.lssoftware.urlshortenerapp.ui.theme.UrlShortenerAppTheme
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: UrlShortenerViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
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
            coroutineScope.launch {
                viewModel.shortenUrl(url)
            }
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
        UrlInput() {
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
            value = url,
            onValueChange = { url = it },
            label = { Text("Enter URL to shorten") },
            placeholder = { Text("https://example.com") }
        )
        Button(
            onClick = { onShortenUrl(url) },
            enabled = url.isNotEmpty()
        ) {
            Text("Shorten")
        }
    }
}

@Composable
fun RecentlyShortenedUrlList(
    shortenedUrls: List<ShortenUrl> = emptyList()
) {
    LazyColumn {
        items(shortenedUrls) { url ->
            ListItem(
                headlineContent = { Text(url.originalUrl) },
                supportingContent = { Text(url.shortenedUrl) },
                modifier = Modifier.padding(8.dp)
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