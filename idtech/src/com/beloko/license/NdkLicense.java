package com.beloko.license;




import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

public class NdkLicense {
    static final String SERVICE = "com.android.vending.licensing.ILicensingService";
                                
    public static void check(final Context context,final int  nonce,final String key,final NdkLicenseCallback callback) {
        context.bindService(
            new Intent(SERVICE),
            new ServiceConnection() {
                public void onServiceConnected(ComponentName name, IBinder binder) {
                    Parcel d = Parcel.obtain();
                    try {
                        d.writeInterfaceToken(SERVICE);
                        d.writeLong(nonce);
                        d.writeString(context.getPackageName());
                        d.writeStrongBinder(new NdkLicenseListener(callback,key));
                        binder.transact(1, d, null, IBinder.FLAG_ONEWAY);
                    } catch (RemoteException e) {
                    }
                    d.recycle();
                }

                public void onServiceDisconnected(ComponentName name) {}
            },
            Context.BIND_AUTO_CREATE);
    }
}