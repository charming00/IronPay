package com.charming.ironpay.QRcode.common;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

/**
 * 兼容低版本的子线程开启任务
 * 
 * @author hugo
 * 
 */
public class Runnable {

	@SuppressLint("NewApi")
	@SuppressWarnings("unchecked")
	public static void execAsync(AsyncTask<?, ?, ?> task) {
//		if (Build.VERSION.SDK_INT >= 11) {
//			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
//		}
//		else {
//			task.execute();
//		}
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

	}

}
