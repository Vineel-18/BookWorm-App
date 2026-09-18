package com.bookworm.app

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.webkit.WebViewAssetLoader

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var assetLoader: WebViewAssetLoader

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Serve assets from https://appassets.androidplatform.net/assets/
        assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView = findViewById(R.id.webView)
        setupWebView()
        setupBackNavigation()

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        } else {
            webView.loadUrl(HOME_URL)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.apply {
            settings.apply {
                javaScriptEnabled                = true
                domStorageEnabled                = true
                databaseEnabled                  = true
                allowFileAccess                  = false   // not needed with AssetLoader
                allowContentAccess               = false
                setSupportZoom(false)
                builtInZoomControls              = false
                displayZoomControls              = false
                useWideViewPort                  = true
                loadWithOverviewMode             = true
                mediaPlaybackRequiresUserGesture = false
            }
            webViewClient  = BookWormWebViewClient()
            webChromeClient = WebChromeClient()
        }
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) webView.goBack() else finish()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    private inner class BookWormWebViewClient : WebViewClient() {
        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest
        ): WebResourceResponse? {
            // Let the AssetLoader handle all https://appassets.androidplatform.net requests
            return assetLoader.shouldInterceptRequest(request.url)
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            // Keep all appassets URLs inside the WebView
            return if (url.startsWith("https://appassets.androidplatform.net")) {
                false
            } else {
                startActivity(
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(url)
                    )
                )
                true
            }
        }
    }

    companion object {
        // WebViewAssetLoader serves from https://appassets.androidplatform.net/assets/bookworm/
        const val HOME_URL =
            "https://appassets.androidplatform.net/assets/bookworm/login.html"
    }
}
