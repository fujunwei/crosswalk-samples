package org.example.xwalkembedded;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Proxy;
import android.net.ProxyInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;

//import org.xwalk.core.XWalkGeolocationCallback;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import android.util.ArrayMap;
import android.widget.VideoView;

import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.util.Util;

public class XWalkWebViewActivity extends AppCompatActivity implements AudioCapabilitiesReceiver.Listener {
    private XWalkView mXWalkView;
    final static  String TAG = "fujunwei";
    private StreamProxy mProxy;
    private SocketProxy mSocketProxy;

    private SurfaceView surfaceView;
    private XWalkExoMediaPlayer mXWalkExoMediaPlayer;
    private AndroidMediaPlayer mAndroidMediaPlayer;

    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
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

        mXWalkView = (XWalkView) findViewById(R.id.xwalkWebView);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
//        mXWalkView.setUIClient(new XWalkUIClient(mXWalkView) {
//            @Override
//            public void onGeolocationPermissionsShowPrompt(XWalkView view, String origin,
//                                                           XWalkGeolocationCallback callback) {
//            }
//        });
        XWalkSettings settings = mXWalkView.getSettings();
        mXWalkExoMediaPlayer = new XWalkExoMediaPlayer(this, mXWalkView, surfaceView);
        mXWalkExoMediaPlayer.updateProxySetting("140.207.47.119", 10010);
        mAndroidMediaPlayer = new AndroidMediaPlayer(this, mXWalkView, surfaceView);

        mXWalkView.setExMediaPlayer(mXWalkExoMediaPlayer);
//        mXWalkView.load("http://crosswalk-project.org/", null);
        Log.d(TAG, "=====in crosswalk webview ");

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(this, this);
        audioCapabilitiesReceiver.register();
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
            MediaPlayer mPlayer = new MediaPlayer();
            String url = "http://programmerguru.com/android-tutorial/wp-content/uploads/2013/04/hosannatelugu.mp3";
            try {
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(url);
                mPlayer.prepare();
                mPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        } else if (id == R.id.action_proxy) {
//            updateNewProxy();
//            setProxyKK(this, "122.96.25.242", 9399); // Error proxy ip child-p.intel.com:912
//            mXWalkView.proxySettingsChanged("", 9396, "", null);
            VideoView m;
        } else if (id == R.id.action_baidu) {
//            if (mProxy == null) {
//                mProxy = new StreamProxy();
//                mProxy.init();
//                mProxy.start();
//            }
//            mSocketProxy = new SocketProxy(8123);
//            try {
//                mSocketProxy.startProxy();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            String[] a = {"*.intel.com", "*.intel2.com"};
//            mXWalkView.proxySettingsChanged("122.96.25.242", 9399, "", a);
            mXWalkView.proxySettingsChanged("140.207.47.119", 10010, "", a);
            mXWalkView.load("file:///android_asset/index.html", null);
        } else if (id == R.id.action_video) {
//            if (mProxy == null) {
//                mProxy = new StreamProxy();
//                mProxy.init();
//                mProxy.start();
//            }
//            mSocketProxy = new SocketProxy(8123);
//            try {
//                mSocketProxy.startProxy();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            mXWalkView.load("file:///android_asset/video.html", null);//http://www.zhangxinxu.com/study/201003/html5-video-mp4.html

            //http://120.52.73.7/103.38.59.16/youku/6572A850BE73078284C1F593A/03002001005721B16ECE05003E88037E969A53-228F-72D7-A8C5-B434E9A4547F.mp4
            playWithExoPlayer(Uri.parse("http://101.227.216.142/vhot2.qqvideo.tc.qq.com/j0199q9hom4.mp4?vkey=7086CFBEFB7DFD27C61719C7000DB9B78B8DBECC336B74D4DC36C4AB018CB2B569AE533D79FFCA0D6C79AA0CA0B51C6ABB5235A1BC46D39910ED1AE4B36A70C1B6634E18EC59E7B3BFCE1708631C8D10458ABEED44DFCA8A&br=60&platform=2&fmt=auto&level=0&sdtfrom=v5010&locid=67551d9a-bf0d-4b25-bfc8-b2ee2334f02e&size=4580061&ocid=256578988"));
        } else if (id == R.id.action_enableProxy) {
//            mXWalkExoMediaPlayer.updateProxySetting("122.96.25.242", 9399);
            mXWalkExoMediaPlayer.updateProxySetting("140.207.47.119", 10010);
        } else if (id == R.id.action_disableProxy) {
            mXWalkExoMediaPlayer.updateProxySetting("", -1);
//            mXWalkView.setVisibility(View.INVISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    void playWithExoPlayer(Uri uri) {
        Intent mpdIntent = new Intent(this, PlayerActivity.class)
                .setData(uri)
                .putExtra(PlayerActivity.CONTENT_ID_EXTRA, "Demo Testing".toLowerCase(Locale.US).replaceAll("\\s", "")) //sample.contentId
                .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, Util.TYPE_OTHER) //sample.type
                .putExtra(PlayerActivity.PROVIDER_EXTRA, ""); //sample.provider
        startActivity(mpdIntent);
    }


    // AudioCapabilitiesReceiver.Listener methods

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        mXWalkExoMediaPlayer.releasePlayer();
        mXWalkExoMediaPlayer.preparePlayer(true);
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
