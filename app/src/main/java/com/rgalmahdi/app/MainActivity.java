package com.rgalmahdi.app;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // هذه الصفحة تطلع لو موقعك طافي
                String errorHtml = "<html><body style='background-color:#000; color:#FFD700; text-align:center; padding-top:100px; font-family:sans-serif;'>" +
                        "<h1>رق المهدي ن1 ❍〠〄</h1>" +
                        "<h3>الموقع تحت الصيانة حالياً</h3>" +
                        "<p>اسحب للأسفل للتحديث</p>" +
                        "<p>أو تأكد من اتصالك بالانترنت</p>" +
                        "</body></html>";
                view.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
            }
        });

        webView.loadUrl("https://rgalmahdi.com");
        swipeRefreshLayout.setOnRefreshListener(() -> webView.reload());
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
