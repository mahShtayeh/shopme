package com.shopme.admin;

import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path; 

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
	public static void saveFile(String uploadDir, String fileName, 
			MultipartFile multipartFile) throws IOException {
		Path uploadPath = Paths.get(uploadDir); 
		
		if(!Files.exists(uploadPath)) 
			Files.createDirectories(uploadPath); 
		
		try(InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName); 
			
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING); 
		} catch(IOException ex) {
			throw new IOException("Couldn't save file: " + fileName, ex); 
		}
	}
	
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir); 
		
		try {
			Files.list(dirPath).forEach(file -> {
				if(!Files.isDirectory(file)) {
					try {
						Files.delete(file); 
					} catch (IOException ex) {
						System.out.println("Couldn't delete the file: " + file); 
					}
				}
			});
		} catch (IOException ex) { 
			System.out.println("Couldn't lest directory: " + dirPath); 
		}
	}
}
