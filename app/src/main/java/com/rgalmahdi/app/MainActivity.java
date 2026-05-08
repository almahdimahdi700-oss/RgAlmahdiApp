package com.rgalmahdi.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.UpdateFrom;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private AdView mAdView;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipe = findViewById(R.id.swipe);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("https://rgalmahdi.blogspot.com"); 

        swipe.setOnRefreshListener(() -> webView.reload());
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                swipe.setRefreshing(false);
            }
        });

        MobileAds.initialize(this, initializationStatus -> {});
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        new AppUpdater(this)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("https://almahdimahdi700-oss.github.io/RgAlmahdiApp/update.json")
                .setTitleOnUpdateAvailable("🔥 تحديث رق المهدي برو")
                .setContentOnUpdateAvailable("إصدار جديد بمزايا حصرية. حدث الآن")
                .setButtonUpdate("تحديث فوري")
                .setButtonDismiss("ذكرني لاحقاً")
                .start();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
