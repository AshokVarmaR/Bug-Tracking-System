package com.bts.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "files")
public class FileManager {

	@Getter
	@Setter
	private String path;

	private Path rootLocation;

	@PostConstruct
	void init() {
		this.rootLocation = Paths.get(path);
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize file storage", e);
		}
	}

	/**
	 * Save a new file
	 */
	public String save(MultipartFile file, String fileName) {
		try {
			Path target = rootLocation.resolve(fileName).normalize();
			Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
			String fileUrl = "/"+fileName;
			return fileUrl;
		} catch (IOException e) {
			throw new RuntimeException("Failed to save file: " + fileName, e);
		}
	}

	/**
	 * Replace an existing file
	 */
	public String replace(MultipartFile file, String existingFileName) {
		delete(existingFileName);
		return save(file, file.getOriginalFilename());
	}

	/**
	 * Delete a file
	 */
	public void delete(String fileName) {
	    try {
	        Path target = rootLocation.resolve(fileName).normalize();
	        Files.deleteIfExists(target);
	    } catch (IOException e) {
	        // log and ignore
	        System.err.println("Could not delete file: {"+ fileName+"}");
	    }
	}


	/**
	 * Read file as byte[]
	 */
	public byte[] read(String fileName) {
		try {
			Path target = rootLocation.resolve(fileName).normalize();
			return Files.readAllBytes(target);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read file: " + fileName, e);
		}
	}
}
