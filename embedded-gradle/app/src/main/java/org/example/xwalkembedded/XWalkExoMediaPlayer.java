package org.example.xwalkembedded;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;

//import org.example.player.ExoMediaPlayer;
import org.example.player.DashRendererBuilder;
import org.example.player.DemoPlayer;
import org.example.player.ExtractorRendererBuilder;
import org.example.player.HlsRendererBuilder;
import org.example.player.SmoothStreamingRendererBuilder;
import org.xwalk.core.XWalkExMediaPlayer;
import org.xwalk.core.XWalkView;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.MediaFormat;
import com.google.android.exoplayer.drm.UnsupportedDrmException;
import com.google.android.exoplayer.metadata.id3.GeobFrame;
import com.google.android.exoplayer.metadata.id3.Id3Frame;
import com.google.android.exoplayer.metadata.id3.PrivFrame;
import com.google.android.exoplayer.metadata.id3.TxxxFrame;
import com.google.android.exoplayer.text.CaptionStyleCompat;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.SubtitleLayout;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.MimeTypes;
import com.google.android.exoplayer.util.Util;
import com.google.android.exoplayer.util.VerboseLogUtil;

import java.io.FileDescriptor;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by fujunwei on 16-5-11.
 */
public class XWalkExoMediaPlayer extends XWalkExMediaPlayer implements SurfaceHolder.Callback,
        DemoPlayer.Listener, DemoPlayer.Id3MetadataListener{
    static final String TAG = "ExoMediaPlayer";
    private static final int MENU_GROUP_TRACKS = 1;
    private static final int ID_OFFSET = 2;
    private Context mContext;

    private DemoPlayer player;
    //    private DebugTextViewHelper debugViewHelper;
    private boolean playerNeedsPrepare;
    private EventLogger eventLogger;
    private DebugTextViewHelper debugViewHelper;

    private Uri contentUri;
    private int contentType;
    private String contentId;
    private String provider;

    public static final String PROXY_HOST = "140.207.47.119";
    public static final int PROXY_HTTP_PORT = 10010;
    private String proxyHost;
    private int proxyPort;

    MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    MediaPlayer.OnCompletionListener mCompletionListener;
    MediaPlayer.OnPreparedListener mPreparedListener;
    MediaPlayer.OnSeekCompleteListener mSeekCompleteListener;
    MediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener;
    MediaPlayer.OnErrorListener mErrorListener;

    SurfaceView mSurfaceView;
    XWalkView mXWalkView;

    private int mBufferedPercentage;
    private int mVideoWidth;
    private int mVideoHeight;
    Map<String, String> mHeaders;

    public XWalkExoMediaPlayer(Context context, XWalkView xWalkView, SurfaceView surfaceView) {
        mContext = context;
        mXWalkView = xWalkView;
        mSurfaceView = surfaceView;
    }

    public void updateProxySetting(String host, int port) {
        proxyHost = host;
        proxyPort = port;
    }

    @Override
    public void prepareAsync() {
        Log.d(TAG, "==== in prepareAsync ");
        preparePlayer(true);
    }

    @Override
    public void setSurface(Surface surface) {
        Log.d(TAG, "==== in setSurface ");
        if (surface == null) {
            Log.e(TAG, "==== Surface is null");
            return;
        }
        player.setSurface(surface);//mSurfaceView.getHolder().getSurface()
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) {
        configurePlayingSource(uri, headers);

//        new PrebufferData(uri.toString());
    }

//    @Override
//    public void setDataSource (FileDescriptor fd, long offset, long length) {
//        // super.setDataSource(fd, offset, length);
//    }
//
    @Override
    public void setDataSource (Context context, Uri uri) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "Crosswalk");
        configurePlayingSource(uri, headers);
    }

    @Override
    public boolean isPlaying() {
        Log.d(TAG, "==== in isPlaying ");
        return player == null ? false : player.isPlaying();
    }

    @Override
    public int getVideoWidth() {
        Log.d(TAG, "==== in getVideoWidth ");
        return mVideoWidth;
    }

    @Override
    public int getVideoHeight() {
        Log.d(TAG, "==== in getVideoHeight ");
        return mVideoHeight;
    }

    @Override
    public int getCurrentPosition() {
//        Log.d(TAG, "==== in getCurrentPosition " + player.getBufferedPercentage());
        if (mBufferedPercentage != player.getBufferedPercentage()) {
            mBufferedPercentage = player.getBufferedPercentage();
            mBufferingUpdateListener.onBufferingUpdate(null, mBufferedPercentage);
        }
        return (int) player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        Log.d(TAG, "==== in getDuration ");
        return (int) player.getDuration();
    }

    @Override
    public void release() {
        Log.d(TAG, "==== in release ");
        releasePlayer();
    }

    @Override
    public void setVolume(float volume1, float volume2) {
        Log.d(TAG, "==== in setVolume ");
    }

    @Override
    public void start() {
        Log.d(TAG, "==== in start ");
        player.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        Log.d(TAG, "==== in pause ");
        player.setPlayWhenReady(false);
    }

    @Override
    public void seekTo(int msec) {
        Log.d(TAG, "==== in seekTo ");
        player.seekTo(msec);
        mSeekCompleteListener.onSeekComplete(null);
    }

    @Override
    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener listener) {
        Log.d(TAG, "==== in setOnBufferingUpdateListener ");
        mBufferingUpdateListener = listener;
    }

    @Override
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        Log.d(TAG, "==== in setOnCompletionListener ");
        mCompletionListener = listener;
    }

    @Override
    public void setOnErrorListener(MediaPlayer.OnErrorListener listener) {
        Log.d(TAG, "==== in setOnErrorListener ");
        mErrorListener = listener;
    }

    @Override
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener listener) {
        Log.d(TAG, "==== in setOnPreparedListener ");
        mPreparedListener = listener;
    }

    @Override
    public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener listener) {
        Log.d(TAG, "==== in setOnSeekCompleteListener ");
        mSeekCompleteListener = listener;
    }

    @Override
    public void setOnVideoSizeChangedListener(MediaPlayer.OnVideoSizeChangedListener listener) {
        Log.d(TAG, "==== in setOnVideoSizeChangedListener ");
        mVideoSizeChangedListener = listener;
    }

    private void configurePlayingSource(Uri uri, Map<String, String> headers) {
        Log.d(TAG, "==== in setDataSource ");
        contentUri = uri;//Uri.parse("http://122.96.25.242:8088/war.mp4");//uri;
        contentType = inferContentType(contentUri, "");
        contentId = "Demo Testing".toLowerCase(Locale.US).replaceAll("\\s", "");
        provider = "";

        mHeaders = headers;
    }

    // Internal methods

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        switch (contentType) {
            case Util.TYPE_SS:
                return new SmoothStreamingRendererBuilder(mContext, mHeaders, contentUri.toString(),
                        new SmoothStreamingTestMediaDrmCallback(), proxyHost, proxyPort);
            case Util.TYPE_DASH:
                return new DashRendererBuilder(mContext, mHeaders, contentUri.toString(),
                        new WidevineTestMediaDrmCallback(contentId, provider), proxyHost, proxyPort);
            case Util.TYPE_HLS:
                return new HlsRendererBuilder(mContext, mHeaders, contentUri.toString(),
                        proxyHost, proxyPort);
            case Util.TYPE_OTHER:
                return new ExtractorRendererBuilder(mContext, mHeaders, contentUri,
                        proxyHost, proxyPort);
            default:
                throw new IllegalStateException("Unsupported type: " + contentType);
        }
    }

    public void preparePlayer(boolean playWhenReady) {
        if (player == null) {
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.setMetadataListener(this);
            playerNeedsPrepare = true;
            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
//            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
//            debugViewHelper.start();
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
        }
//        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(playWhenReady);
    }

    public void releasePlayer() {
        if (player != null) {
//            debugViewHelper.stop();
//            debugViewHelper = null;
            player.release();
            player = null;
            eventLogger.endSession();
            eventLogger = null;
            mBufferedPercentage = 0;
        }
    }

    // DemoPlayer.Listener implementation

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch(playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                mBufferingUpdateListener.onBufferingUpdate(null, player.getBufferedPercentage());
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                mCompletionListener.onCompletion(null);
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                text += "ready";
                mPreparedListener.onPrepared(null);
                break;
            default:
                text += "unknown";
                break;
        }
        Log.d(TAG, "====onStateChanged " + text);
    }

    @Override
    public void onError(Exception e) {
        Log.d(TAG, "====onError ");
        String errorString = null;
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            errorString = mContext.getString(Util.SDK_INT < 18 ? R.string.error_drm_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown);
        } else if (e instanceof ExoPlaybackException
                && e.getCause() instanceof MediaCodecTrackRenderer.DecoderInitializationException) {
            // Special case for decoder initialization failures.
            MediaCodecTrackRenderer.DecoderInitializationException decoderInitializationException =
                    (MediaCodecTrackRenderer.DecoderInitializationException) e.getCause();
            if (decoderInitializationException.decoderName == null) {
                if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                    errorString = mContext.getString(R.string.error_querying_decoders);
                } else if (decoderInitializationException.secureDecoderRequired) {
                    errorString = mContext.getString(R.string.error_no_secure_decoder,
                            decoderInitializationException.mimeType);
                } else {
                    errorString = mContext.getString(R.string.error_no_decoder,
                            decoderInitializationException.mimeType);
                }
            } else {
                errorString = mContext.getString(R.string.error_instantiating_decoder,
                        decoderInitializationException.decoderName);
            }
        }
        if (errorString != null) {
            Toast.makeText(mContext.getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
        }
        playerNeedsPrepare = true;

        mErrorListener.onError(null, MediaPlayer.MEDIA_ERROR_UNKNOWN, MediaPlayer.MEDIA_ERROR_UNSUPPORTED);
    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees,
                                   float pixelWidthAspectRatio) {
        Log.d(TAG, "==== onVideoSizeChanged " + width + " " + height);
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoSizeChangedListener.onVideoSizeChanged(null, width, height);
    }

    // DemoPlayer.MetadataListener implementation

    @Override
    public void onId3Metadata(List<Id3Frame> id3Frames) {
        for (Id3Frame id3Frame : id3Frames) {
            if (id3Frame instanceof TxxxFrame) {
                TxxxFrame txxxFrame = (TxxxFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: description=%s, value=%s", txxxFrame.id,
                        txxxFrame.description, txxxFrame.value));
            } else if (id3Frame instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: owner=%s", privFrame.id, privFrame.owner));
            } else if (id3Frame instanceof GeobFrame) {
                GeobFrame geobFrame = (GeobFrame) id3Frame;
                Log.i(TAG, String.format("ID3 TimedMetadata %s: mimeType=%s, filename=%s, description=%s",
                        geobFrame.id, geobFrame.mimeType, geobFrame.filename, geobFrame.description));
            } else {
                Log.i(TAG, String.format("ID3 TimedMetadata %s", id3Frame.id));
            }
        }
    }

    // SurfaceHolder.Callback implementation

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
        }
    }
    /**
     * Makes a best guess to infer the type from a media {@link Uri} and an optional overriding file
     * extension.
     *
     * @param uri The {@link Uri} of the media.
     * @param fileExtension An overriding file extension.
     * @return The inferred type.
     */
    private static int inferContentType(Uri uri, String fileExtension) {
        String lastPathSegment = !TextUtils.isEmpty(fileExtension) ? "." + fileExtension
                : uri.getLastPathSegment();
        Log.e(TAG, "====Get uri content type " + lastPathSegment);
        return Util.inferContentType(lastPathSegment);
    }

}
