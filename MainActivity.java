package com.rqalmahdi.app;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {
    WebView webView;
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipe = findViewById(R.id.swipe);
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        
        String htmlData = "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>body{margin:0;padding:0;} #ad-banner{position:fixed;bottom:0;width:100%;z-index:9999;text-align:center;background:#fff;}</style>" +
                "</head><body>" +
                "<div id='ad-banner'>" +
                "<script type='text/javascript'>atOptions = {'key' : '5195f84e21ecd335219963087dc2f5bf','format' : 'iframe','height' : 50,'width' : 320,'params' : {}};<\/script>" +
                "<script type='text/javascript' src='https://intermediatenormalconfederate.com/5195f84e21ecd335219963087dc2f5bf/invoke.js'><\/script>" +
                "</div>" +
                "<iframe src='https://rgalmahdi.blogspot.com/?m=1' style='border:0;width:100%;height:calc(100vh - 50px);'></iframe>" +
                "</body></html>";

        webView.loadDataWithBaseURL("https://rgalmahdi.blogspot.com", htmlData, "text/html", "UTF-8", null);
        webView.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) { swipe.setRefreshing(false); }
        });
        swipe.setOnRefreshListener(() -> webView.reload());
    }
    @Override public void onBackPressed() { if (webView.canGoBack()) { webView.goBack(); } else { super.onBackPressed(); } }
}
