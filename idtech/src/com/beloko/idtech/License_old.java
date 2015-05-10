package com.beloko.idtech;


import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.beloko.serial.Serial;
import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class License_old {

	private static final String LOG = "License";

	private static final int LIC_NETWORK_RETRIES = 4; //How many times to automatically retry license check
	private static final int LIC_MAX_UNLIC_CNT = 2; //Number of times it can run unlicened before quits 


	// Generate your own 20 random bytes, and put them here.
	private static final byte[] SALT = new byte[] {
		-4, 12, 54, -12, -20, -57, -74, 99, 51, 19, 20, -45, 77, 17, -36, -33, 0, 6, 13,
		89
	};
	private LicenseCheckerCallback mLicenseCheckerCallback;
	private LicenseChecker mChecker;


	private int licNetworkRetries = LIC_NETWORK_RETRIES;

	final Context ctx;
	final int game;

	public License_old(Context ctx,int game,String public_key)
	{
		this.ctx = ctx;
		this.game = game;
		if (GD.DEBUG) Log.d(LOG,"CREATED");
		String deviceId = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);

		mLicenseCheckerCallback = new MyLicenseCheckerCallback();
		// Construct the LicenseChecker with a policy.
		mChecker = new LicenseChecker(
				ctx, new ServerManagedPolicy(ctx,
						new AESObfuscator(SALT, ctx.getPackageName(), deviceId)),
						public_key);
		
		//NdkLicense.check( ctx);

	}

	private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
		public void allow(int  reason ) {

			// Should allow user access.
			if (GD.DEBUG) Log.d(LOG,"LICENCE ALLOW  reason =" +  reason );
			AppSettings.setIntOption(ctx, "unlic_cnt", 0);

		}

		public void dontAllow(int policyReason) {

			// displayResult(getString(R.string.dont_allow));
			// Should not allow access. In most cases, the app should assume
			// the user has access unless it encounters this. If it does,
			// the app should inform the user of their unlicensed ways
			// and then either shut down the app or limit the user to a
			// restricted set of features.
			// In this example, we show a dialog that takes the user to Market.
			// If the reason for the lack of license is that the service is
			// unavailable or there is another problem, we display a
			// retry button on the dialog and a different message.
			if (GD.DEBUG) Log.d(LOG,"LICENCE NOT ALLOWED! reason = " + policyReason);
			//displayDialog(policyReason == Policy.RETRY);
			if (policyReason == Policy.RETRY)
			{
				if (GD.DEBUG) Log.d(LOG,"License Can not contact server, tries left = " + licNetworkRetries);
				if (licNetworkRetries > 0)
				{
					licNetworkRetries--;
					mChecker.checkAccess(mLicenseCheckerCallback);
				}
				else
				{
					//Actually don't do this, easy to hack but nevermind.
					//dontAllow(Policy.NOT_LICENSED);
				}
			}
			else // UNLICENSED
			{
				if (GD.DEBUG) Log.d(LOG,"UNLICENSED");

				//Increment number of times unlicensed
				int unliccnt = 	AppSettings.getIntOption(ctx, "unlic_cnt", 0);
				AppSettings.setIntOption(ctx, "unlic_cnt", unliccnt+1);
			}
		}

		public void applicationError(int errorCode) {

			Log.d(LOG,"LICENCE Error = " + errorCode);
		}
	}

	public void doCheck() {
		if (GD.DEBUG) Log.v(LOG, "doCheck()");
		licNetworkRetries = LIC_NETWORK_RETRIES;
		mChecker.checkAccess(mLicenseCheckerCallback);
	}


	public boolean isLicensed(String file)
	{
		int unliccnt = 	AppSettings.getIntOption(ctx, "unlic_cnt", 0);
		if (unliccnt < LIC_MAX_UNLIC_CNT)
			return true;
		else
		{
			Log.d(LOG,"game = " + game);
			int serial_result = Serial.checkSerial(ctx, file,  Serial.getKey(game), null, null);
			Log.d(LOG,"file = " + file);
			Log.d(LOG,"serial_result = " + serial_result);

			if (serial_result == game)
				return true;
			else
			{
				Toast.makeText(ctx,"Ser res = " + serial_result, Toast.LENGTH_LONG).show();
				return false;
			}
		}	
	}

	public void showBadLicense(Context ctx)
	{
		final License_old this_ = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage("Google License error. Please make sure you have internet access. Please Contact support@beloko.com for a solution to play on devices without Google Play or internet, Thank you.")
		.setCancelable(true)
		.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				this_.doCheck();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

}
