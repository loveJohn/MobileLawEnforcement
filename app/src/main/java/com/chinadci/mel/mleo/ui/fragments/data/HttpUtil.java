package com.chinadci.mel.mleo.ui.fragments.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.chinadci.mel.android.core.DciActivityManager;

public class HttpUtil {

	// 服务器请求超时 ConnectionTimeoutException
	private static final int REQUEST_TIMEOUT = 10000;// 设置请求超时10秒钟
	// 服务器响应超时 SocketTimeoutException
	private static final int SO_TIMEOUT = 10000; // 设置等待数据超时时间10秒钟

	private static final String APPLICATION_JSON = "application/json";

	public static String getDataWithoutCookie(String url, String paramStr) {
		String result = "";
		BufferedReader in = null;
		try {
			// String utf8Str = new String(paramStr.getBytes(),"UTF-8");
			if (paramStr != null) {
				url = url + "?" + paramStr;
			}
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			connection.setReadTimeout(REQUEST_TIMEOUT);
			connection.setConnectTimeout(SO_TIMEOUT);
			// 设置通用的请求属性
			// connection.setRequestProperty("accept", "*/*");
			// connection.setRequestProperty("connection", "Keep-Alive");
			// connection.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// String xxx = key + "--->" + map.get(key);
			// System.out.println(xxx);
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}
	
	public static String getDataWithoutCookie2(String url, String paramStr) {
		String result = "";
		BufferedReader in = null;
		try {
			String utf8Str = URLEncoder.encode(paramStr, "utf-8");
			if (paramStr != null) {
				url = url + "/" + utf8Str;
			}
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			connection.setReadTimeout(REQUEST_TIMEOUT);
			connection.setConnectTimeout(SO_TIMEOUT);
			// 设置通用的请求属性
			// connection.setRequestProperty("accept", "*/*");
			// connection.setRequestProperty("connection", "Keep-Alive");
			// connection.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// String xxx = key + "--->" + map.get(key);
			// System.out.println(xxx);
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public static String getDataAndCookie(String url, String paramStr) {
		String result = "";
		BufferedReader in = null;
		try {
			// String utf8Str = new String(paramStr.getBytes(),"UTF-8");
			if (paramStr != null) {
				url = url + "?" + paramStr;
			}
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			connection.setReadTimeout(REQUEST_TIMEOUT);
			connection.setConnectTimeout(SO_TIMEOUT);
			// 设置通用的请求属性
			// connection.setRequestProperty("accept", "*/*");
			// connection.setRequestProperty("connection", "Keep-Alive");
			// connection.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// String xxx = key + "--->" + map.get(key);
			// System.out.println(xxx);
			// }
			DciActivityManager.getInstance().setUserCookie(
					map.get("Set-Cookie").get(0));
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public static String get(String url, String paramStr) {
		String result = "";
		BufferedReader in = null;
		try {
			// String utf8Str=new String(paramStr.getBytes(),"UTF-8");
			if (paramStr != null) {
				url = url + "?" + paramStr;
			}
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			connection.setReadTimeout(REQUEST_TIMEOUT);
			connection.setConnectTimeout(SO_TIMEOUT);
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			if (DciActivityManager.getInstance().getUserCookie() != null) {
				connection.setRequestProperty("Cookie", DciActivityManager
						.getInstance().getUserCookie());
			}
			// connection.setRequestProperty("user-agent",
			// "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			// Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			// for (String key : map.keySet()) {
			// System.out.println(key + "--->" + map.get(key));
			// }
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	public static String post(String url, String jsonStr) {
		HttpURLConnection connection;
		String result = null;
		try {
			// 创建连接
			URL postUrl = new URL(url);
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(REQUEST_TIMEOUT);
			connection.setReadTimeout(SO_TIMEOUT);
			connection.setRequestMethod("POST");
			// connection.setUseCaches(false);
			// connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Accept", APPLICATION_JSON);
			connection.setRequestProperty("Content-Type", APPLICATION_JSON);
			if (DciActivityManager.getInstance().getUserCookie() != null) {
				connection.setRequestProperty("Cookie", DciActivityManager
						.getInstance().getUserCookie());
			}
			connection.connect();
			// POST请求
			if (jsonStr != null && !jsonStr.equals("")) {
				OutputStream out = connection.getOutputStream();
				// 注意中文提交(如果报错，要考虑中文的问题，去掉中文重试)
				// byte[] b = jsonStr.getBytes("ISO-8859-1");
				// String output = new String(b,"UTF-8");
				// out.write(output.getBytes());
				out.write(jsonStr.getBytes()); // 注意中文提交(如果报错，要考虑中文的问题，去掉中文重试)
				out.flush();
				out.close();
			}
			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String lines;
			StringBuilder sb = new StringBuilder("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "UTF-8");
				sb.append(lines);
			}
			result = sb.toString();
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String post(String url, String jsonStr, int connectTimeout,
			int readTimeout) {
		HttpURLConnection connection;
		String result = null;
		try {
			// 创建连接
			URL postUrl = new URL(url);
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(connectTimeout);
			connection.setReadTimeout(readTimeout);
			connection.setRequestMethod("POST");
			// connection.setUseCaches(false);
			// connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Accept", APPLICATION_JSON);
			connection.setRequestProperty("Content-Type", APPLICATION_JSON);
			connection.connect();
			// POST请求
			if (jsonStr != null && !jsonStr.equals("")) {
				OutputStream out = connection.getOutputStream();
				// 注意中文提交(如果报错，要考虑中文的问题，去掉中文重试)
				out.write(jsonStr.getBytes()); // 注意中文提交(如果报错，要考虑中文的问题，去掉中文重试)
				out.flush();
				out.close();
			}
			// 读取响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String lines;
			StringBuilder sb = new StringBuilder("");
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "UTF-8");
				sb.append(lines);
			}
			result = sb.toString();
			reader.close();
			// 断开连接
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getToCallWCF() {
		String result = "";
		BufferedReader in = null;
		try {
			URL realUrl = new URL(
					"http://192.168.200.161:11667/CoreService/REST/RestService.svc/CaseAutoSend?caseId=123&actId=11111");
			URLConnection connection = realUrl.openConnection();
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("Content-type", "application/json");
			connection.connect();
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	private InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}

	private String getStrFromStream(String path) {
		StringBuilder sb = new StringBuilder();
		String temp;
		try {
			InputStream is = getImageStream(path);
			assert is != null;
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
		} catch (Exception ignored) {
		}
		return sb.toString();
	}

}
