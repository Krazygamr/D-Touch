package com.beloko.idtech.cloudsave;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.beloko.idtech.AppSettings;
import com.beloko.idtech.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;

public class CloudSaveActivity extends Activity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener {

	private static GoogleDriveSaveGames saveGamesLocal;
	
	private static final String TAG = "CloudSaveActivity";

	private static final String KEY_IN_RESOLUTION = "is_in_resolution";

	/**
	 * Request code for auto Google Play Services error resolution.
	 */
	protected static final int REQUEST_CODE_RESOLUTION = 1;

	/**
	 * Google API client.
	 */
	private GoogleApiClient mGoogleApiClient;

	/**
	 * Determines if the client is in a resolution state, and waiting for
	 * resolution intent to return.
	 */
	private boolean mIsInResolution;

	/**
	 * Called when the activity is starting. Restores the activity state.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_cloud_save);
		
		AppSettings.reloadSettings(getApplicationContext());
		
		Button button = (Button)findViewById(R.id.button1);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveGamesLocal = new GoogleDriveSaveGames(GoogleDriveSaveGames.Location.LOCAL);
				saveGamesLocal.addFolder(AppSettings.getGameDir(), "FULL/gzdoom_saves", new String[] {".zds"});
				
				GoogleDriveSaveGames saveGamesRemote = new GoogleDriveSaveGames(GoogleDriveSaveGames.Location.REMOTE);
				
			
				GoogleDriveSync.sync(saveGamesLocal, saveGamesRemote);
				
			}
		});
		
		if (savedInstanceState != null) {
			mIsInResolution = savedInstanceState.getBoolean(KEY_IN_RESOLUTION,
					false);
		}
		
		
	}

	/**
	 * Called when the Activity is made visible. A connection to Play Services
	 * need to be initiated as soon as the activity is visible. Registers
	 * {@code ConnectionCallbacks} and {@code OnConnectionFailedListener} on the
	 * activities itself.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(Drive.API).addScope(Drive.SCOPE_FILE).addScope(Drive.SCOPE_APPFOLDER)
					//.addApi(Games.API).addScope(Games.SCOPE_GAMES)
					// Optionally, add additional APIs and scopes if required.
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
		mGoogleApiClient.connect();
		Log.d(TAG,"onStart");
		GoogleDriveSync.setApiClient(mGoogleApiClient);
		GoogleDriveSync.setContext(this);
	}

	/**
	 * Called when activity gets invisible. Connection to Play Services needs to
	 * be disconnected as soon as an activity is invisible.
	 */
	@Override
	protected void onStop() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		super.onStop();
	}

	/**
	 * Saves the resolution state.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(KEY_IN_RESOLUTION, mIsInResolution);
	}

	/**
	 * Handles Google Play Services resolution callbacks.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_RESOLUTION:
			retryConnecting();
			break;
		}
	}

	private void retryConnecting() {
		mIsInResolution = false;
		if (!mGoogleApiClient.isConnecting()) {
			mGoogleApiClient.connect();
		}
	}

	/**
	 * Called when {@code mGoogleApiClient} is connected.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i(TAG, "GoogleApiClient connected");
		// TODO: Start making API requests.
		
	}

	/**
	 * Called when {@code mGoogleApiClient} connection is suspended.
	 */
	@Override
	public void onConnectionSuspended(int cause) {
		Log.i(TAG, "GoogleApiClient connection suspended");
		retryConnecting();
	}

	/**
	 * Called when {@code mGoogleApiClient} is trying to connect but failed.
	 * Handle {@code result.getResolution()} if there is a resolution available.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
		if (!result.hasResolution()) {
			// Show a localized error dialog.
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0, new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							retryConnecting();
						}
					}).show();
			return;
		}
		// If there is an existing resolution error being displayed or a
		// resolution
		// activity has started before, do nothing and wait for resolution
		// progress to be completed.
		if (mIsInResolution) {
			return;
		}
		mIsInResolution = true;
		try {
			result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e(TAG, "Exception while starting resolution activity", e);
			retryConnecting();
		}
	}
}
