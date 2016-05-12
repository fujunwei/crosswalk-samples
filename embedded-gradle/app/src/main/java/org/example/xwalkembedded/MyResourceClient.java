package org.example.xwalkembedded;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.example.socketproxy.HttpGetProxy;
import org.example.socketproxy.Utils;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkViewInternal;
import android.media.MediaPlayer.OnPreparedListener;

import com.google.android.exoplayer.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * Created by junweifu on 4/13/2016.
 */
public class MyResourceClient extends XWalkResourceClient {
    String TAG = "MyResourceClient";
    private HttpGetProxy proxy;
    static private final int PREBUFFER_SIZE= 4*1024*1024;
    private long startTimeMills;
    String id = "";
    private long waittime=8000;//等待缓冲时间
    boolean mOverrideResourceLoading = true;
    StreamProxySeek mProxy;
    Activity mActivity;

    public MyResourceClient(XWalkView xWalkView) {
        super(xWalkView);
    }

    public MyResourceClient(XWalkView xWalkView, Activity activity) {
        super(xWalkView);
        mActivity = activity;
    }

//    @Override
//    public boolean shouldOverrideResourceLoading(XWalkView view,
//            MediaPlayer mediaPlayer, Context context, Uri uri, Map<String, String> headers) {
//        Log.d(TAG, "======in shouldOverrideResourceLoading " + uri.toString());
//        if (!mOverrideResourceLoading) {
//            return false;
//        }
//
////        playWithExoPlayer(context, uri);
////        createSocketProxy(mediaPlayer, context, uri, headers);
////        createHttpConnectionProxy(mediaPlayer, context, uri, headers);
//        try {
//            mediaPlayer.setDataSource(context, Uri.parse("http://www.zhangxinxu.com/study/media/cat.mp4"), headers);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return true;
//    }

    void playWithExoPlayer(Context context, Uri uri) {
        Intent mpdIntent = new Intent(mActivity, PlayerActivity.class)
                .setData(uri)
                .putExtra(PlayerActivity.CONTENT_ID_EXTRA, "Demo Testing".toLowerCase(Locale.US).replaceAll("\\s", "")) //sample.contentId
                .putExtra(PlayerActivity.CONTENT_TYPE_EXTRA, Util.TYPE_OTHER) //sample.type
                .putExtra(PlayerActivity.PROVIDER_EXTRA, ""); //sample.provider
        mActivity.startActivity(mpdIntent);
    }

    void createSocketProxy(MediaPlayer mediaPlayer, Context context, Uri uri, Map<String, String> headers) {
        //创建预加载视频文件存放文件夹
        new File(getBufferDir()).mkdirs();

        // 初始化代理服务器
        proxy = new HttpGetProxy(getBufferDir(),// 预加载视频文件存放路径
                PREBUFFER_SIZE,// 预加载体积
                10);// 预加载文件上限

        id = System.currentTimeMillis() + "";
        try {
            proxy.startDownload(id, uri.toString(), true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        delayToStartPlay.sendEmptyMessageDelayed(0, waittime);
//
//        mediaPlayer.setOnPreparedListener(mOnPreparedListener);

        String proxyUrl = proxy.getLocalURL(id);
        Uri localUri = Uri.parse(proxyUrl);
        try {
            mediaPlayer.setDataSource(context, localUri, headers);
        } catch (IOException e) {
            Log.e(TAG, "Media player set data source failed : " + e);
        }
    }

    void createHttpConnectionProxy(MediaPlayer mediaPlayer,
                                   Context context, Uri uri,
                                   Map<String, String> headers) {
        if (mProxy == null) {
            mProxy = new StreamProxySeek();
            mProxy.init();
            mProxy.start();
        }
        String playurl = String.format(Locale.US, "http://127.0.0.1:%d/%s", 8123, uri.toString());
        Log.e(TAG, "====shouldOverrideResourceLoading " + uri.toString() + " \n new play url = " + playurl);
        try {
            mediaPlayer.setDataSource(context, Uri.parse(playurl), headers);
        } catch (IOException e) {
            Log.e(TAG, "Media player set data source failed : " + e);
        }
    }

//    private OnPreparedListener mOnPreparedListener=new OnPreparedListener(){
//        @Override
//        public void onPrepared(MediaPlayer mp) {
//            mVideoView.start();
//            long duration=System.currentTimeMillis() - startTimeMills;
//            Log.e(TAG,"等待缓冲时间:"+waittime+",首次缓冲时间:"+duration);
//        }
//    };
//
//    private Handler delayToStartPlay = new Handler() {
//        public void handleMessage(Message msg) {
//            startTimeMills=System.currentTimeMillis();
//            String proxyUrl = proxy.getLocalURL(id);
//            mVideoView.setVideoPath(proxyUrl);
//        }
//    };
//
    static public String getBufferDir(){
        String bufferDir = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/ProxyBuffer/files";
        return bufferDir;
    }

}
