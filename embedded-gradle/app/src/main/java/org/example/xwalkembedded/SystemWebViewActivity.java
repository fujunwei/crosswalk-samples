package org.example.xwalkembedded;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Proxy;
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
import java.net.ServerSocket;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        ServerSocket socket = new ServerSocket();

        return super.onOptionsItemSelected(item);
    }

    // Extract a ProxyConfig object from the supplied Intent's extra data
    // bundle. The android.net.ProxyProperties class is not exported from
    // the Android SDK, so we have to use reflection to get at it and invoke
    // methods on it. If we fail, return an empty proxy config (meaning
    // 'direct').
    // TODO(sgurun): once android.net.ProxyInfo is public, rewrite this.
    private String updateNewProxy() {
        try {
            final String buildDirectProxy = "buildDirectProxy";
            String className;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                className = "android.net.ProxyProperties";
            } else {
                className = "android.net.ProxyInfo";
            }
//            Class<?> cls = Class.forName(className);
//            Method buildProxy = cls.getDeclaredMethod(buildDirectProxy, new Class[]{String.class, int.class});
//            android.net.ProxyInfo proxyInfo = (ProxyInfo) buildProxy.invoke(cls, new Object[]{"", 912});

            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");

            //get ProxyProperties constructor
            Class[] proxyPropertiesCtorParamTypes = new Class[3];
            proxyPropertiesCtorParamTypes[0] = String.class;
            proxyPropertiesCtorParamTypes[1] = int.class;
            proxyPropertiesCtorParamTypes[2] = String.class;

            Constructor proxyPropertiesCtor = proxyPropertiesClass.getConstructor(proxyPropertiesCtorParamTypes);

            //create the parameters for the constructor
            Object[] proxyPropertiesCtorParams = new Object[3];
            proxyPropertiesCtorParams[0] = "child-p.intel.com";
            proxyPropertiesCtorParams[1] = 912;
            proxyPropertiesCtorParams[2] = null;

            //create a new object using the params
            Object proxySettings = proxyPropertiesCtor.newInstance(proxyPropertiesCtorParams);

            Intent intent = new Intent();
            intent.setAction(Proxy.PROXY_CHANGE_ACTION);
            Log.d("fujunwei", "====before proxy info");
            intent.putExtra("android.intent.extra.PROXY_INFO", (Parcelable)proxySettings);
            Log.d("fujunwei", "====after proxy info");

            sendBroadcast(intent);

        } catch (ClassNotFoundException ex) {
            Log.e(TAG, "Using no proxy configuration due to exception:" + ex);
            return null;
        } catch (NoSuchMethodException ex) {
            Log.e(TAG, "Using no proxy configuration due to exception:" + ex);
            return null;
        } catch (IllegalAccessException ex) {
            Log.e(TAG, "Using no proxy configuration due to exception:" + ex);
            return null;
        } catch (InvocationTargetException ex) {
            Log.e(TAG, "Using no proxy configuration due to exception:" + ex);
            return null;
        } catch (NullPointerException ex) {
            Log.e(TAG, "Using no proxy configuration due to exception:" + ex);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                        String CLASS_NAME = "android.net.ProxyProperties";
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                            CLASS_NAME = "android.net.ProxyProperties";
                        } else {
                            CLASS_NAME = "android.net.ProxyInfo";
                        }

                        Class cls = Class.forName(CLASS_NAME);
                        Constructor constructor = cls.getConstructor(String.class, Integer.TYPE, String.class);
                        constructor.setAccessible(true);
                        Object proxyProperties = constructor.newInstance(host, port, "*.intel.com");
                        intent.putExtra("proxy", (Parcelable) proxyProperties);
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
