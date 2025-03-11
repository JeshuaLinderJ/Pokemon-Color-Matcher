# Pokemon Color Matcher - README

## Project Overview

Pokemon Color Matcher is a cross-platform application that helps you find Pokemon based on colors. It captures the color under your cursor anywhere on screen, then matches it to the closest Pokemon by RGB value. The source of the pokemon images comes from the official Nintendo provided database of pokemon, however I kept that script out of this program in order to avoid any problems, as well as future-proof this application in the case of a website change. The majority of this project was AI assisted, with a number of LLMs used within the 2 days of development, however the overall base and logic comes from me, as well as my understanding of Java, Python, and amateaur Maven knowledge. I used LLM assistence for the python and java configuration, linking Maven to the Python GUI, as well as the majority of the Python GUI, leaving my usual option of manually creating a java GUI, as a preference to leave behind the outdated and quite frankly, difficulty, with Swing (my main library used for Java application development). I chose Java as it's currently my focus during my academic studies, and to gain more profficiency and comfort with the syntax, which can be a bit verbose. I chose Python for the GUI, rather than the script, as a way to challenge myself, as I've done a number of Python scripts in the past, and felt that my overall knowledge of scripting would improve with the use of a language that would make the action more 'out of the way'. I didn't quite layer the program too well, with the intention of keeping a lot of the steps separate and not within a single program, as is the reason why the website image scraper isn't present. However with the use of LLMs, the work of linking files and libraries together, and uniting them in the Maven and PythonInstaller compilers was a great time saver, but did hold me away from manually learning how to do it myself. At the moment, it isn't my focus to spend so much time uniting libraries and such, but in the future, as I do more of these projects, I'll settle in a setlist of libraries and compilers that will allow me to have a lot more say and choice in the micro, rather than macro. **At the moment, this does not work during compilation, as the image init doesn't link up to the python GUI, however by running the highlighted instructions below, directly through a few python command-line copy and pastes. The application consists of two main components:

1. **Java Backend**: Processes Pokemon images and calculates average RGB values
2. **Python GUI**: Provides an interactive interface to track cursor colors and display matches

## Prerequisites

- Java Development Kit (JDK) 17 or newer
- Maven 3.6+
- Python 3.7+
- pip (Python package manager)

## Installation

### 1. Clone or download the project

```bash
git clone <repository-url>
cd {project repository}
```

### 2. Install Java dependencies

```bash
cd my-app
mvn clean install
```

### 3. Install Python dependencies

```bash
pip install PyQt5 mss pyinstaller
```

## Running the Application

### Option 1: Run from source

```bash
cd my-app
python src/main/python/pokemon_color_matcher.py
```

### Option 2: Run the compiled executable

If you've built the executable (see below), you can run it directly:

```bash
# On macOS/Linux
cd my-app/dist
./PokemonColorMatcher

# On Windows
cd my-app\dist
PokemonColorMatcher.exe
```

## Using the Application

1. Click the **Initialize Pokemon Database** button when first running the application
   - This will process all Pokemon images and create the image_info.json file
   - This only needs to be done once unless you add new Pokemon images

2. Click **Start Color Tracking** to begin tracking the cursor color
   - The color sample will update in real-time as you move your cursor
   - The application tracks cursor position across your entire screen

3. Press the **SPACE** key while hovering over any color to find the closest matching Pokemon
   - The application will display the matching Pokemon image
   - Information includes the Pokemon's name, average RGB value, and color distance

4. Click **Stop Color Tracking** when done

## Building the Project

### Building the Java Backend

After making changes to the Java code:

```bash
cd my-app
mvn clean package
```

This creates the JAR file at `target/my-app-1.0-SNAPSHOT.jar` // or whatever snapshot currently on

### Building the Python GUI as a Standalone Application

To create a standalone executable:

```bash
cd my-app
pyinstaller --onefile --windowed --name PokemonColorMatcher src/main/python/pokemon_color_matcher.py
```

This creates an executable in the `dist` directory.

### Creating a Complete Distributable Package

To package both the Python application and required Java resources:

```bash
# After running pyinstaller
mkdir -p dist/src/main/resources/images
cp -R src/main/resources/images/* dist/src/main/resources/images/
cp target/my-app-1.0-SNAPSHOT.jar dist/
```

## Development Workflow

### Modifying the Java Backend

1. Edit Java files in `src/main/java`
2. Rebuild with `mvn clean package`
3. Run the application to test changes

### Modifying the Python GUI

1. Edit pokemon_color_matcher.py
2. Run the script directly for testing
3. Rebuild with PyInstaller when changes are complete
```
bash
cd /Users/jeshualinderjimenez/Desktop/UPRM/28-imageparser/my-app
pyinstaller --onefile --windowed --name PokemonColorMatcher src/main/python/pokemon_color_matcher.py
```
***
# Navigate to your project directory
cd /Users/jeshualinderjimenez/Desktop/UPRM/28-imageparser/my-app

# Create a virtual environment
python -m venv venv

# Activate it
source venv/bin/activate  # On macOS/Linux
# or
# venv\Scripts\activate  # On Windows

# Install required packages
pip install PyQt5 mss

# Run the script
python src/main/python/pokemon_color_matcher.py
***
## Project Structure

- `src/main/java` - Java backend code
- `src/main/resources/images` - Pokemon images
- `src/main/python` - Python GUI code
- `target` - Compiled Java classes and JAR file
- `image_info.json` - Generated Pokemon data
- `documentPokemon.flag` - Flag indicating data has been processed

## Troubleshooting

- **Java initialization fails**: Ensure the Java JAR file is correctly built and located at `target/my-app-1.0-SNAPSHOT.jar`
- **Images not displaying**: Check that the image paths in `image_info.json` are correct
- **Python dependencies missing**: Run `pip install PyQt5 mss` to install required packages
- **Color tracking not working**: Make sure you've clicked "Start Color Tracking"

## License

This project is licensed under the MIT License - see the LICENSE file for details.