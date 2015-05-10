package com.beloko.license;


import android.os.Parcel;
import android.util.Log;

public class NdkLicenseListener extends android.os.Binder {
	static final String LISTENER = "com.android.vending.licensing.ILicenseResultListener";

	String LOG = "NdkLicenseListener";

	NdkLicenseCallback cb;
	String key;
	public NdkLicenseListener(NdkLicenseCallback callback,String key)
	{
		cb = callback;
		this.key = key;
	}

	public boolean onTransact(int op, Parcel in, Parcel reply, int flags) {

		Log.d("LIC","onTransact");

		if (op == 1) {

			NdkLicenseCallback.LicStatus ret = new NdkLicenseCallback.LicStatus();

			in.enforceInterface(LISTENER);
			int code         = in.readInt();
			String data      = in.readString();
			String signature = in.readString();
			Log.d(LOG,"code = " + code + " data = " + data);
			Log.d(LOG,"sig = " + signature);
			Log.d(LOG,"key = " + key);
			if (code == 0 || code == 2) {
				
				try {
					byte[] sig_test;
					sig_test = Base64.decode(signature);
					int verif = NdkLvlInterface.doCheck(key.getBytes(), data.getBytes(), sig_test);
					if (verif == 0)
					{
						//Log.e(LOG, "Signature verification failed.");
						ret.code =  NdkLicenseCallback.NO_GOOD;
						ret.desc = "Signature verification failed.";
						cb.status(ret);
						return true;
					}
					
				} catch (Base64DecoderException e) {
					ret.code =  NdkLicenseCallback.ERROR;
					ret.desc = e.toString();
					cb.status(ret);
					return true;
					
				}
				
				
				if ((data.startsWith("0|")))// || (data.startsWith("2|")))
				{
					ret.code =  NdkLicenseCallback.GOOD;
					ret.desc = "GOOD";
					cb.status(ret);
					return true;
				}
				else
				{
					ret.code =  NdkLicenseCallback.NO_GOOD;
					ret.desc = "Bad DATA code: " + data;
					cb.status(ret);
					return true;
				}


			} else if (code == 1) {
				ret.code =  NdkLicenseCallback.NO_GOOD;
				ret.desc = "Bad code: " + code;
				cb.status(ret);
				return true;
			} else {
				ret.code =  NdkLicenseCallback.ERROR;
				ret.desc = "Bad code: " + code;
				cb.status(ret);
				return true;
			}
		}


		return true;
	}
}