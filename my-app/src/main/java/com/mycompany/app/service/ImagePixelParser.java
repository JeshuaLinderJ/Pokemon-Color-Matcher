package com.mycompany.app.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImagePixelParser {

    public static String rgbAverage(String filename) {
        try {
            // Step 1: Load the image from file
            filename = "src/main/resources/images/" + filename; // e.g. "metapod.png" -> "src/main/resources/images/metapod.png"
            File file = new File(filename); // e.g., "C:/images/picture.png" | cannot be jpg for lack of RGBA, only RGB
            BufferedImage image = ImageIO.read(file);

            int width = image.getWidth();
            int height = image.getHeight();

            // int totalPixels = width * height;
            int sumRed = 0;
            int sumGreen = 0;
            int sumBlue = 0;
            int nonTransparentPixels = 0;  // Counter for non-transparent pixels

            // System.out.println("Image dimensions: " + width + " x " + height);


            // Step 2: Loop through each pixel (row-major order)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    // Step 3: Get the pixel color (ARGB)
                    int pixel = image.getRGB(x, y);

                    // Step 4: Extract color components (Alpha, Red, Green, Blue)
                    int alpha = (pixel >> 24) & 0xff;
                    int red   = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue  = pixel & 0xff;

                    if(alpha == 255){ // Ensuring that the pixel is not transparent
                        sumRed += red; 
                        sumGreen += green; 
                        sumBlue += blue;
                        nonTransparentPixels++; // Count this pixel
                    }

                    // Step 5: Do something with the pixel data (print here)
                    // System.out.println("Pixel at (" + x + ", " + y + "): "
                    //                    + "Alpha: " + alpha
                    //                    + ", Red: " + red
                    //                    + ", Green: " + green
                    //                    + ", Blue: " + blue); // Initial code that allowed for live readings to ensure library worked
                }
            }

            // Only divide by non-transparent pixels count if there are any
            if (nonTransparentPixels > 0) {
                sumRed = sumRed / nonTransparentPixels;
                sumGreen = sumGreen / nonTransparentPixels;
                sumBlue =  sumBlue / nonTransparentPixels;
                return("R" + sumRed + "G" + sumGreen + "B" + sumBlue); // Formatting as R#G#B# that can easily be parsed or read by eye
            } 
            else {
                System.out.println("No non-transparent pixels found in the image");
                return null;
            }
            
            // System.out.println("Non-transparent pixels: " + nonTransparentPixels + " out of " + totalPixels);

        } catch (IOException e) {
            System.out.println("Error reading the image file: " + e.getMessage());
        }
        return null; // Adhering to compiling requirements, should never reach here due to catch
    }

    // public static void main(String[] args){
    //     System.out.println(rgbAverage("metapod.png"));
    // }
}