# Module Optimizer Tool

A desktop tool for Blue Protocol that:

- Captures a region of the screen
- OCRs module stats
- Parses effects like ARMOR+10
- Builds Module objects
- Scores all possible 4 module combinations based on desired outcome
- Outputs users' 4 best modules

## WIP
- 1 Save best score/modules to CSV or JSON
- 2 create UI for ease of use
- 3 Four Modules types means: 
  - 4 regions region selects that are consistent
  - save them and click button for which you are snapping
- 4 Automate capture and cursor movement (Like left click a module and it automatically does everything)


## Tech
- Java 17+
- Tess4J OCR
- JNativeHook for global hotkeys
- Swing overlay for region selection
