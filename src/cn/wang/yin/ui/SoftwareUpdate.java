package cn.wang.yin.ui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import cn.wang.yin.bean.ApkBean;
import cn.wang.yin.utils.CollectDebugLogUtil;
import cn.wang.yin.utils.PersonConstant;
import cn.wang.yin.utils.PersonStringUtils;

public class SoftwareUpdate {
	private static String TAG = "VersionUpdate";
	int newVerCode = 0;
	String newVerName = "";
	public static ProgressDialog pBar;
	public static final int NEED_UPDATE = 100;
	public static final int UNEED_UPDATE = 101;
	public static final int NETWORK_ERROR = 102;
	public static final int DOWNLOAD_SUCCESS = 103;
	public static final int DOWNLOAD_FAIL = 104;
	public static final int NEED_SDCARD = 105;
	// static int update = 104;
	File myTempFile = null;
	Context context;
	ProgressDialog p_dialog;
	public static ApkBean bean = new ApkBean();

	public SoftwareUpdate(Context cxt) {
		try {
			if (cxt != null) {
				context = cxt;
				p_dialog = new ProgressDialog(cxt);
				p_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				p_dialog.setMessage("正在新检查版本...");
				pBar = new ProgressDialog(context);
				pBar.setTitle("正在下载");
				pBar.setIndeterminate(false);
				pBar.setCancelable(false);
				pBar.setProgress(0);
				System.out.println();
				pBar.setMax(100);
				pBar.setMessage("请稍候...");
				pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {
		try {
			if (p_dialog != null) {
				p_dialog.show();
				new Thread(checkRunnnable).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NEED_UPDATE: {
				p_dialog.dismiss();
				String verName = PersonStringUtils.getVerName(context);
				StringBuffer sb = new StringBuffer();
				sb.append("当前版本:");
				sb.append(verName);
				sb.append(", 发现新版本:");
				sb.append(newVerName);
				sb.append(", 是否更新?");
				Dialog dialog = new AlertDialog.Builder(context)
						.setTitle("软件更新")
						.setMessage(sb.toString())
						// 设置内容
						.setPositiveButton("更新",// 设置确定按钮
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										try {
											pBar.show();
											// 下载
											new Thread(downloadRunnnable)
													.start();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								})
						.setNegativeButton("暂不更新",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int whichButton) {

									}
								}).create();
				// 显示对话框
				dialog.show();
			}
				break;
			case UNEED_UPDATE: {
				if (p_dialog != null)
					p_dialog.dismiss();
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle("提示")
						.setMessage("已经是最新版本，无需更新")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
				break;
			case NETWORK_ERROR: {
				if (p_dialog != null)
					p_dialog.dismiss();
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle("提示")
						.setMessage("检查错误，请重试!")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
				break;
			case DOWNLOAD_SUCCESS: {
				if (pBar != null)
					pBar.dismiss();
				if (myTempFile != null)
					openFile(myTempFile);
			}
				break;
			case DOWNLOAD_FAIL: {
				if (pBar != null)
					pBar.dismiss();
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle("提示")
						.setMessage("下载失败，请重试!")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
				break;

			case NEED_SDCARD: {
				if (pBar != null)
					pBar.dismiss();
				AlertDialog dialog = new AlertDialog.Builder(context)
						.setTitle("提示")
						.setMessage("没有SD卡！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();
			}
				break;

			}

			super.handleMessage(msg);
		}

	};

	/**
	 * 检查线程
	 */
	protected Runnable checkRunnnable = new Runnable() {

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = NEED_UPDATE;
			StringBuilder sb = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			// 设置网络超时参数
			HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
			HttpConnectionParams.setSoTimeout(httpParams, 30000);
			HttpResponse response = null;
			try {
				response = client.execute(new HttpGet(
						PersonConstant.UPDATE_SERVER
								+ PersonConstant.UPDATE_VERJSON));
			} catch (ClientProtocolException e1) {
				msg.what = NETWORK_ERROR;
				e1.printStackTrace();
			} catch (IOException e1) {
				msg.what = NETWORK_ERROR;
				e1.printStackTrace();
			}
			HttpEntity entity = null;
			if (response != null) {
				entity = response.getEntity();
			}

			if (entity != null) {
				BufferedReader reader;
				try {
					reader = new BufferedReader(new InputStreamReader(
							entity.getContent(), "UTF-8"), 8192);
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
					reader.close();
				} catch (UnsupportedEncodingException e) {
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (IllegalStateException e) {
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				}

			}

			try {
				String verjson = sb.toString();
				System.out.println(sb);
				JSONObject jo = new JSONObject(verjson);
				System.out.println(jo.get("bean"));

				if (jo.get("bean") != null) {
					JSONObject jb = (JSONObject) jo.get("bean");

					bean.setApkName(jb.getString("apkName"));
					bean.setAppName(jb.getString("apkName"));
					bean.setUpdateUrl(jb.getString("updateUrl"));
					bean.setVerCode(jb.getString("verCode"));
					bean.setVerName(jb.getString("verName"));
				}
				int vercode = PersonStringUtils.getVerCode(context);
				newVerCode = Integer.parseInt(bean.getVerCode());
				newVerName = bean.getVerName();
				if (newVerCode > vercode) {
					msg.what = NEED_UPDATE;
				} else if (newVerCode <= vercode) {
					msg.what = UNEED_UPDATE;
				}

			} catch (Exception e) {
				msg.what = NETWORK_ERROR;
				Log.e(TAG, e.getMessage());
			}
			handler.sendMessage(msg);
		}
	};

	/**
	 * 下载线程
	 */
	protected Runnable downloadRunnnable = new Runnable() {

		@Override
		public void run() {

			Message msg = new Message();
			msg.what = DOWNLOAD_SUCCESS;
			try {
				Looper.prepare();
				DownloadFile downloadFile = new DownloadFile();
				downloadFile.execute(bean.getUpdateUrl());
				Looper.loop();
			} catch (Exception e) {
				msg.what = DOWNLOAD_FAIL;
				e.printStackTrace();
				CollectDebugLogUtil.saveDebug(e.getMessage(), e.getClass()
						.toString(), this.getClass().toString() + "\t"
						+ "SoftwareUpdate--downloadFile");
			}
			handler.sendMessage(msg);
		}

	};

	/* 在手机上打开文件的method */
	private void openFile(File f) {
		try {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(f),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class DownloadFile extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... sUrl) {
			Message msg = new Message();
			msg.what = DOWNLOAD_SUCCESS;
			try {
				URL url = new URL(sUrl[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				int fileLength = connection.getContentLength();
				fileLength = fileLength > 10 ? fileLength : 1166000;
				InputStream input = new BufferedInputStream(url.openStream());
				myTempFile = new File(
						Environment.getExternalStorageDirectory(),
						PersonConstant.UPDATE_SAVENAME);
				OutputStream output = null;
				output = new FileOutputStream(myTempFile);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				try {
					while ((count = input.read(data)) != -1) {
						total += count;
						publishProgress((int) (total * 100 / fileLength));
						output.write(data, 0, count);
					}
				} catch (Exception e) {
					msg.what = DOWNLOAD_FAIL;
					e.printStackTrace();
				} finally {
					output.flush();
					output.close();
					input.close();
				}

			} catch (Exception e) {
				msg.what = DOWNLOAD_FAIL;
				e.printStackTrace();
			}
			handler.sendMessage(msg);
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				pBar.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			try {
				pBar.setProgress(progress[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
