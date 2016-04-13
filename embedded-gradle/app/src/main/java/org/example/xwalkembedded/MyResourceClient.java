package org.example.xwalkembedded;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.example.socketproxy.C;
import org.example.socketproxy.HttpGetProxy;
import org.example.socketproxy.Utils;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.internal.XWalkViewInternal;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by junweifu on 4/13/2016.
 */
public class MyResourceClient extends XWalkResourceClient {
    String TAG = "MyResourceClient";
    private boolean enablePrebuffer=true;//预加载开关
    private HttpGetProxy proxy;

    public MyResourceClient(XWalkView xWalkView) {
        super(xWalkView);
    }

    @Override
    public boolean shouldOverrideResourceLoading(XWalkView view,
            MediaPlayer mediaPlayer, Context context, Uri uri, Map<String, String> headers) {
        String localAddress = "";

        new File(C.getBufferDir()).mkdirs();//创建预加载文件的文件夹
        Utils.clearCacheFile(C.getBufferDir());//清除前面的预加载文件

        if (enablePrebuffer) {//使用预加载
            //初始化代理服务器
            if (proxy == null) {
                proxy = new HttpGetProxy(9110);
                proxy.asynStartProxy();
            }
            String[] urls = proxy.getLocalURL(uri.toString());
            String mp4Url=urls[0];
            localAddress =urls[1];

            Log.e(TAG, "==== download Url  = " + mp4Url);
            try {
                String prebufferFilePath = proxy.prebuffer(mp4Url,
                        HttpGetProxy.SIZE);

                Log.e(TAG, "预加载文件：" + prebufferFilePath);
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
                Log.e(TAG, Utils.getExceptionMessage(ex));
            }
        }

        Log.e(TAG, "==== local address url  = " + localAddress);
        Uri localUri = Uri.parse(localAddress);
        try {
            mediaPlayer.setDataSource(context, localUri, headers);
        } catch (IOException e) {
            Log.e(TAG, "Media player set data source failed : " + e);
        }

        return true;
    }
}
