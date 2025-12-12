# Module Optimizer Tool

A desktop tool for Blue Protocol that:

- Captures a region of the screen
- OCRs module stats
- Parses effects like ARMOR+10
- Builds Module objects
- Capture modules from in game
- Scores combinations of 4 modules based on desired outcome

# WIP

1 Scoring all random modules combinations and testing weights
2 record area or keep on screen and use global key listener to snap modules
3 create UI for ease

## Tech
- Java 17+
- Tess4J OCR
- JNativeHook for global hotkeys
- Swing overlay for region selection