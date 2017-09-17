package com.elephant.common.config.file;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.common.collect.Maps;

@ApplicationScoped
public class PropertyResolver {

	private Map<String, String> properties = Maps.newHashMap();
	
	@PostConstruct
	private void  init(){
		ConfigurationFiles.PROPERTIES_FILES.forEach(file->{
        	Properties p = new Properties();
			try {
				if (file.getType() == PathType.APP_CLASS) {
					p.load(Files.newBufferedReader(Paths.get(this.getClass().getResource(file.getPath()).toURI())));
				}else {
					p.load(Files.newBufferedReader(Paths.get(file.getPath())));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			properties.putAll(Maps.fromProperties(p));
		});
	}
	
	public Optional<String> getValue(String key){
		return Optional.ofNullable(properties.get(key));
	}
	
	public static void main(String[] args){
		PropertyResolver p = new PropertyResolver();
		System.out.println(p.getValue("kit"));
	}
	
}
