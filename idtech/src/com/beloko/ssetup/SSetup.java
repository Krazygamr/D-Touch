package com.beloko.ssetup;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.beloko.idtech.AppSettings;
import com.beloko.license.NdkLicense;
import com.beloko.license.NdkLicenseCallback;
import com.beloko.license.NdkLicenseCallback.LicStatus;

public class SSetup {

	static Context ctx;
	static Activity act=null;

	public static native void setup( Activity activity,int ver);

	public static native String getUpdate(Activity activity,int ver);

	
	public static void loadSO(Context ctx_,Activity activity)
	{
		ctx = ctx_;
		act = activity;
		try {
			Log.i("JNI", "Trying to load libraries");
			System.loadLibrary("s-setup_so");
		}
		catch (UnsatisfiedLinkError ule) {
			Log.e("JNI", "WARNING: Could not load shared library: " + ule.toString());
		}
	}
	
	public static void load(Context ctx_,Activity activity)
	{
		ctx = ctx_;
		act = activity;
	}
	
	
	
	public static String getFilesDir()
	{
		return ctx.getFilesDir().toString();
	}
	
	public static String getLibsDir()
	{
		return ctx.getApplicationInfo().nativeLibraryDir;
	}
	
	public static int getVer()
	{
		//Log.d("SSetup","get ver setup");
		int ver=0;
		PackageInfo pInfo;
		try {
			pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			ver = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ver;
	}
	
	public static Activity getActivity()
	{
		if (act == null)
			Log.e("LOG","Activity Error");
		
		return act;
	}
	
	
	static ProgressDialog checkLicPd = null;
	static Object checkLicPdLock = new Object();
	static int ver;
	public static void getUpdateSec(final Activity activity,String key,int ver)
	{
		act = activity;
		SSetup.ver = ver;
		synchronized (checkLicPdLock) {
			if (checkLicPd != null)
				checkLicPd.dismiss();

			checkLicPd = new ProgressDialog(act);
			checkLicPd.setTitle("Checking connection...");
			checkLicPd.setMessage("Please wait.");
			checkLicPd.setCancelable(true);
			checkLicPd.show();
		}


		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				synchronized (checkLicPdLock) {
					if (checkLicPd != null)
					{
						checkLicPd.dismiss();
						checkLicPd = null;
						act.runOnUiThread(new Runnable(){
							public void run(){
								Toast.makeText(act,"Error contacting connecting to service", Toast.LENGTH_LONG).show();
							}
						});	

					}
					t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
				}
			}
		}, 10000); // 10 second timeout

		NdkLicense.check(act, AppSettings.rnlvl,key, new LCallback());
	}


	static private class LCallback implements NdkLicenseCallback {

		@Override
		public void status(final LicStatus ret) {
			
			synchronized (checkLicPdLock) {
				if (checkLicPd != null)
				{
					checkLicPd.dismiss();
					checkLicPd = null;
				}
			}
			
			if (ret.code == NdkLicenseCallback.GOOD)
			{
				act.runOnUiThread(new Runnable(){
					public void run(){
						//Toast.makeText(act,"FIX ME NOW!", Toast.LENGTH_LONG).show();
						new GenUpdateThread().execute(null,null);
					}
				});	
				
			}
			else
			{
				act.runOnUiThread(new Runnable(){
					public void run(){
						Toast.makeText(act,"Error contacting server: " + ret.code + " Info:" + ret.desc, Toast.LENGTH_LONG).show();
					}
				});	
			}
		}
	}

	static private class GenUpdateThread extends AsyncTask<String, Integer, Long> {

		private ProgressDialog progressBar;
		String errorstring= null;

		@Override
		protected void onPreExecute() {
			progressBar = new ProgressDialog(act); 
			progressBar.setMessage("Downloading files..");
			progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressBar.setCancelable(false);
			progressBar.show();
		}

		protected Long doInBackground(String... info) {

			SSetup.loadSO(act,act);
			String result = SSetup.getUpdate(act, ver);
			if (!result.contentEquals(""))
				errorstring = result;

			return 0l;
		}

		protected void onProgressUpdate(Integer... progress) {

		}                                

		protected void onPostExecute(Long result) {
			progressBar.dismiss();
			if (errorstring!=null)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(act);
				builder.setMessage("Error accessing server: " + errorstring)
				.setCancelable(true)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});

				builder.show();
			}
		}
	}
}
