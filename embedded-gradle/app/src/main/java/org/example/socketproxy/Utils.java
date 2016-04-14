package org.example.socketproxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.client.RedirectLocations;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

/**
 * 工具类
 * @author hellogv
 *
 */
public class Utils {
	private static final String TAG="com.proxy.utils";
	
	/**
	 * 获取重定向后的URL，即真正有效的链接
	 * @param urlString
	 * @return
	 */
	public static String getRedirectUrl(String urlString){
		URL url;
		try {
			url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setInstanceFollowRedirects(false);
			if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_MOVED_PERM)
				return urlConnection.getHeaderField("Location");

			if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_MOVED_TEMP)
				return urlConnection.getHeaderField("Location");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return urlString;
	}

	static protected String getSubString(String source,String startStr,String endStr){
		int startIndex=source.indexOf(startStr)+startStr.length();
		int endIndex=source.indexOf(endStr,startIndex);
		return source.substring(startIndex, endIndex);
	}
	
	/**
	 * 获取有效的文件名
	 * @param str
	 * @return
	 */
	static protected String getValidFileName(String str)
    {
        str=str.replace("\\","");
        str=str.replace("/","");
        str=str.replace(":","");
        str=str.replace("*","");
        str=str.replace("?","");
        str=str.replace("\"","");
        str=str.replace("<","");
        str=str.replace(">","");
        str=str.replace("|","");
        str=str.replace(" ","_");    //前面的替换会产生空格,最后将其一并替换掉
        return str;
    }
	
	/**
	 * 获取外部存储器可用的空间
	 * @return
	 */
	static protected long getAvailaleSize(String dir) {
		StatFs stat = new StatFs(dir);//path.getPath());
		long totalBlocks = stat.getBlockCount();// 获取block数量
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize; // 获取可用大小
	}
	
	/**
	 * 获取文件夹内的文件，按日期排序，从旧到新
	 * @param dirPath
	 * @return
	 */
	static private List<File> getFilesSortByDate(String dirPath) {
		List<File> result = new ArrayList<File>();
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if(files==null || files.length==0)
			return result;
		
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.valueOf(f1.lastModified()).compareTo(
						f2.lastModified());
			}
		});

		for (int i = 0; i < files.length; i++){
			result.add(files[i]);
			Log.i(TAG, i+":"+files[i].lastModified() + "---" + files[i].getPath());
		}
		return result;
	}
	
	/**
	 * 删除多余的缓存文件
	 * @param dirPath 缓存文件的文件夹路径
	 * @param maximun 缓存文件的最大数量
	 */
	static protected void asynRemoveBufferFile(final String dirPath,final int maximun) {
		new Thread() {
			public void run() {
				List<File> lstBufferFile = Utils.getFilesSortByDate(dirPath);
				while (lstBufferFile.size() > maximun) {
					Log.i(TAG, "---delete " + lstBufferFile.get(0).getPath());
					lstBufferFile.get(0).delete();
					lstBufferFile.remove(0);
				}
			}
		}.start();
	}
	
	public static String getExceptionMessage(Exception ex){
		String result="";
		StackTraceElement[] stes = ex.getStackTrace();
		for(int i=0;i<stes.length;i++){
			result=result+stes[i].getClassName() 
			+ "." + stes[i].getMethodName() 
			+ "  " + stes[i].getLineNumber() +"line"
			+"\r\n";
		}
		return result;
	}
}
