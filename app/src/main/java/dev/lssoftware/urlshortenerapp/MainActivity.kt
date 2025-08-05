package dev.lssoftware.urlshortenerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.lssoftware.urlshortenerapp.network.UrlShortenerAPI
import dev.lssoftware.urlshortenerapp.network.UrlShortenerRepositoryImpl
import dev.lssoftware.urlshortenerapp.ui.App
import dev.lssoftware.urlshortenerapp.ui.UrlShortenerViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModelFactory: UrlShortenerViewModel.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // As no dependency injection framework is used, we manually create the repository and provide it to the ViewModel factory to keep it life-cycle aware
        val repository = UrlShortenerRepositoryImpl(UrlShortenerAPI.create())
        viewModelFactory = UrlShortenerViewModel.Factory(repository)

        enableEdgeToEdge()
        setContent {
            val viewModel: UrlShortenerViewModel = viewModel(factory = viewModelFactory)
            App(viewModel)
        }
    }
}