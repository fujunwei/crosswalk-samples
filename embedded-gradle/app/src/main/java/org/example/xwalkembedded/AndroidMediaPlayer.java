package org.example.xwalkembedded;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.id3.GeobFrame;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.metadata.id3.PrivFrame;
import com.google.android.exoplayer.metadata.id3.TxxxFrame;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.Util;

import org.example.player.DashRendererBuilder;
import org.example.player.DemoPlayer;
import org.example.player.ExtractorRendererBuilder;
import org.example.player.HlsRendererBuilder;
import org.example.player.SmoothStreamingRendererBuilder;
import org.example.socketproxy.HttpGetProxy;
import org.xwalk.core.XWalkExMediaPlayer;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

//import org.example.player.ExoMediaPlayer;

/**
 * Created by fujunwei on 16-5-11.
 */
public class AndroidMediaPlayer extends XWalkExMediaPlayer {
    static final String TAG = "AndroidMediaPlayer";

    SurfaceView mSurfaceView;
    XWalkView mXWalkView;
    Context mContext;

    MediaPlayer mMediaPlayer;
    StreamProxy mProxy;

    static private final int PREBUFFER_SIZE= 4*1024*1024;
    private long startTimeMills;
    private HttpGetProxy proxy;
    String id = "";

    public AndroidMediaPlayer(Context context, XWalkView xWalkView, SurfaceView surfaceView) {
        mContext = context;
        mXWalkView = xWalkView;
        mSurfaceView = surfaceView;
    }

    MediaPlayer getMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        return mMediaPlayer;
    }

    private void releaseMediaPlayer() {
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    @Override
    public void prepareAsync() {
        Log.d(TAG, "==== in prepareAsync ");
        getMediaPlayer().prepareAsync();
    }

    @Override
    public void setSurface(Surface surface) {
        Log.d(TAG, "==== in setSurface ");
        if (surface == null) {
            Log.d(TAG, "==== surface is null ");
        }
        getMediaPlayer().setSurface(surface);
    }

    @Override
    public void setDataSource(FileDescriptor fd, long offset, long length) {
        Log.d(TAG, "=====setDataSource " + fd.toString());
    }

    @Override
    public void setDataSource(Context context, Uri uri) {
        Log.d(TAG, "=====setDataSource " + uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) {
        Log.d(TAG, "=====ddd setDataSource " + uri);
//        createHttpConnectionProxy(getMediaPlayer(), context, uri, headers);
        createSocketProxy(getMediaPlayer(), context, uri, headers);
    }

    @Override
    public boolean isPlaying() {
        Log.d(TAG, "==== in isPlaying " + getMediaPlayer().isPlaying());
        return getMediaPlayer().isPlaying();
    }

    @Override
    public int getVideoWidth() {
        Log.d(TAG, "==== in getVideoWidth " + getMediaPlayer().getVideoWidth());
        return getMediaPlayer().getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        Log.d(TAG, "==== in getVideoHeight " + getMediaPlayer().getVideoHeight());
        return getMediaPlayer().getVideoHeight();
    }

    @Override
    public int getCurrentPosition() {
        return getMediaPlayer().getCurrentPosition();
    }

    @Override
    public int getDuration() {
        Log.d(TAG, "==== in getDuration " + getMediaPlayer().getDuration());
        return getMediaPlayer().getDuration();
    }

    @Override
    public void release() {
        Log.d(TAG, "==== in release ");
        releaseMediaPlayer();
    }

    @Override
    public void setVolume(float volume1, float volume2) {
        Log.d(TAG, "==== in setVolume ");
    }

    @Override
    public void start() {
        Log.d(TAG, "==== in start ");
        getMediaPlayer().start();
    }

    @Override
    public void pause() {
        Log.d(TAG, "==== in pause ");
        getMediaPlayer().pause();
    }

    @Override
    public void seekTo(int msec) {
        Log.d(TAG, "==== in seekTo ");
        getMediaPlayer().pause();
    }

    @Override
    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
        Log.d(TAG, "==== in setOnBufferingUpdateListener ");
        getMediaPlayer().setOnBufferingUpdateListener(listener);
    }

    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        Log.d(TAG, "==== in setOnCompletionListener ");
        getMediaPlayer().setOnCompletionListener(listener);
    }

    @Override
    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        Log.d(TAG, "==== in setOnErrorListener ");
        getMediaPlayer().setOnErrorListener(listener);
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        Log.d(TAG, "==== in setOnPreparedListener ");
        getMediaPlayer().setOnPreparedListener(listener);
    }

    @Override
    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener) {
        Log.d(TAG, "==== in setOnSeekCompleteListener ");
        getMediaPlayer().setOnSeekCompleteListener(listener);
    }

    @Override
    public void setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener listener) {
        getMediaPlayer().setOnVideoSizeChangedListener(listener);
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

        Log.d(TAG, "======local url 222222 ");
        String proxyUrl = proxy.getLocalURL(id);
        Log.d(TAG, "======local url " + proxyUrl);
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
            mProxy = new StreamProxy();
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

    static public String getBufferDir(){
        String bufferDir = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/ProxyBuffer/files";
        return bufferDir;
    }

}
