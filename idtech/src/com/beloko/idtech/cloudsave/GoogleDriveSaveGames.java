package com.beloko.idtech.cloudsave;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class GoogleDriveSaveGames {
	public enum Location {LOCAL,REMOTE};
	
	public Location location; 
	
	int appVersion;
	
	public ArrayList<GoogleDriveItem> items;

	
	public GoogleDriveSaveGames(Location loc)
	{
		location = loc;
		items = new ArrayList<GoogleDriveItem>();
	}
	
	
	public void addFolder(String basepath,String savegamepath, final String[]  filter)
	{

		File dir = new File(basepath + "/" + savegamepath);
		
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				
				for (String extension : filter)
				{
					if (file.getName().toLowerCase().endsWith(extension))
					{
						return true;
					}
				}
				return false;
			}
		});
		
		if (files == null)
		{
			Utils.Log(1,"GoogleDriveSaveGames", "No file found in folder: " + dir.getAbsolutePath());
			return;
		}
		
		Utils.Log(1,"GoogleDriveSaveGames", "Found " + files.length + " files in folder: " + dir.getAbsolutePath());
	
		for (File f: files)
		{
			
			String md5 = "";
			FileInputStream fis;
			try {
				fis = new FileInputStream(f);
				md5 = new String(Hex.encodeHex(DigestUtils.md5(fis)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Utils.Log(3,"GoogleDriveSaveGames", "Adding file: " + f.getName() + " MD5: " + md5);
			
			GoogleDriveItem item = new GoogleDriveItem(GoogleDriveItem.Type.FILE,savegamepath,f.getName(),md5,null);
			items.add(item);
		}
		
	}


}
