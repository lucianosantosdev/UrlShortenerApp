package dev.lssoftware.urlshortenerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.lssoftware.urlshortenerapp.data.UrlShortenerRepositoryImpl
import dev.lssoftware.urlshortenerapp.ui.App
import dev.lssoftware.urlshortenerapp.ui.UrlShortenerViewModel
import dev.lssoftware.urlshortenerapp.ui.theme.UrlShortenerAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: UrlShortenerViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = UrlShortenerRepositoryImpl()
        viewModelFactory = UrlShortenerViewModel.Factory(repository)
        enableEdgeToEdge()
        setContent {
            val viewModel: UrlShortenerViewModel = viewModel(factory = viewModelFactory)
            App(viewModel)
        }
    }
}