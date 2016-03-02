package org.example.xwalkembedded;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.Button;

import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkInitializer;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XWalkInitListenerActivity extends Activity implements XWalkInitializer.XWalkInitListener {
    private static final String TAG = "XWalkInitListener";
    XWalkInitializer mXWalkInitializer;
    private XWalkView mXWalkView;
    private XWalkCookieManager xWalkCookieManager;
    private String xwalkCookie;
    private long startAppTimer;
    private long xwalkReadyTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttom);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                loadXWalk();
            }
        });
    }

    private void loadXWalk() {
        startAppTimer = System.currentTimeMillis();

        mXWalkInitializer = new XWalkInitializer(this, this);
        mXWalkInitializer.initAsync();

        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        mXWalkView = new XWalkView(this, (AttributeSet) null);
        xWalkCookieManager = new XWalkCookieManager();
        initializeXWalkViewClients(mXWalkView);
        setContentView(mXWalkView);
    }

    @Override
    public void onXWalkInitCompleted() {
        onXWalkReady();
    }

    @Override
    public void onXWalkInitStarted() {

    }

    @Override
    public void onXWalkInitCancelled() {

    }

    @Override
    public void onXWalkInitFailed() {

    }

    //    @Override
    protected void onXWalkReady() {
        xwalkReadyTimer = System.currentTimeMillis();
        Log.d(TAG, "====on xwalk ready timer is " + (startAppTimer - xwalkReadyTimer));
//        final String commandLineParams [] = { "--show-fps-counter" };
//        CommandLine.getInstance().appendSwitchesAndArguments(commandLineParams);

        //file:///android_asset/image.html
        //http://m.weibo.cn
        //http://m.taobao.com
        //about:blank
        mXWalkView.load(null, "<html><head>test<a href=\"http://wap.bai.com\">中文</a></html>");
//        mXWalkView.load("http://www.baidu.com", null);
//        mXWalkView.setBackgroundColor(Color.TRANSPARENT);
//        mXWalkView.setBackgroundColor(Color.RED);
        Log.d(TAG, "====on after load url timer is " + (xwalkReadyTimer - System.currentTimeMillis()));
//

        String data = "<!doctype html><html><head><meta content=\"text/html;charset=utf-8\"></head><body><a>dfdfdf你好</a></body></html>";
        String encodeData = Uri.encode(data);
//	    mXWalkView.load("index.html",encodeData);
//	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//	            WindowManager.LayoutParams.FLAG_FULLSCREEN);

//	    mXWalkView.setOnKeyListener(new TestOnKeyListener());
//	    mXWalkView.setFocusable(true);
//	    mXWalkView.requestFocus();
        //
//	    mXWalkView.setUIClient(new TestXWalkUIClient(this.getApplicationContext(), mXWalkView));
        List<HttpCookie> cookieList = HttpCookie.parse("_T_WM=485387a4681594220c4c1dc196db9daf; expires=Tue, 10-Nov-2015 05:57:30 GMT; path=/; domain=.weibo.cn");
        for (HttpCookie httpCookie : cookieList) {
            Log.d("fujunwei", "====d= " + httpCookie.toString() + " " + httpCookie.getDomain() + " " + httpCookie.getMaxAge() + httpCookie.getPath());
        }

        //Hash map testing
        Map<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("Server", "Tengine");
        tempMap.put("Date", "Wed, 11 Nov 2015 08:50:50 GMT");
        tempMap.put("Set-Cookie", "M_WEIBOCN_PARAMS=23fasdecec; expires=Wed, 11-Nov-2015 09:00:50 GMT; path=/; domain=.weibo.cn");
        Log.d("fujunwei", "=====get Cookie==== " + tempMap.get("Set-Cookie"));
        Log.d("fujunwei", "=========return value " + getReturnValue());
        TextUtils.isEmpty(data);
    }

    int getReturnValue() {
        boolean test = true;
        return test ? 10 : 20;
    }

    private void initializeXWalkViewClients(XWalkView xwalkView) {
        xwalkView.setResourceClient(new XWalkResourceClient(xwalkView) {
            @Override
            public void onProgressChanged(XWalkView view, int newProgress) {

            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                Log.e("fujunwei", "====" + view.getTitle());
            }

            @Override
            public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
                Log.e("Override", url);
//	            	view.load(url, null);

                return false;
            }

            @Override
            public void onReceivedResponseHeaders(XWalkView view,
                                                  XWalkWebResourceRequest request,
                                                  XWalkWebResourceResponse response) {
                if (request.isForMainFrame() && response.getStatusCode() < 404) {
                    Map<String, String> headers = response.getResponseHeaders();
                    Log.d("fujunwei", "=====onReceivedResponseHeaders size " + headers.size() + " ==== " + response.getMimeType());

                    for (String name : headers.keySet()) {
                        Log.d("fujunwei", "+++++onReceivedResponseHeaders " + name + " " + headers.get(name));
                    }
                    Log.d("fujunwei", "==========onReceivedResponseHeaders " + headers.get("Set-Cookie"));
                }
            }
        });

        xwalkView.setUIClient(new XWalkUIClient(xwalkView) {
            @Override
            public void onReceivedTitle(XWalkView view, String title) {
                Log.d("fujunwei", "====== the title " + title);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        xwalkCookie = xWalkCookieManager.getCookie("http://m.weibo.cn");
                        Log.d("fujunwei", "========the cookies " + xwalkCookie);
//                        CookieStore cookieStore = xWalkCookieManager.?
                    }
                }, 2000);
            }

            // File Chooser
            @Override
            public void openFileChooser(XWalkView view, final ValueCallback<Uri> uploadFile, String acceptType, String capture) {
                uploadFile.onReceiveValue(null);

            }

            @Override
            public void onPageLoadStarted(XWalkView view, String url) {
                Log.d("fujunwei", "=====onPageLoadStarted " + url);
            }

            @Override
            public void onPageLoadStopped(XWalkView view, String url, XWalkUIClient.LoadStatus status) {
//                if (!loadOnce) {
//                    loadOnce = true;
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mXWalkView.load("about:blank", null);
//                        }
//                    }, 2000);
//                }
                Log.d("fujunwei", "=====onPageLoadStopped " + url);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mXWalkView.onActivityResult(requestCode, resultCode, data);
    }

    private void setKeyListener() {
        //点击后退按钮,让WebView后退一页(也可以覆写Activity的onKeyDown方法)
        mXWalkView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "====webview onKey");
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {  //表示按返回键
                        Log.d(TAG, "====webview setOnKeyListener");
                        return true;    //已处理
                    }
                }
                return false;
            }
        });

    }
}
