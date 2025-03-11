package com.mycompany.app.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.mycompany.app.service.DocumentPokemon;
import com.mycompany.app.service.ImagePixelParser;

public class PokemonImageViewer extends JFrame {
    private JLabel statusLabel;
    private JLabel colorLabel;
    private JPanel imagePanel;
    private BufferedImage currentImage;
    private JComboBox<String> imageSelector;

    public PokemonImageViewer() {
        setTitle("Pokemon Image Viewer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create panels
        JPanel topPanel = new JPanel();
        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentImage != null) {
                    // Calculate centered position
                    int x = (getWidth() - currentImage.getWidth()) / 2;
                    int y = (getHeight() - currentImage.getHeight()) / 2;
                    g.drawImage(currentImage, x, y, this);
                }
            }
        };
        
        imagePanel.setBackground(Color.DARK_GRAY);
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Create components
        JButton processButton = new JButton("Process Pokemon Images");
        statusLabel = new JLabel("Ready");
        colorLabel = new JLabel("Pixel Color: N/A");
        imageSelector = new JComboBox<>();
        
        // Add mouse motion listener to detect pixel color
        imagePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentImage != null) {
                    // Calculate image position
                    int imgX = (imagePanel.getWidth() - currentImage.getWidth()) / 2;
                    int imgY = (imagePanel.getHeight() - currentImage.getHeight()) / 2;
                    
                    // Check if mouse is over the image
                    if (e.getX() >= imgX && e.getX() < imgX + currentImage.getWidth() &&
                        e.getY() >= imgY && e.getY() < imgY + currentImage.getHeight()) {
                        
                        // Get pixel color
                        int pixelX = e.getX() - imgX;
                        int pixelY = e.getY() - imgY;
                        int rgb = currentImage.getRGB(pixelX, pixelY);
                        
                        // Extract components
                        int alpha = (rgb >> 24) & 0xff;
                        int red = (rgb >> 16) & 0xff;
                        int green = (rgb >> 8) & 0xff;
                        int blue = rgb & 0xff;
                        
                        // Display color information
                        colorLabel.setText(String.format("Pixel Color: R:%d G:%d B:%d A:%d", red, green, blue, alpha));
                    } else {
                        colorLabel.setText("Pixel Color: N/A (not on image)");
                    }
                }
            }
        });
        
        // Add action for the process button
        processButton.addActionListener(e -> {
            new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() {
                    publish("Processing Pokemon images...");
                    try {
                        // Check if flag file exists
                        File flagFile = new File("documentPokemon.flag");
                        if (!flagFile.exists()) {
                            publish("Running documentPokemon() for the first time...");
                            DocumentPokemon.initializePokemon();
                            flagFile.createNewFile();
                            publish("documentPokemon() completed.");
                        } else {
                            publish("documentPokemon() has already been run before.");
                        }
                        
                        // Load available images
                        loadAvailableImages();
                        
                    } catch (IOException ex) {
                        publish("Error: " + ex.getMessage());
                    }
                    return null;
                }
                
                @Override
                protected void process(List<String> chunks) {
                    // Update status label with the latest message
                    if (!chunks.isEmpty()) {
                        statusLabel.setText(chunks.get(chunks.size() - 1));
                    }
                }
                
                @Override
                protected void done() {
                    statusLabel.setText("Ready - Images loaded");
                }
            }.execute();
        });
        
        // Image selector action
        imageSelector.addActionListener(e -> {
            String selectedImage = (String) imageSelector.getSelectedItem();
            if (selectedImage != null) {
                loadImage(selectedImage);
            }
        });
        
        // Layout components
        topPanel.add(processButton);
        topPanel.add(new JLabel("Select Image:"));
        topPanel.add(imageSelector);
        
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(colorLabel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(imagePanel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void loadAvailableImages() {
        imageSelector.removeAllItems();
        
        try {
            Path imageDirPath = Paths.get("src/main/resources/images");
            if (Files.exists(imageDirPath)) {
                List<String> imageFiles = Files.list(imageDirPath)
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.toLowerCase().endsWith(".png") || 
                                   name.toLowerCase().endsWith(".jpg") || 
                                   name.toLowerCase().endsWith(".gif"))
                    .collect(Collectors.toList());
                
                // Add images to selector
                for (String file : imageFiles) {
                    imageSelector.addItem(file);
                }
                
                // Load first image if available
                if (!imageFiles.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        imageSelector.setSelectedIndex(0);
                    });
                }
            }
        } catch (IOException e) {
            statusLabel.setText("Error loading image list: " + e.getMessage());
        }
    }
    
    private void loadImage(String imageName) {
        try {
            String imagePath = "src/main/resources/images/" + imageName;
            currentImage = ImageIO.read(new File(imagePath));
            
            // Calculate RGB average
            String rgbAvg = ImagePixelParser.rgbAverage(imageName);
            if (rgbAvg != null) {
                statusLabel.setText("Loaded: " + imageName + " - RGB Average: " + rgbAvg);
            } else {
                statusLabel.setText("Loaded: " + imageName);
            }
            
            // Repaint to show the image
            imagePanel.repaint();
            
        } catch (IOException e) {
            statusLabel.setText("Error loading image: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PokemonImageViewer viewer = new PokemonImageViewer();
            viewer.setVisible(true);
        });
    }
}