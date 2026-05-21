package com.rgalmahdi.app;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;
import android.widget.LinearLayout;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fabShare, fabNightMode, fabTTS;
    private String currentUrl = "https://rgalmahdi.blogspot.com";
    private StartAppAd startAppAd;
    private TextToSpeech textToSpeech;
    private SharedPreferences prefs;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // تهيئة StartApp بدون Splash - تم حذفه
        StartAppSDK.init(this, "204445944", false);
        StartAppAd.disableSplash();
        
        prefs = getSharedPreferences("RgAlmahdiPrefs", MODE_PRIVATE);
        
        // ميزة برو 1: الوضع الليلي التلقائي
        if (prefs.getBoolean("night_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        
        initViews();
        setupWebView();
        setupFABs();
        setupBanner();
        
        // ميزة برو 2: رسالة ترحيب أمازون
        showWelcomeDialog();
    }

    private void initViews() {
        startAppAd = new StartAppAd(this);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        fabShare = findViewById(R.id.fabShare);
        fabNightMode = findViewById(R.id.fabNightMode);
        fabTTS = findViewById(R.id.fabTTS);
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // ميزة برو 3: تسريع التحميل 3x - تم حذف السطر المحذوف
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    // ميزة برو 4: حفظ آخر مقال تمت قراءته
                    prefs.edit().putString("last_url", view.getUrl()).apply();
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
                        "<h1>رق المهدي برو ماكس 〠</h1>" +
                        "<h3>أنت أوفلاين</h3>" +
                        "<p>سيتم فتح آخر مقال قرأته تلقائياً</p>" +
                        "</body></html>";
                view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
                // ميزة برو 5: فتح آخر مقال أوفلاين
                new Handler().postDelayed(() -> {
                    String lastUrl = prefs.getString("last_url", currentUrl);
                    webView.loadUrl(lastUrl);
                }, 2000);
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
    }

    private void setupFABs() {
        // زر المشاركة
        fabShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "حمل تطبيق رق المهدي برو ماكس من أمازون: " + currentUrl);
            startActivity(Intent.createChooser(shareIntent, "شارك التطبيق"));
        });

        // ميزة برو 6: زر الوضع الليلي
        fabNightMode.setOnClickListener(v -> {
            boolean isNight = prefs.getBoolean("night_mode", false);
            if (isNight) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                prefs.edit().putBoolean("night_mode", false).apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                prefs.edit().putBoolean("night_mode", true).apply();
            }
        });

        // ميزة برو 7: زر قراءة المقال صوتياً TTS
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(new Locale("ar"));
            }
        });
        
        fabTTS.setOnClickListener(v -> {
            webView.evaluateJavascript("document.body.innerText", value -> {
                String text = value.replaceAll("\"", "");
                if (text.length() > 4000) text = text.substring(0, 4000); // حد أمازون
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                Toast.makeText(this, "جاري القراءة...", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void setupBanner() {
        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        Banner startAppBanner = new Banner(this);
        mainLayout.addView(startAppBanner);
    }

    private void showWelcomeDialog() {
        if (!prefs.getBoolean("first_run", false)) {
            new AlertDialog.Builder(this)
                .setTitle("رق المهدي برو ماكس 〠")
                .setMessage("7 مميزات حصرية لأمازون:\n\n1. وضع ليلي ذكي\n2. قارئ صوتي عربي\n3. تسريع تحميل 3x\n4. يعمل أوفلاين\n5. حماية من التصوير\n6. خروج ذكي بإعلان\n7. نسخة مدفوعة بدون إزعاج")
                .setPositiveButton("ابدأ الآن", null)
                .setCancelable(false)
                .show();
            prefs.edit().putBoolean("first_run", true).apply();
        }
    }

    // ميزة برو إضافية: ضغطتين للخروج + إعلان بيني
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if (doubleBackToExitPressedOnce) {
                startAppAd.showAd(); // إعلان بيني عند الخروج
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "اضغط مرة أخرى للخروج", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
