package com.beloko.idtech.cloudsave;

public class GoogleDriveItem {
	public enum Type{FILE,FOLDER};
	Type type;
	String path; //Path on local device
	String name;
	String id;
	String md5;
	
	public GoogleDriveItem(Type type,String path,String name,String md5,String id)
	{
		this.type = type;
		this.path = path;
		this.name = name;
		this.id = id;
		this.md5 = md5;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	

	public String getLocalPath() {
		return path;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getDriveName() {
		return path.replace("/", ":") + ":" + name; //Just use one level of folder in Drive for simplicity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
