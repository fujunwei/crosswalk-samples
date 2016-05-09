package org.example.xwalkembedded;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * Created by fujunwei on 16-4-1.
 */
public class HttpConnectionProxy implements Runnable {
    private static final String LOG_TAG = "HttpConnectionProxy";
    private static AsyncTask<Void, Integer, Integer> sActiveTask;

    private int port = 8123;

    private boolean isRunning = true;
    private ServerSocket socket;
    private Thread thread;

    public void init() {
        try {
            socket = new ServerSocket(port, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));
            socket.setSoTimeout(0);//5000
            Log.d(LOG_TAG, "port " + port + " obtained");
        } catch (UnknownHostException e) {
            Log.e(LOG_TAG, "Error initializing server", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error initializing server", e);
        }
    }

    public void start() {

        if (socket == null) {
            throw new IllegalStateException("Cannot start proxy; it has not been initialized.");
        }

        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        isRunning = false;

        if (thread == null) {
            throw new IllegalStateException("Cannot stop proxy; it has not been started.");
        }

        thread.interrupt();
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, "running");
        while (isRunning) {
            try {
                Log.d(LOG_TAG, "=== waiting client");
                Socket client = socket.accept();
                if (client == null) {
                    continue;
                }
                Log.d(LOG_TAG, "=== client connected");
                new XWalkDownloadTask(new DownloadListener(), client, readRequest(client)).execute();
            } catch (SocketTimeoutException e) {
                // Do nothing
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to client", e);
            }
        }
        Log.d(LOG_TAG, "Proxy interrupted. Shutting down.");
    }

    private String readRequest(Socket client) {
        InputStream is;
        String firstLine;
        try {
            is = client.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is),
                    8192);
            firstLine = reader.readLine();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error parsing request", e);
            return null;
        }

        if (firstLine == null) {
            Log.i(LOG_TAG, "Proxy client closed connection without a request.");
            return null;
        }

        StringTokenizer st = new StringTokenizer(firstLine);
        String method = st.nextToken();
        String uri = st.nextToken();
        Log.d(LOG_TAG, uri);
        String realUri = uri.substring(1);
        Log.d(LOG_TAG, "====" + realUri + " method = " + method);
//        request = new BasicHttpRequest(method, realUri);
        return realUri;
    }

    private class XWalkDownloadTask extends AsyncTask<Void, Integer, Integer> {
        private String TAG = "XWalkDownloadTask";
        private static final String XWALK_DOWNLOAD_DIR = "xwalk_download";
        private static final int DOWNLOAD_SUCCESS = 0;
        private static final int DOWNLOAD_FAILED = -1;

        private DownloadListener mListener;
        private String mDownloadUrl;
        private Socket mClient;

        XWalkDownloadTask(DownloadListener listener, Socket client, String url) {
            super();
            mListener = listener;
            mClient = client;
            mDownloadUrl = url;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "XWalkDownloadTask started, " + mDownloadUrl);
            sActiveTask = this;

            mListener.onDownloadStarted();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            if (mDownloadUrl == null) return DOWNLOAD_FAILED;

            InputStream input = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(mDownloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                // handle redirects ourselves if we do not allow cross-domain redirect
                connection.setInstanceFollowRedirects(true);
                connection.connect();
                Log.d(TAG, "====url.openConnection ");

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "====Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                    return DOWNLOAD_FAILED;
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();

                // 添加头信息
//                byte[] buffer = httpString.toString().getBytes();
//                int readBytes;
//                Log.d(LOG_TAG, "writing to client");
//                client.getOutputStream().write(buffer, 0, buffer.length);

                // Start streaming content.
                byte[] buff = new byte[1024 * 50];
                long total = 0;
                int readBytes;
                int count = 0;
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Log.d(TAG, "====Begin to read download data " + fileLength);
                while (isRunning && (readBytes = input.read(buff,  0, buff.length)) != -1) {
                    total += readBytes;
//                    publishProgress((int)total, fileLength);
                    Log.d(TAG, "====read data " + readBytes);
                    mClient.getOutputStream().write(buff, 0, readBytes);
                    mClient.getOutputStream().flush();
                }
                Log.d(TAG, "====end to read download data " + total);
            } catch (Exception e) {
                e.printStackTrace();
                return DOWNLOAD_FAILED;
            } finally {
                try {
                    if (mClient != null) mClient.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                }

                if (connection != null) connection.disconnect();
            }
            return DOWNLOAD_SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
//            Log.d(TAG, "XWalkDownloadTask updated: " + progress[0] + "/" + progress[1]);
            int percentage = 0;
            if (progress[1] > 0) percentage = (int) (progress[0] * 100.0 / progress[1]);
            mListener.onDownloadUpdated(percentage);
        }

        @Override
        protected void onCancelled(Integer result) {
            Log.d(TAG, "XWalkDownloadTask cancelled");
            sActiveTask = null;
            mListener.onDownloadCancelled();
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG, "XWalkDownloadTask finished, " + result);
            sActiveTask = null;

            if (result == DOWNLOAD_SUCCESS) {
//                mListener.onDownloadCompleted(Uri.fromFile(mDownloadedFile));
            } else {
                // Error codes is not used in download mode.
                mListener.onDownloadFailed(DOWNLOAD_FAILED, 0);
            }
        }
    }

    public class DownloadListener {
        void onDownloadStarted() {

        }

        void onDownloadUpdated(int var1) {

        }

        void onDownloadCancelled() {

        }

        void onDownloadCompleted(Uri var1) {

        }

        void onDownloadFailed(int var1, int var2) {

        }
    }
    /*if ((!TextUtils.isEmpty(url)) && (!url.startsWith("http://127.0.0.1")) && (url.startsWith("http://"))) {
+            StringBuilder localUrl = new StringBuilder().append("http://127.0.0.1:");
+            int proxyPort = 8123;
+            StringBuilder localUrl = localUrl.append(proxyPort).append("/").append(url.substring(7));
+            url = localurl.toString();
+               Log.d("fujunwei",  "======in setdataSource " + url);
+        }*/

}

