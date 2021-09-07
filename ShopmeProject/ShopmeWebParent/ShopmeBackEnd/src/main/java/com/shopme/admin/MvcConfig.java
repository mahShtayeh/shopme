package com.shopme.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//User's profile photos resource handler
		exposeDirectory("user-photos", registry); 
		
		//Categories' images resource handler
		exposeDirectory("../category-images", registry); 
		
		//Brands' logos resource handler
		exposeDirectory("../brands-images", registry); 
	}
	
	private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
		Path dirPath = Paths.get(dirName); 
		String dirAbsolutePath = dirPath.toFile().getAbsolutePath(); 
		
		String dirLogicalPath = dirName.replace("../", "") + "/**"; 
		
		registry.addResourceHandler(dirLogicalPath)
			.addResourceLocations("file:" + dirAbsolutePath + "/"); 
	}
	
}
