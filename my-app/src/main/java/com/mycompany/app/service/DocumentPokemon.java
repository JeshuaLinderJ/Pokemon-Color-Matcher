package com.mycompany.app.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DocumentPokemon {
    public static void initializePokemon(){
        // Create a directory path
        String imageDirectoryPath = "src/main/resources/images";
        Path imageDirPath = Paths.get(imageDirectoryPath);

        // Check if directory exists
        if (!Files.exists(imageDirPath) || !Files.isDirectory(imageDirPath)) {
            System.err.println("The specified directory does not exist or is not a directory: " + imageDirectoryPath);
            System.err.println("Creating directory...");
            try {
                Files.createDirectories(imageDirPath);
                System.out.println("Directory created successfully.");
            } catch (IOException e) {
                System.err.println("Failed to create directory: " + e.getMessage());
                return;
            }
        }

        // List to store image information
        List<Map<String, Object>> imageInfoList = new ArrayList<>();

        try {
            // List all files in the directory
            Files.list(imageDirPath)
                .filter(Files::isRegularFile)
                .forEach(filePath -> {
                    File file = filePath.toFile();
                    String fileName = file.getName().toLowerCase();
                    
                    // Check if the file is an image
                    if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                        fileName.endsWith(".png") || fileName.endsWith(".gif") || 
                        fileName.endsWith(".bmp")) {
                        
                        try {
                            // Read image file
                            BufferedImage img = ImageIO.read(file);
                            
                            // Create a map for this image's information
                            Map<String, Object> imageInfo = new HashMap<>();
                            imageInfo.put("fileName", file.getName());
                            imageInfo.put("filePath", file.getAbsolutePath());
                            imageInfo.put("fileSize", file.length());
                            
                            // Add width and height if image was read successfully
                            if (img != null) {
                                imageInfo.put("width", img.getWidth());
                                imageInfo.put("height", img.getHeight());
                                
                                // Get RGB average using the ImagePixelParser
                                String rgbData = ImagePixelParser.rgbAverage(file.getName());
                                if (rgbData != null) {
                                    imageInfo.put("rgbAverage", rgbData);
                                }
                            }
                            
                            // Add to our list
                            imageInfoList.add(imageInfo);
                            System.out.println("Processed image: " + fileName);
                            
                        } catch (IOException e) {
                            System.err.println("Error processing image: " + file.getName() + " - " + e.getMessage());
                        }
                    }
                });
            
            // Create the final map
            Map<String, Object> finalMap = new HashMap<>();
            finalMap.put("images", imageInfoList);
            finalMap.put("totalImages", imageInfoList.size());
            finalMap.put("generatedAt", new java.util.Date().toString());
            
            // Convert to JSON and write to file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(finalMap);
            
            try (FileWriter fileWriter = new FileWriter("image_info.json")) {
                fileWriter.write(json);
                System.out.println("Successfully wrote information for " + imageInfoList.size() + " images to JSON file");
            }
            
        } catch (IOException e) {
            System.err.println("Error during file processing or JSON writing: " + e.getMessage());
        }
    }
}
