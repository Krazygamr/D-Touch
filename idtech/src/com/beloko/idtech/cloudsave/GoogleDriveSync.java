package com.beloko.idtech.cloudsave;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.beloko.idtech.cloudsave.GoogleDriveSaveGames.Location;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.DriveIdResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

public class GoogleDriveSync {

	static Context context;
	static GoogleApiClient googleApiClient; 
	static String driveFolderId;

	static ArrayList<GoogleDriveItem> filesAdd = new ArrayList<GoogleDriveItem>();
	static ArrayList<GoogleDriveItem> filesUpdate = new ArrayList<GoogleDriveItem>();
	static ArrayList<GoogleDriveItem> filesDelete = new ArrayList<GoogleDriveItem>();


	static Location current_loc;

	static GoogleDriveItem current_item;

	//enum Task {

	public static void setApiClient(GoogleApiClient api)
	{
		googleApiClient = api;
	}
	
	public static void setContext(Context c)
	{
		context = c;
	}

	public static void calculateDifference(GoogleDriveSaveGames saves_from, GoogleDriveSaveGames saves_to)
	{
		filesAdd.clear();
		filesUpdate.clear();
		filesDelete.clear();

		for (GoogleDriveItem item_from : saves_from.items) //For each item, check
		{

			GoogleDriveItem item_to = null;
			for (GoogleDriveItem item : saves_to.items) //Check if item exists
			{
				if (item_from.getDriveName().contentEquals(item.getDriveName()))
					item_to = item;
			}

			if (item_to == null) //Item does not even exist, so ADD file
			{
				Utils.Log(2, "GoogleDriveSync", "Adding file: " + item_from.getDriveName());
				filesAdd.add(item_from);

			}
			else //Item already exists, so check if file is different
			{

			}

		}

	}


	public static int sync(GoogleDriveSaveGames saves_from, GoogleDriveSaveGames saves_to)
	{
		if ((saves_from.location == Location.LOCAL) && (saves_to.location == Location.REMOTE))
		{
			Utils.Log(1, "GoogleDriveSync", "Uploading saves to Google Drive");
			current_loc = Location.REMOTE;
		}
		else if ((saves_from.location == Location.REMOTE) && (saves_to.location == Location.LOCAL))
		{
			current_loc = Location.LOCAL;
			Utils.Log(1, "GoogleDriveSync", "Downloading saves from Google Drive");
		}
		else
		{
			Utils.Log(1, "GoogleDriveSync", "ERROR: Bad location types");
			return -1;
		}

		calculateDifference(saves_from,saves_to);


		processList();

		return 0;
	}



	//Creating new Drive file ------------------------------------
	final static private ResultCallback<ContentsResult> contentsCallback = new
			ResultCallback<ContentsResult>() {
		@Override
		public void onResult(ContentsResult result) {

			Utils.Log(3,"GoogleDriveSync","ContentsResult callback");

			if (!result.getStatus().isSuccess()) {
				Utils.Log(1,"GoogleDriveSync","ERROR while trying to create new file contents");
				return;
			}

			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
			.setTitle(current_item.getDriveName())
			.setMimeType("application/octet-stream")
			.build();

			if (googleApiClient == null)
				Utils.Log(1,"GoogleDriveSync","ERROR googleApiClient is NULL");
			
			Drive.DriveApi.getRootFolder(googleApiClient)
			.createFile(googleApiClient, changeSet, result.getContents())
			.setResultCallback(fileCallback);
		}
	};

	final static private ResultCallback<DriveFileResult> fileCallback = new
			ResultCallback<DriveFileResult>() {
		@Override
		public void onResult(DriveFileResult result) {
			if (!result.getStatus().isSuccess()) {
				Utils.Log(1,"GoogleDriveSync","ERROR while trying to create the file");
				return;
			}
			Utils.Log(1,"GoogleDriveSync","Created a file: " + result.getDriveFile().getDriveId());

			current_item.setId(result.getDriveFile().getDriveId().encodeToString()); //Set the Drive of of the new item
			processList(); //Carry on with the list of tasks!
		}
	};
	

	private static void driveAddFile(GoogleDriveItem item)
	{
		Utils.Log(1, "GoogleDriveSync", "Adding file to Google Drive " + item.getName());
		current_item = item;
		Drive.DriveApi.newContents(googleApiClient).setResultCallback(contentsCallback);
	}

	//----------------------------------------------------
	// Updating drive content ---------------------------
	//----------------------------------------------------
	
	public static class EditContentsAsyncTask extends AsyncTask<DriveFile, Void, Boolean> {

		public EditContentsAsyncTask(Context context) {

		}

		@Override
		protected Boolean doInBackground(DriveFile... args) {
			DriveFile file = args[0];
			try {
				ContentsResult contentsResult = file.openContents(
						googleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
				if (!contentsResult.getStatus().isSuccess()) {
					Utils.Log(1,"GoogleDriveSync","ERROR Could not open Drive file for writing");
					return false;
				}
				OutputStream outputStream = contentsResult.getContents().getOutputStream();
				outputStream.write("Hello world".getBytes());
				com.google.android.gms.common.api.Status status = file.commitAndCloseContents(
						googleApiClient, contentsResult.getContents()).await();
				return status.getStatus().isSuccess();
			} catch (IOException e) {
				Utils.Log(1,"GoogleDriveSync","ERROR IOException while appending to the output stream");
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Utils.Log(3,"GoogleDriveSync","ERROR while editing contents");
				return;
			}
			Utils.Log(2,"GoogleDriveSync","Successfully edited contents");

			processList(); //Carry on with the list of tasks!
		}

	}

	/*
	final static private ResultCallback<DriveIdResult> idCallback = new ResultCallback<DriveIdResult>() {
		@Override
		public void onResult(DriveIdResult result) {
			if (!result.getStatus().isSuccess()) {
				Utils.Log(1,"GoogleDriveSync","ERROR while trying to get IdResult");
				return;
			}

			DriveFile file = Drive.DriveApi.getFile(googleApiClient, result.getDriveId());
			new EditContentsAsyncTask(context).execute(file);
		}
	};
*/

	private static void driveUpdateFile(GoogleDriveItem item)
	{
		current_item = item;
		Utils.Log(3,"GoogleDriveSync","driveUpdateFile ID = " + item.getId());
		//DriveId.decodeFromString
		//Drive.DriveApi.fetchDriveId(googleApiClient, DriveId.decodeFromString(item.getId())).setResultCallback(idCallback);
		DriveId driveid = DriveId.decodeFromString(item.getId());
		DriveFile file = Drive.DriveApi.getFile(googleApiClient, driveid);
		new EditContentsAsyncTask(context).execute(file);
	}

	public static int processList()
	{
		if (current_loc == Location.REMOTE) //Uploading to Google Drive
		{
			if (filesAdd.size() > 0)
			{
				GoogleDriveItem item = filesAdd.get(0);
				filesAdd.remove(0);
				filesUpdate.add(item); //Add drive file just create a new file but does not fill the content, for that we add it to the update list

				driveAddFile(item);
				return 1;
			}

			if (filesUpdate.size() > 0)
			{
				GoogleDriveItem item = filesUpdate.get(0);
				filesUpdate.remove(0);
				current_item = item;
				driveUpdateFile(item);
			}

		}
		else
		{

		}

		return 0;
	}

}
