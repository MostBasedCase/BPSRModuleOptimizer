# Module Optimizer Tool

A desktop tool for Blue Protocol that:

- Captures a region of the screen
- OCRs module stats
- Parses effects like ARMOR+10
- Builds Module objects
- Scores all possible 4 module combinations based on desired outcome
- Outputs users' 4 best modules

## Demo
[DEMO](https://github.com/user-attachments/assets/2d567c5b-61f1-42e9-b23d-a8dc1fde9e02)


## ALPHA TEASE
- You can download it [here](https://drive.google.com/file/d/18etNtpdSxfnQVdExS0mxkTGgGvk1jcKU)
 run it on the command line for now if you want!
- Missing removal and modyfing captured modules (can edit stuff in JSONs).
- The read me will help you get started.


## WIP
- 1 ask user how many mods in row (in inventory)
- If they add in order its easy to locate and adjust list 
- inside and outside of game
- 2 create UI for ease of use
- 3 Automate cursor movement 


## Tech
- Java 17+
- Tess4J OCR
- JNativeHook for global hotkeys
- Swing overlay for region selection
