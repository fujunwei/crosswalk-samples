package org.example.xwalkembedded;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Proxy;
import android.net.ProxyInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by fujunwei on 16-3-23.
 */
public class SystemWebViewActivity extends AppCompatActivity {
    private WebView mSystemWebView;
    final static  String TAG = "fujunwei";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mSystemWebView = (WebView) findViewById(R.id.systemWebView);
        WebSettings settings = mSystemWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(true);
//        mSystemWebView.loadUrl("http://crosswalk-project.org/");
        mSystemWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        Log.d(TAG, "=====in system webview ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_proxy) {
//            updateNewProxy();
            setProxyKK(this, "child-p.intel.com", 912);
        } else if (id == R.id.action_baidu) {
            mSystemWebView.loadUrl("http://www.baidu.com/");
        } else if (id == R.id.action_video) {
            mSystemWebView.loadUrl("file:///android_asset/video.html");//http://www.zhangxinxu.com/study/201003/html5-video-mp4.html
        }

        return super.onOptionsItemSelected(item);
    }

    // from https://stackoverflow.com/questions/19979578/android-webview-set-proxy-programatically-kitkat
    private static boolean setProxyKK(Activity activity, String host, int port) {
        Log.d(TAG, "Setting proxy with >= 4.4 API.");

        Context appContext = activity.getApplicationContext();

        try {
            Class applictionCls = Class.forName("android.app.Application");
            Field loadedApkField = applictionCls.getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(appContext);
            Class loadedApkCls = Class.forName("android.app.LoadedApk");
            Field receiversField = loadedApkCls.getDeclaredField("mReceivers");
            receiversField.setAccessible(true);
            ArrayMap receivers = (ArrayMap) receiversField.get(loadedApk);
            for (Object receiverMap : receivers.values()) {
                for (Object rec : ((ArrayMap) receiverMap).keySet()) {
                    Class clazz = rec.getClass();
                    if (clazz.getName().contains("ProxyChangeListener")) {
                        Method onReceiveMethod = clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
                        Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);

                        /*********** optional, may be need in future *************/
                        String className;
                        String proxyInfo;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            className = "android.net.ProxyProperties";
                            proxyInfo = "proxy";
                        } else {
                            className = "android.net.ProxyInfo";
                            proxyInfo = "android.intent.extra.PROXY_INFO";
                        }

                        Class cls = Class.forName(className);
                        Constructor constructor = cls.getConstructor(String.class, Integer.TYPE, String.class);
                        constructor.setAccessible(true);
                        Object proxyProperties = constructor.newInstance(host, port, "*.intel.com");
                        intent.putExtra(proxyInfo, (Parcelable) proxyProperties);
                        /*********** optional, may be need in future *************/

                        onReceiveMethod.invoke(rec, appContext, intent);
                    }
                }
            }

            Log.d(TAG, "Setting proxy with >= 4.4 API successful!");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return false;
    }


}
