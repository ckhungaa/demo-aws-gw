package com.elephant.common.config.file;

import java.util.List;

import com.google.common.collect.Lists;

public class ConfigurationFiles {

	public static final List<ConfigurationFile> PROPERTIES_FILES = Lists.newArrayList(
			new ConfigurationFile(PathType.APP_CLASS, "/config.properties"),
			new ConfigurationFile(PathType.SYSTEM_FILE, "/etc/config.properties"));

}
