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
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.ads.splash.SplashConfig;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.Ad;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fabShare;
    private String currentUrl = "https://rgalmahdi.blogspot.com";
    private StartAppAd startAppAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // تفعيل Splash Ad قبل setContentView
        StartAppSDK.init(this, "204445944", false);
        SplashConfig.getSplashConfig()
            .setAppName("رق المهدي برو")
            .setLogo(R.drawable.ic_launcher) // ← المسار الصحيح من مجلد drawable
            .setTheme(SplashConfig.Theme.USER_DEFINED)
            .setBgColor(android.graphics.Color.BLACK);
        
        StartAppAd.showSplash(this, savedInstanceState, new AdDisplayListener() {
            @Override
            public void adHidden(Ad ad) {
                loadMainContent();
            }
            @Override public void adDisplayed(Ad ad) {}
            @Override public void adClicked(Ad ad) {}
            @Override public void adNotDisplayed(Ad ad) {
                loadMainContent();
            }
        });
    }

    private void loadMainContent() {
        setContentView(R.layout.activity_main);
        startAppAd = new StartAppAd(this);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        fabShare = findViewById(R.id.fabShare);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

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
                currentUrl = url;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                String errorHtml = "<html><body style='background-color:#000; color:#FFD700; text-align:center; padding-top:100px; font-family:sans-serif;'>" +
                        "<h1>رق المهدي برو 〠</h1>" +
                        "<h3>تعذر تحميل المدونة</h3>" +
                        "<p>اسحب للأسفل للتحديث</p>" +
                        "<p>تأكد من اتصالك بالانترنت</p>" +
                        "</body></html>";
                view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
            }
        });

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

        webView.loadUrl(currentUrl);
        swipeRefreshLayout.setOnRefreshListener(() -> webView.reload());

        fabShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "تابع مدونة رق المهدي برو https://rgalmahdi.blogspot.com: " + currentUrl);
            startActivity(Intent.createChooser(shareIntent, "شارك الرابط عبر"));
        });

        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        Banner startAppBanner = new Banner(this);
        mainLayout.addView(startAppBanner);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            startAppAd.onBackPressed();
            super.onBackPressed();
        }
    }
}
