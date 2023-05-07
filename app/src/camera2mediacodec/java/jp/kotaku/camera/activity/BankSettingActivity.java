package jp.kotaku.camera.activity;

import android.content.Context;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import jp.co.yumemi.android.code_check.R;

public class BankSettingActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_bank_setting);
        WebView webView = findViewById(R.id.bankWebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(false);
        webView.getSettings().setAllowContentAccess(false);
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        webView.getSettings().setTextZoom(100);
        webView.addJavascriptInterface(new WebCallBack(() -> showToast("Js call this callback interface!!")),
                "Pay");
//        webView.loadUrl("https://kotaku-blog.link/cancel.html");
        webView.loadUrl("file:///android_asset/news.html");
    }

    private void showToast(String content) {
        Toast.makeText(mContext, content, Toast.LENGTH_LONG).show();
        finish();
    }

    public static class WebCallBack {

        Callbacks callbacks;

        WebCallBack(Callbacks c) {
            callbacks = c;
        }

        @JavascriptInterface
        public void gotoSetting() {
            if (callbacks != null)
                callbacks.gotoSetting();
        }
    }

    interface Callbacks {
        void gotoSetting();
    }
}
