package com.elephant.common.config.file;

public class ConfigurationFile {

	private PathType type;
	
	private String path;

	public ConfigurationFile(PathType type, String path) {
		super();
		this.type = type;
		this.path = path;
	}

	public PathType getType() {
		return type;
	}

	public String getPath() {
		return path;
	}
	
}
