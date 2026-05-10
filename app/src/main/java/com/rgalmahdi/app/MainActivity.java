package com.rgalmahdi.app;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String mainUrl = "https://rgalmahdi.com";
    
    // كود الإعلانات حقك
    private String adScript = "<script async='async' data-cfasync='false' src='https://intermediatenormalconfederate.com/525d849562869222b431b036a02e540b/invoke.js'></script><div id='container-525d849562869222b431b036a02e540b'></div>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // التحديث التلقائي - يفحص Github كل ما تفتح التطبيق
        new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.GITHUB)
                .setGitHubUserAndRepo("almahdimahdi700-oss", "RgAlmahdiApp")
                .setDisplay(Display.DIALOG)
                .setTitleOnUpdateAvailable("تحديث جديد متوفر")
                .setContentOnUpdateAvailable("يوجد إصدار جديد من تطبيق رق المهدي. هل تريد التحديث الآن؟")
                .setButtonUpdate("تحديث")
                .setButtonDismiss("لاحقاً")
                .setButtonDoNotShowAgain(null)
                .showAppUpdated(true)
                .start();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);

        // إعدادات WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // عشان الروابط تفتح داخل التطبيق
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // حقن كود الإعلانات بعد ما تحمل الصفحة
                webView.loadUrl("javascript:(function() {" +
                        "var parent = document.getElementsByTagName('body').item(0);" +
                        "var script = document.createElement('div');" +
                        "script.innerHTML = '" + adScript + "';" +
                        "parent.appendChild(script);" +
                        "})()");
                
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // السحب للتحديث
        swipeRefreshLayout.setOnRefreshListener(() -> webView.reload());

        // تحميل الموقع
        webView.loadUrl(mainUrl);
    }

    // زر الرجوع
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
