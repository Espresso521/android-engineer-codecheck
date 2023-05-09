package jp.kotaku.camera.activity;

import android.content.Context;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import jp.co.yumemi.android.code_check.R;

public class BankSettingActivity extends AppCompatActivity {

    private Context mContext;

    private WebView webView;

    private static boolean isEdy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_bank_setting);
        webView = findViewById(R.id.bankWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        webView.getSettings().setTextZoom(100);
        webView.addJavascriptInterface(new WebCallBack(new Callbacks() {
                    @Override
                    public void gotoNextPhase() {
                        showToast("gotoNextPhase");
                    }

                    @Override
                    public void checkBoxItem1(boolean value) {
                        showToast("checkBoxItem1 value is " + value);
                    }

                    @Override
                    public void checkBoxItem2(boolean value) {
                        showToast("checkBoxItem2 value is " + value);
                    }
                }),
                "Pay");

        if(isEdy) {
            webView.loadUrl("https://kotaku-blog.link/news.html?edy=1");
        } else {
            webView.loadUrl("https://kotaku-blog.link/news.html");
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // redirect url deal
                return false;
            }
        });
    }

    private void showToast(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
    }

    public static class WebCallBack {

        Callbacks callbacks;

        WebCallBack(Callbacks c) {
            callbacks = c;
        }

        @JavascriptInterface
        public void gotoNextPhase() {
            if (callbacks != null)
                callbacks.gotoNextPhase();
        }

        @JavascriptInterface
        public void checkBoxItem1(boolean value) {
            if (callbacks != null)
                callbacks.checkBoxItem1(value);
        }

        @JavascriptInterface
        public void checkBoxItem2(boolean value) {
            if (callbacks != null)
                callbacks.checkBoxItem2(value);
        }
    }

    interface Callbacks {
        void gotoNextPhase();

        void checkBoxItem1(boolean value);

        void checkBoxItem2(boolean value);
    }

    @Override
    public void onBackPressed() {
        if(webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }
}
