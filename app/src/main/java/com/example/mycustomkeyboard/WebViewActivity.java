package com.example.mycustomkeyboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.RelativeLayout;

public class WebViewActivity extends Activity {

    private WebView web;
    private MyKeyBoardViewWeb keyBoardView;
    private RelativeLayout root;
    private RelativeLayout keyboardRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        root = findViewById(R.id.root);
        keyBoardView = findViewById(R.id.mykeyboard);
        keyboardRoot = findViewById(R.id.mykeyboard_root);

        web = findViewById(R.id.web);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.addJavascriptInterface(new DemoJavaScriptInterface(), "demo");
        web.loadUrl("file:///android_asset/index.html");
    }

    public class DemoJavaScriptInterface {

        @JavascriptInterface
        public void showInput(final int height) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (keyBoardView.getVisibility() != View.VISIBLE) {
                        keyBoardView.setAttach(web, height, root, keyboardRoot);
                    }
                }
            });
        }
    }
}
