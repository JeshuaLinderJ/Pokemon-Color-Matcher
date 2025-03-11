import sys
import json
import os
import subprocess
import math
from PyQt5.QtWidgets import (QApplication, QMainWindow, QLabel, QPushButton, 
                             QVBoxLayout, QHBoxLayout, QWidget, QFrame, QScrollArea,
                             QMessageBox)
from PyQt5.QtGui import QPixmap, QImage, QColor, QKeyEvent
from PyQt5.QtCore import Qt, QTimer, QPoint
import mss

# Function to get correct resource path whether running as script or bundled app
def resource_path(relative_path):
    """ Get absolute path to resource, works for dev and for PyInstaller """
    try:
        # PyInstaller creates a temp folder and stores path in _MEIPASS
        base_path = sys._MEIPASS
    except Exception:
        base_path = os.path.abspath(".")
    return os.path.join(base_path, relative_path)

def check_java():
    try:
        subprocess.run(["java", "-version"], capture_output=True, check=True)
        return True
    except:
        QMessageBox.critical(None, "Error", "Java not found. Please install Java to use this application.")
        return False

class PokemonColorMatcher(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Pokemon Color Matcher")
        self.setGeometry(100, 100, 800, 600)
        
        # Pokemon data - Fix paths to match the project structure
        self.json_path = resource_path("image_info.json")
        self.image_dir = resource_path("images")
        
        # UI Setup
        self.init_ui()
        
        # Global cursor tracking timer
        self.cursor_timer = QTimer(self)
        self.cursor_timer.timeout.connect(self.track_cursor)
        self.cursor_timer.start(100)  # Check cursor every 100ms
        
        # Screenshot tool
        self.sct = mss.mss()
        
        # Last detected color
        self.last_color = None
        self.track_enabled = False
        
        # Install event filter to catch keyboard events
        self.installEventFilter(self)
        
    def eventFilter(self, obj, event):
        """Filter events to catch space bar press"""
        if event.type() == QKeyEvent.KeyPress and event.key() == Qt.Key_Space:
            # Space bar pressed, find matching Pokemon
            if self.track_enabled and self.last_color:
                self.find_matching_pokemon()
            return True
        return super().eventFilter(obj, event)
    
    def init_ui(self):
        # Main layout
        main_widget = QWidget()
        main_layout = QVBoxLayout(main_widget)
        
        # Top control panel
        control_panel = QHBoxLayout()
        
        # Initialize button
        self.init_button = QPushButton("Initialize Pokemon Database")
        self.init_button.clicked.connect(self.initialize_pokemon)
        control_panel.addWidget(self.init_button)
        
        # Toggle tracking button
        self.track_button = QPushButton("Start Color Tracking")
        self.track_button.clicked.connect(self.toggle_tracking)
        control_panel.addWidget(self.track_button)
        
        main_layout.addLayout(control_panel)
        
        # Color display
        color_frame = QFrame()
        color_frame.setFrameShape(QFrame.StyledPanel)
        color_frame.setMinimumHeight(50)
        color_frame.setMaximumHeight(50)
        self.color_layout = QHBoxLayout(color_frame)
        
        self.color_sample = QFrame()
        self.color_sample.setMinimumWidth(50)
        self.color_sample.setMaximumWidth(50)
        self.color_sample.setStyleSheet("background-color: #888888")
        
        self.color_info = QLabel("Cursor Color: N/A")
        
        self.color_layout.addWidget(self.color_sample)
        self.color_layout.addWidget(self.color_info)
        
        main_layout.addWidget(color_frame)
        
        # Instructions label
        instructions = QLabel("Press SPACE to find matching Pokemon for the current color under cursor")
        instructions.setAlignment(Qt.AlignCenter)
        main_layout.addWidget(instructions)
        
        # Results section
        self.results_label = QLabel("Closest Pokemon Match:")
        main_layout.addWidget(self.results_label)
        
        # Pokemon match display
        self.match_scroll = QScrollArea()
        self.match_scroll.setWidgetResizable(True)
        self.match_widget = QWidget()
        self.match_layout = QVBoxLayout(self.match_widget)
        self.match_scroll.setWidget(self.match_widget)
        
        main_layout.addWidget(self.match_scroll)
        
        # Status bar
        self.status_bar = self.statusBar()
        self.status_bar.showMessage("Ready")
        
        self.setCentralWidget(main_widget)
        
    def initialize_pokemon(self):
        """Run the Java code to initialize Pokemon data if needed"""
        self.status_bar.showMessage("Initializing Pokemon database...")
        
        if not check_java():
            return
        
        flag_exists = os.path.exists("documentPokemon.flag")
        
        if not flag_exists:
            # Run Java application to initialize
            try:
                result = subprocess.run(
                    ["java", "-cp", "target/my-app-1.0-SNAPSHOT.jar", "com.mycompany.app.App"],
                    capture_output=True,
                    text=True
                )
                self.status_bar.showMessage("Pokemon database initialized successfully")
            except Exception as e:
                self.status_bar.showMessage(f"Error initializing Pokemon database: {str(e)}")
        else:
            self.status_bar.showMessage("Pokemon database already initialized")
        
        # Load JSON data
        self.load_pokemon_data()
        
    def load_pokemon_data(self):
        """Load Pokemon data from JSON file"""
        try:
            with open(self.json_path, 'r') as f:
                data = json.load(f)
                self.pokemon_data = data.get('images', [])
                self.status_bar.showMessage(f"Loaded {len(self.pokemon_data)} Pokemon images")
        except Exception as e:
            self.status_bar.showMessage(f"Error loading Pokemon data: {str(e)}")
    
    def toggle_tracking(self):
        """Toggle cursor color tracking"""
        self.track_enabled = not self.track_enabled
        if self.track_enabled:
            self.track_button.setText("Stop Color Tracking")
            self.status_bar.showMessage("Color tracking active - press SPACE to find matching Pokemon")
            self.setFocus()  # Ensure window has focus to capture keypresses
        else:
            self.track_button.setText("Start Color Tracking")
            self.status_bar.showMessage("Color tracking stopped")
    
    def track_cursor(self):
        """Track cursor position and get color under cursor"""
        if not self.track_enabled:
            return
            
        # Get cursor position
        cursor_pos = QApplication.desktop().cursor().pos()
        
        # Capture a small region around the cursor
        monitor = {"top": cursor_pos.y(), "left": cursor_pos.x(), "width": 1, "height": 1}
        screenshot = self.sct.grab(monitor)
        
        # Get color at cursor position
        color = QColor(screenshot.pixel(0, 0)[0], screenshot.pixel(0, 0)[1], screenshot.pixel(0, 0)[2])
        
        # Update color display
        self.color_sample.setStyleSheet(f"background-color: rgb({color.red()}, {color.green()}, {color.blue()})")
        self.color_info.setText(f"Cursor Color: R:{color.red()} G:{color.green()} B:{color.blue()}")
        self.last_color = color
        
        # Removed right-click check since we're using space bar instead
    
    def find_matching_pokemon(self):
        """Find the Pokemon with the closest average RGB value"""
        if not self.pokemon_data or not self.last_color:
            self.status_bar.showMessage("No Pokemon data loaded or no color detected")
            return
            
        closest_match = None
        min_distance = float('inf')
        
        for pokemon in self.pokemon_data:
            rgb_data = pokemon.get('rgbAverage', '')
            if not rgb_data:
                continue
                
            # Parse RGB from format "R{r}G{g}B{b}"
            try:
                r = int(rgb_data.split('R')[1].split('G')[0])
                g = int(rgb_data.split('G')[1].split('B')[0])
                b = int(rgb_data.split('B')[1])
                
                # Calculate color distance (Euclidean)
                distance = math.sqrt(
                    (r - self.last_color.red()) ** 2 +
                    (g - self.last_color.green()) ** 2 +
                    (b - self.last_color.blue()) ** 2
                )
                
                if distance < min_distance:
                    min_distance = distance
                    closest_match = pokemon
            except:
                continue
        
        if closest_match:
            self.display_match(closest_match, min_distance)
        else:
            self.status_bar.showMessage("No matching Pokemon found")
    
    def display_match(self, pokemon, distance):
        """Display the matching Pokemon"""
        # Clear previous results
        while self.match_layout.count():
            child = self.match_layout.takeAt(0)
            if child.widget():
                child.widget().deleteLater()
        
        # Create match display
        match_frame = QFrame()
        match_frame.setFrameShape(QFrame.StyledPanel)
        match_layout = QHBoxLayout(match_frame)
        
        # Image
        image_path = os.path.join(self.image_dir, pokemon.get('fileName', ''))
        if os.path.exists(image_path):
            pixmap = QPixmap(image_path)
            if not pixmap.isNull():
                pixmap = pixmap.scaled(256, 256, Qt.KeepAspectRatio, Qt.SmoothTransformation)
                image_label = QLabel()
                image_label.setPixmap(pixmap)
                match_layout.addWidget(image_label)
        
        # Info
        info_layout = QVBoxLayout()
        info_layout.addWidget(QLabel(f"Name: {pokemon.get('fileName', 'Unknown')}"))
        
        rgb_data = pokemon.get('rgbAverage', '')
        if rgb_data:
            try:
                r = int(rgb_data.split('R')[1].split('G')[0])
                g = int(rgb_data.split('G')[1].split('B')[0])
                b = int(rgb_data.split('B')[1])
                
                info_layout.addWidget(QLabel(f"Average RGB: R:{r} G:{g} B:{b}"))
                
                color_preview = QFrame()
                color_preview.setMinimumSize(50, 50)
                color_preview.setMaximumSize(50, 50)
                color_preview.setStyleSheet(f"background-color: rgb({r}, {g}, {b})")
                info_layout.addWidget(color_preview)
            except:
                info_layout.addWidget(QLabel("RGB data parsing error"))
        
        info_layout.addWidget(QLabel(f"Color Distance: {distance:.2f}"))
        match_layout.addLayout(info_layout)
        
        self.match_layout.addWidget(match_frame)
        self.status_bar.showMessage(f"Found match: {pokemon.get('fileName', 'Unknown')}")

def main():
    app = QApplication(sys.argv)
    window = PokemonColorMatcher()
    window.show()
    sys.exit(app.exec_())

if __name__ == "__main__":
    main()