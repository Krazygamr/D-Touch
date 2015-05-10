package com.beloko.idtech.cloudsave;

import java.util.ArrayList;

public class GoogleDriveFolder {
	String path;
	
	GoogleDriveItem drive_folder; //The folder on Google Drive
	
	ArrayList<GoogleDriveItem> drive_files; //The files in the folder above
	
	public GoogleDriveFolder(String path)
	{
		this.path = path;
		drive_folder = new GoogleDriveItem(GoogleDriveItem.Type.FOLDER,path,null,null,null); //Create the folder, no md5, don't know the driveID yet
		
		drive_files = new ArrayList<GoogleDriveItem>();
	}

	public void addFile(String path,String name,String md5)
	{
		drive_files.add(new GoogleDriveItem(GoogleDriveItem.Type.FILE,path,name,md5,null));
	}

}
