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
		String dirName = "user-photos"; 
		Path userPhotosDir = Paths.get(dirName); 
		
		String userPhotosPath = userPhotosDir.toFile().getAbsolutePath(); 
		
		registry.addResourceHandler("/" + dirName + "/**")
			.addResourceLocations("file:" + userPhotosPath + "/"); 
		
		//Categories' profile photos resource handler
		String categoryImagesDirName = "../category-images"; 
		Path categoryImagesDir = Paths.get(categoryImagesDirName); 
		
		String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath(); 
		
		registry.addResourceHandler("/category-images/**")
			.addResourceLocations("file:" + categoryImagesPath + "/"); 
		
		//Brands' profile photos resource handler
		String brandLogoDirName = "../brands-images"; 
		Path brandLogoDir = Paths.get(brandLogoDirName); 
		
		String brandLogoPath = brandLogoDir.toFile().getAbsolutePath(); 
		
		registry.addResourceHandler("/brands-images/**")
			.addResourceLocations("file:" + brandLogoPath + "/");
	}
	
}
