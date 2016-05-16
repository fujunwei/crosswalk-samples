package org.example.xwalkembedded;

import android.util.Log;

import org.example.socketproxy.Config;
import org.example.socketproxy.DownloadThread;
import org.example.socketproxy.Utils;

import java.io.File;
import java.net.URI;

/**
 * Created by junweifu on 5/15/2016.
 */
public class PrebufferData {
    final static public int SIZE =  (int) (100 * 1024 * 1024);
    final static public String TAG = "PrebufferData";
    DownloadThread download;

    public PrebufferData(String url) {
        new File(Utils.getBufferDir()).mkdirs();//创建预加载文件的文件夹
        Utils.clearCacheFile(Utils.getBufferDir());//清除前面的预加载文件

        try {
            String prebufferFilePath = prebuffer(url, SIZE);
            Log.e(TAG, "==== 预加载文件：" + prebufferFilePath + "\n" + url);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            Log.e(TAG, Utils.getExceptionMessage(ex));
        }
    }

    /**
     * 把URL提前下载在SD卡，实现预加载
     * @param urlString
     * @return 返回预加载文件名
     * @throws Exception
     */
    public String prebuffer(String urlString,int size) throws Exception{
        if(download!=null && download.isDownloading())
            download.stopThread();

        URI tmpURI=new URI(urlString);
        String fileName=Utils.getValidFileName(tmpURI.getPath());
        String filePath=Utils.getBufferDir()+"/"+fileName;

        download=new DownloadThread(urlString,filePath,size);
        download.startThread();

        return filePath;
    }
}
