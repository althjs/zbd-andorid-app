package kr.zbd.android

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import kr.zbd.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView

        // Enable JavaScript and other settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        // Set a custom WebViewClient to handle URL loading
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false

                if (Uri.parse(url).host == "zbd.kr") {
                    return false // Let the WebView handle it
                }

                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    startActivity(this)
                }
                return true
            }

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false

                if (Uri.parse(url).host == "zbd.kr") {
                    return false // Let the WebView handle it
                }

                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    startActivity(this)
                }
                return true
            }
        }

        // Handle the intent that started the activity (for deep links)
        handleIntent(intent)

        // Load the initial URL if not from a deep link
        if (intent?.data == null) {
            webView.loadUrl("https://zbd.kr/")
        }

        // onBackPressed() 대체
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                }
                else {
                    isEnabled = false // 콜백 비활성화
                    onBackPressedDispatcher.onBackPressed() // 기본 뒤로 가기 동작 수행
                }
            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Handle deep links when the activity is already running
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkData: Uri? = intent?.data
        if (appLinkData != null) {
            // This is a deep link, load the URL in the WebView
            val url = appLinkData.toString().replace("kr.zbd.android://", "https://")
            webView.loadUrl(url)
        }
    }
}