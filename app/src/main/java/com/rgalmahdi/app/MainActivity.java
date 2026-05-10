package com.rgalmahdi.app;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fabShare;
    private String currentUrl = "https://rgalmahdi.blogspot.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. منع تصوير الشاشة - ميزة 8
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        fabShare = findViewById(R.id.fabShare);

        // إعدادات الويب فيو
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        // 2. الكاش السريع - ميزة 6
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // 3. شريط التحميل - ميزة 2
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
                currentUrl = url; // حفظ الرابط الحالي للمشاركة
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // 4. صفحة الأوفلاين الفخمة - ميزة 4
                String errorHtml = "<html><body style='background-color:#000; color:#FFD700; text-align:center; padding-top:100px; font-family:sans-serif;'>" +
                        "<h1>رق المهدي ن1 〠➥❍</h1>" +
                        "<h3>الموقع تحت الصيانة حالياً</h3>" +
                        "<p>اسحب للأسفل للتحديث</p>" +
                        "<p>أو تأكد من اتصالك بالانترنت</p>" +
                        "</body></html>";
                view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
            }
        });

        // 5. تحميل الملفات - ميزة 6
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimetype);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("جاري تحميل الملف...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "بدأ التحميل...", Toast.LENGTH_LONG).show();
            }
        });

        // 6. الرابط الصحيح تبعك
        webView.loadUrl(currentUrl);

        // 7. السحب للتحديث - ميزة موجودة
        swipeRefreshLayout.setOnRefreshListener(() -> webView.reload());

        // 8. زر المشاركة العائم - ميزة 4
        fabShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "شوف مقال من موقع رق المهدي ن1: " + currentUrl);
            startActivity(Intent.createChooser(shareIntent, "شارك الرابط عبر"));
        });
    }

    // زر الرجوع الذكي
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
