package org.example.xwalkembedded;

/**
 * Created by fujunwei on 16-3-2.
 */


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebResourceResponse;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

public class BackgroundTestActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    XWalkView mXwalkView;

    class MyResourceClient extends XWalkResourceClient {
        MyResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public WebResourceResponse shouldInterceptLoadRequest(XWalkView view, String url) {
            // Handle it here
            return super.shouldInterceptLoadRequest(view, url);
        }
    }

    class MyUIClient extends XWalkUIClient {
        MyUIClient(XWalkView view) {
            super(view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mXwalkView = new XWalkView(this);
        setContentView(mXwalkView);
        mXwalkView.setResourceClient(new MyResourceClient(mXwalkView));
        mXwalkView.setUIClient(new MyUIClient(mXwalkView));
        mXwalkView.addJavascriptInterface(this, "Test");
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        mXwalkView.load("file:///android_asset/background.html", null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mXwalkView != null) {
            mXwalkView.pauseTimers();
            mXwalkView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mXwalkView != null) {
            mXwalkView.resumeTimers();
            mXwalkView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXwalkView != null) {
            mXwalkView.onDestroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mXwalkView != null) {
            mXwalkView.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (mXwalkView != null) {
            mXwalkView.onNewIntent(intent);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, String.valueOf(event.getDisplayLabel()));
        return super.dispatchKeyEvent(event);
    }

    @JavascriptInterface
    public void white(){
        runOnUiThread(new Runnable() {
            public void run() {
                mXwalkView.setBackgroundColor(Color.WHITE);
            }
        });
    }

    @JavascriptInterface
    public void black(){
        runOnUiThread(new Runnable() {
            public void run() {
                mXwalkView.setBackgroundColor(Color.BLACK);
            }
        });
    }

    @JavascriptInterface
    public void transparent(){
        runOnUiThread(new Runnable() {
            public void run() {
                mXwalkView.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    @JavascriptInterface
    public void red(){
        runOnUiThread(new Runnable() {
            public void run() {
                mXwalkView.setBackgroundColor(Color.RED);
            }
        });
    }
}