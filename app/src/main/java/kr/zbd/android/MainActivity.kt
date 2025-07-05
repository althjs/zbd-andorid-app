package kr.zbd.android

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.chrisbanes.photoview.PhotoView
import kr.zbd.android.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webView: WebView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private var cameraPhotoPath: String? = null
    
    // 이미지 뷰어 관련 변수들
    private var imageViewerContainer: ConstraintLayout? = null
    private var photoView: PhotoView? = null
    private var isImageViewerActive = false

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val STORAGE_PERMISSION_REQUEST_CODE = 101
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView
        swipeRefreshLayout = binding.swipeRefreshLayout
        
        // Pull to Refresh 설정
        swipeRefreshLayout.setOnRefreshListener {
            webView.reload()
        }
        
        // WebView가 최상단에 있을 때만 Pull to Refresh 활성화
        webView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            swipeRefreshLayout.isEnabled = scrollY == 0
        }

        // Enable JavaScript and other settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        
        // WebView 줌 기능 완전 비활성화
        webView.settings.builtInZoomControls = false
        webView.settings.displayZoomControls = false
        webView.settings.setSupportZoom(false)
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        
        // 메모리 최적화 설정
        webView.settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
        webView.settings.databaseEnabled = true
        
        // 하드웨어 가속 활성화
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
        
        // 다운로드 리스너 추가
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val request = DownloadManager.Request(Uri.parse(url))
            request.setMimeType(mimetype)
            
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            request.allowScanningByMediaScanner()
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            
            Toast.makeText(this, "다운로드를 시작합니다: $fileName", Toast.LENGTH_SHORT).show()
        }

        // Set WebChromeClient for file upload
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                Log.d("MainActivity", "onShowFileChooser called")
                
                if (fileUploadCallback != null) {
                    fileUploadCallback!!.onReceiveValue(null)
                    fileUploadCallback = null
                }

                fileUploadCallback = filePathCallback
                Log.d("MainActivity", "fileUploadCallback set")

                val acceptTypes = fileChooserParams?.acceptTypes
                Log.d("MainActivity", "Accept types: ${acceptTypes?.joinToString(", ")}")
                
                // 웹과 동일한 UX를 위해 항상 카메라 옵션 표시
                val showCameraOption = true

                // Check camera permission
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Requesting camera permission")
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
                    return true
                }

                // Check storage permission for Android 13+
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MainActivity", "Requesting media permission (Android 13+)")
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO), STORAGE_PERMISSION_REQUEST_CODE)
                        return true
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("MainActivity", "Requesting storage permission")
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
                        return true
                    }
                }

                Log.d("MainActivity", "All permissions granted, starting file chooser")
                startFileChooser(showCameraOption)
                return true
            }
        }

        // Set a custom WebViewClient to handle URL loading
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false

                // 이미지 파일인지 확인
                if (isImageUrl(url)) {
                    showImageViewer(url)
                    return true
                }

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

                // 이미지 파일인지 확인
                if (isImageUrl(url)) {
                    showImageViewer(url)
                    return true
                }

                if (Uri.parse(url).host == "zbd.kr") {
                    return false // Let the WebView handle it
                }

                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    startActivity(this)
                }
                return true
            }
            
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                swipeRefreshLayout.isRefreshing = true
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipeRefreshLayout.isRefreshing = false
                
                // JavaScript를 통해 Pull to Refresh 상태 감지 개선
                view?.evaluateJavascript("""
                    (function() {
                        // 페이지 스크롤 위치 감지
                        let isAtTop = true;
                        let hasModalOpen = false;
                        
                        function updatePullToRefreshState() {
                            const currentIsAtTop = window.pageYOffset === 0 || document.documentElement.scrollTop === 0;
                            
                            // 모달이 열려있는지 확인
                            const modals = document.querySelectorAll('[role="dialog"], .modal, .overlay, .popup, [class*="modal"], [class*="dialog"], [class*="overlay"], [class*="popup"]');
                            const hasVisibleModal = Array.from(modals).some(modal => {
                                const style = window.getComputedStyle(modal);
                                return style.display !== 'none' && style.visibility !== 'hidden' && style.opacity !== '0';
                            });
                            
                            // 고정된 위치의 요소들(모달 등)이 있는지 확인
                            const fixedElements = document.querySelectorAll('[style*="position: fixed"], [style*="position:fixed"]');
                            const hasFixedElements = Array.from(fixedElements).some(el => {
                                const style = window.getComputedStyle(el);
                                return style.position === 'fixed' && style.display !== 'none' && style.zIndex > 1000;
                            });
                            
                            hasModalOpen = hasVisibleModal || hasFixedElements;
                            
                            // 모달이 열려있거나 페이지 최상단이 아니면 Pull to Refresh 비활성화
                            const shouldEnablePullToRefresh = currentIsAtTop && !hasModalOpen;
                            
                            if (shouldEnablePullToRefresh !== isAtTop || hasModalOpen) {
                                isAtTop = shouldEnablePullToRefresh;
                                // 안드로이드에 스크롤 상태 전달
                                if (window.Android && window.Android.setCanPullToRefresh) {
                                    window.Android.setCanPullToRefresh(isAtTop);
                                }
                            }
                        }
                        
                        // 스크롤 이벤트 리스너
                        let scrollTimeout;
                        window.addEventListener('scroll', function() {
                            clearTimeout(scrollTimeout);
                            scrollTimeout = setTimeout(updatePullToRefreshState, 16); // 60fps
                        }, { passive: true });
                        
                        // DOM 변경 감지 (모달 열기/닫기)
                        const observer = new MutationObserver(function(mutations) {
                            let shouldUpdate = false;
                            mutations.forEach(function(mutation) {
                                if (mutation.type === 'childList' || mutation.type === 'attributes') {
                                    shouldUpdate = true;
                                }
                            });
                            if (shouldUpdate) {
                                clearTimeout(scrollTimeout);
                                scrollTimeout = setTimeout(updatePullToRefreshState, 50);
                            }
                        });
                        
                        // DOM 변경 감지 시작
                        observer.observe(document.body, {
                            childList: true,
                            subtree: true,
                            attributes: true,
                            attributeFilter: ['style', 'class']
                        });
                        
                        // 터치 이벤트 감지 (모달 내 스크롤)
                        let touchStartY = 0;
                        document.addEventListener('touchstart', function(e) {
                            touchStartY = e.touches[0].clientY;
                        }, { passive: true });
                        
                        document.addEventListener('touchmove', function(e) {
                            // 터치 이벤트가 모달 내에서 발생하는지 확인
                            const target = e.target;
                            let isInModal = false;
                            let currentElement = target;
                            
                            while (currentElement && currentElement !== document.body) {
                                const style = window.getComputedStyle(currentElement);
                                if (style.position === 'fixed' || 
                                    currentElement.getAttribute('role') === 'dialog' ||
                                    currentElement.className.includes('modal') ||
                                    currentElement.className.includes('dialog') ||
                                    currentElement.className.includes('overlay') ||
                                    currentElement.className.includes('popup')) {
                                    isInModal = true;
                                    break;
                                }
                                currentElement = currentElement.parentElement;
                            }
                            
                            if (isInModal) {
                                // 모달 내에서 스크롤 중일 때 Pull to Refresh 비활성화
                                if (window.Android && window.Android.setCanPullToRefresh) {
                                    window.Android.setCanPullToRefresh(false);
                                }
                            }
                        }, { passive: true });
                        
                        document.addEventListener('touchend', function() {
                            // 터치 종료 후 상태 재확인
                            setTimeout(updatePullToRefreshState, 100);
                        }, { passive: true });
                        
                        // 초기 상태 설정
                        updatePullToRefreshState();
                        
                        // 페이지 로드 완료 후 한 번 더 확인
                        setTimeout(updatePullToRefreshState, 100);
                    })();
                """, null)
                // 웹 표준 API 확장 - 웹페이지 캡처 기능
                view?.evaluateJavascript("""
                    // 웹 표준 API처럼 동작하도록 확장
                    if (!window.webkitRequestFileSystem && !window.showSaveFilePicker) {
                        // Android WebView에서 파일 저장 API 에뮬레이션
                        window.saveWebPageAsImage = function() {
                            if (window.Android && window.Android.captureWebPage) {
                                window.Android.captureWebPage();
                                return Promise.resolve();
                            }
                            return Promise.reject(new Error('WebView capture not supported'));
                        };
                        
                        // HTML Canvas toBlob API 확장
                        if (!HTMLCanvasElement.prototype.toBlobNative) {
                            HTMLCanvasElement.prototype.toBlobNative = HTMLCanvasElement.prototype.toBlob;
                            HTMLCanvasElement.prototype.toBlob = function(callback, type, quality) {
                                // 일반적인 Canvas는 기본 동작
                                if (this.width < window.innerWidth || this.height < window.innerHeight) {
                                    return this.toBlobNative(callback, type, quality);
                                }
                                
                                // 전체 페이지 캡처 시도
                                try {
                                    if (window.Android && window.Android.captureWebPage) {
                                        window.Android.captureWebPage();
                                        if (callback) callback(null); // 안드로이드에서 처리됨을 알림
                                        return;
                                    }
                                } catch (e) {
                                    console.log('Android capture fallback');
                                }
                                
                                // 폴백: 기본 Canvas toBlob
                                return this.toBlobNative(callback, type, quality);
                            };
                        }
                    }
                """, null)
            }
        }

        // Handle the intent that started the activity (for deep links)
        handleIntent(intent)

        // Load the initial URL if not from a deep link
        if (intent?.data == null) {
            webView.loadUrl("https://zbd.kr/")
        }
        
        // JavaScript 인터페이스 추가
        webView.addJavascriptInterface(WebAppInterface(), "Android")

        // onBackPressed() 대체
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    // 1. 이미지 뷰어가 활성화된 경우 - 이미지 뷰어만 닫기
                    isImageViewerActive -> {
                        hideImageViewer()
                    }
                    // 2. WebView에서 뒤로 갈 수 있는 경우 - WebView 뒤로가기
                    webView.canGoBack() -> {
                        webView.goBack()
                    }
                    // 3. 더 이상 뒤로 갈 곳이 없는 경우 - 앱 종료 확인
                    else -> {
                        showExitConfirmDialog()
                    }
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
        Log.d("MainActivity", "handleIntent called with data: $appLinkData")
        
        if (appLinkData != null) {
            Log.d("MainActivity", "Processing deep link: $appLinkData")
            // This is a deep link, load the URL in the WebView
            val url = appLinkData.toString().replace("kr.zbd.android://", "https://")
            Log.d("MainActivity", "Loading URL in WebView: $url")
            webView.loadUrl(url)
        } else {
            Log.d("MainActivity", "No deep link data found")
        }
    }

    private fun startFileChooser(showCameraOption: Boolean) {
        Log.d("MainActivity", "startFileChooser called with camera option: $showCameraOption")
        
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 멀티파일 선택 활성화
        }

        val intents = mutableListOf<Intent>()
        
        // 웹과 동일한 UX를 위해 항상 카메라 옵션 표시
        if (showCameraOption) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoFile = createImageFile()
            if (photoFile != null) {
                cameraPhotoPath = photoFile.absolutePath
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    photoFile
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                intents.add(cameraIntent)
                Log.d("MainActivity", "Camera intent added")
            }
            
            // 비디오 카메라 인텐트 추가
            val videoCameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            intents.add(videoCameraIntent)
            Log.d("MainActivity", "Video camera intent added")
        }
        
        val chooserIntent = Intent.createChooser(intent, "파일 선택").apply {
            if (intents.isNotEmpty()) {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
            }
        }

        Log.d("MainActivity", "Launching file chooser with ${intents.size} additional intents")
        fileChooserLauncher.launch(chooserIntent)
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = getExternalFilesDir(null)
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        } catch (ex: IOException) {
            Log.e("MainActivity", "Error creating image file", ex)
            null
        }
    }

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MainActivity", "fileChooserLauncher result: ${result.resultCode}")
        
        val results = mutableListOf<Uri>()
        
        // Handle selected files from gallery/file picker first (갤러리나 파일 선택기에서 선택한 파일 우선)
        if (result.resultCode == Activity.RESULT_OK || result.data != null) {
            result.data?.let { data ->
                Log.d("MainActivity", "Result data available")
                
                // Single file selection
                data.data?.let { uri ->
                    results.add(uri)
                    Log.d("MainActivity", "Single file added: $uri")
                }
                
                // Multiple file selection
                data.clipData?.let { clipData ->
                    Log.d("MainActivity", "Multiple files selected: ${clipData.itemCount}")
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        results.add(uri)
                        Log.d("MainActivity", "Multiple file added: $uri")
                    }
                }
            }
        }
        
        // Handle camera photo only if no other files were selected (다른 파일이 선택되지 않은 경우에만 카메라 사진 추가)
        if (results.isEmpty() && cameraPhotoPath != null) {
            val file = File(cameraPhotoPath!!)
            if (file.exists()) {
                results.add(Uri.fromFile(file))
                Log.d("MainActivity", "Camera photo added: ${cameraPhotoPath}")
            }
        }
        
        // Clean up camera photo path
        cameraPhotoPath = null
        
        Log.d("MainActivity", "Total files selected: ${results.size}")
        
        if (results.isNotEmpty()) {
            fileUploadCallback?.onReceiveValue(results.toTypedArray())
        } else {
            Log.d("MainActivity", "No files selected or operation cancelled")
            fileUploadCallback?.onReceiveValue(null)
        }
        
        fileUploadCallback = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE, STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 웹과 동일한 UX를 위해 항상 카메라 옵션 표시
                    startFileChooser(true)
                } else {
                    fileUploadCallback?.onReceiveValue(null)
                    fileUploadCallback = null
                }
            }
        }
    }
    
    // JavaScript 인터페이스 클래스 - 웹페이지 캡처 기능
    inner class WebAppInterface {
        @android.webkit.JavascriptInterface
        fun captureWebPage() {
            runOnUiThread {
                captureWebViewAsImage()
            }
        }
        
        @android.webkit.JavascriptInterface
        fun setCanPullToRefresh(canPull: Boolean) {
            runOnUiThread {
                swipeRefreshLayout.isEnabled = canPull
            }
        }
        
        @android.webkit.JavascriptInterface
        fun showImageViewer(imageUrl: String) {
            runOnUiThread {
                showImageViewer(imageUrl)
            }
        }
    }
    
    private fun captureWebViewAsImage() {
        try {
            // 메모리 부족 방지를 위한 가비지 컬렉션
            System.gc()
            
            // WebView 크기 확인 및 최적화
            val maxDimension = 2048 // 최대 해상도 제한
            val webViewWidth = webView.width
            val webViewHeight = webView.height
            
            val scale = if (webViewWidth > maxDimension || webViewHeight > maxDimension) {
                minOf(maxDimension.toFloat() / webViewWidth, maxDimension.toFloat() / webViewHeight)
            } else {
                1.0f
            }
            
            val scaledWidth = (webViewWidth * scale).toInt()
            val scaledHeight = (webViewHeight * scale).toInt()
            
            val bitmap = Bitmap.createBitmap(
                scaledWidth,
                scaledHeight,
                Bitmap.Config.RGB_565 // 메모리 사용량 절반으로 줄임
            )
            
            val canvas = android.graphics.Canvas(bitmap)
            if (scale != 1.0f) {
                canvas.scale(scale, scale)
            }
            
            webView.draw(canvas)
            
            // 파일로 저장
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "WebCapture_$timeStamp.png"
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos) // 압축률 80%
            fos.flush()
            fos.close()
            
            // 메모리 정리
            bitmap.recycle()
            System.gc()
            
            // 미디어 스캐너에 알림
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = Uri.fromFile(file)
            sendBroadcast(mediaScanIntent)
            
            Toast.makeText(this, "웹페이지가 캡처되었습니다: $fileName", Toast.LENGTH_LONG).show()
            
        } catch (e: OutOfMemoryError) {
            Log.e("MainActivity", "메모리 부족으로 웹페이지 캡처 실패", e)
            Toast.makeText(this, "메모리가 부족하여 캡처에 실패했습니다", Toast.LENGTH_SHORT).show()
            System.gc() // 가비지 컬렉션 강제 실행
        } catch (e: Exception) {
            Log.e("MainActivity", "웹페이지 캡처 실패", e)
            Toast.makeText(this, "웹페이지 캡처에 실패했습니다", Toast.LENGTH_SHORT).show()
        }
    }
    
    // 이미지 뷰어 관련 메서드들
    private fun isImageUrl(url: String): Boolean {
        val imageExtensions = arrayOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg")
        val lowerUrl = url.lowercase()
        // 이미지 확장자 확인 또는 이미지 호스팅 서비스 패턴 확인
        return imageExtensions.any { lowerUrl.contains(it) } || 
               lowerUrl.contains("picsum.photos") ||
               lowerUrl.contains("images/") ||
               lowerUrl.contains("img/") ||
               url.contains("image") ||
               // Content-Type을 확인할 수 없으므로 일반적인 패턴으로 감지
               (lowerUrl.contains("random=") && lowerUrl.contains("picsum"))
    }
    
    private fun showImageViewer(imageUrl: String) {
        if (isImageViewerActive) return
        
        isImageViewerActive = true
        
        // 이미지 뷰어 컨테이너 생성
        imageViewerContainer = ConstraintLayout(this).apply {
            id = View.generateViewId()
            setBackgroundColor(0xDD000000.toInt()) // 반투명 검은색 배경
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            
            // 배경 클릭 시 뷰어 닫기
            setOnClickListener { hideImageViewer() }
        }
        
        // PhotoView 생성 (고성능 이미지 뷰어)
        photoView = PhotoView(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            
            // PhotoView 자체 설정
            minimumScale = 0.5f
            maximumScale = 10.0f
            mediumScale = 2.0f
            
            // 싱글탭으로 이미지 뷰어 닫기
            setOnPhotoTapListener { _, _, _ ->
                hideImageViewer()
            }
            
            // 뷰 영역 외부 탭으로도 닫기
            setOnOutsidePhotoTapListener {
                hideImageViewer()
            }
        }
        
        // 이미지 로드
        Glide.with(this)
            .load(imageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(photoView!!)
        
        // 뷰 계층에 추가
        imageViewerContainer?.addView(photoView)
        (binding.root as ConstraintLayout).addView(imageViewerContainer)
    }
    
    private fun hideImageViewer() {
        if (!isImageViewerActive) return
        
        isImageViewerActive = false
        
        imageViewerContainer?.let { container ->
            (binding.root as ConstraintLayout).removeView(container)
        }
        
        imageViewerContainer = null
        photoView = null
    }
    
    // 앱 종료 확인 다이얼로그
    private fun showExitConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("앱 종료")
            .setMessage("정말로 앱을 종료하시겠습니까?")
            .setPositiveButton("종료") { _, _ ->
                // 앱 종료
                finishAffinity()
            }
            .setNegativeButton("취소") { dialog, _ ->
                // 다이얼로그만 닫기
                dialog.dismiss()
            }
            .setCancelable(true) // 바깥 영역 터치로도 취소 가능
            .show()
    }
}