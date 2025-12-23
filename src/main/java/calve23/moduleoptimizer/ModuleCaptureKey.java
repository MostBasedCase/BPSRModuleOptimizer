package calve23.moduleoptimizer;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import net.sourceforge.tess4j.TesseractException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ModuleCaptureKey implements NativeKeyListener, NativeMouseListener {
    private static volatile State state = State.READY;
    private static volatile State previousState = State.READY;
    private volatile boolean canUndo = false;
    private long lastCaptureMs = 0;
    private Module currentModule;
    private ModuleType moduleType = null;

    private static final java.util.concurrent.CountDownLatch[] REGION_ARM = { new java.util.concurrent.CountDownLatch(1) };




    @Override
    public void nativeMousePressed(NativeMouseEvent m) {
        if (onCoolDown(lastCaptureMs)) return;
        if (m.getButton() != NativeMouseEvent.BUTTON2) return;
        if (state != State.READY_TO_CAPTURE && state != State.READY_TO_SAVE)  return;
        handleThread(Action.CAPTURE);
    }
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (onCoolDown(lastCaptureMs)) return;

        Action a = switch (e.getKeyCode()) {
            case NativeKeyEvent.VC_F6 -> Action.SET_PRIORITY;
            case NativeKeyEvent.VC_F7 -> Action.CREATE_REGION;
            case NativeKeyEvent.VC_F9 -> Action.SCORE;
            case NativeKeyEvent.VC_F10 -> Action.LOAD;
            case NativeKeyEvent.VC_ESCAPE -> Action.EXIT;
            case NativeKeyEvent.VC_SPACE -> Action.SAVE;
            case NativeKeyEvent.VC_BACKSPACE -> Action.UNDO_SAVE;
            case NativeKeyEvent.VC_P -> Action.PAUSE;
            case NativeKeyEvent.VC_1 ->  Action.GOLD_2_CAPTURE; //3 regions
            case NativeKeyEvent.VC_2 ->  Action.GOLD_1_CAPTURE; //3 regions
            case NativeKeyEvent.VC_3 ->  Action.PURPLE_CAPTURE; //2 regions
            case NativeKeyEvent.VC_4 ->  Action.BLUE_CAPTURE;   // 1 region (4 region sets)
            default -> null;
        };
        handleThread(a);
    }
    private void handleThread(Action a) { //Keybinds get in queue unless I make a new thread on some actions
        if (a != null) {
            new Thread(() -> {
                try {
                    handle(a);
                } catch (Exception err) {throw new RuntimeException(err);}
            }).start();
        }
    }
    private void handle(Action action) throws TesseractException, AWTException {
        if (canNotDo(action)) return;
        switch (action) {
        case CREATE_REGION -> createRegion();
        case CAPTURE -> captureMod();
        case SAVE -> saveMod();
        case LOAD ->  ModuleInventory.load();
        case SCORE -> score();
        case EXIT -> exitProgram();
        case SET_PRIORITY -> System.out.println("Set priority coming soon");
        case UNDO_SAVE -> undoSave();
        case PAUSE -> pause();
        case GOLD_2_CAPTURE -> updateModuleType(ModuleType.A);
        case GOLD_1_CAPTURE -> updateModuleType(ModuleType.B);
        case PURPLE_CAPTURE -> updateModuleType(ModuleType.C);
        case BLUE_CAPTURE -> updateModuleType(ModuleType.D);
        }
    }

    private void updateModuleType(ModuleType mT) {
        moduleType = mT;
        System.out.println("Module type for capturing changed: " + mT.getName());
    }

    private void pause() {
        if(state != State.PAUSED)  {
            previousState = state;
            state = State.PAUSED;
            System.out.println("PAUSED");
        } else {
            state = previousState;
            System.out.println("UNPAUSED");
        }
    }
    private void undoSave() {
                System.out.println("Removed: " + ModuleInventory.removeLast().toString());
        canUndo = false;
    }
    private void saveMod() {
        updateState(State.READY_TO_CAPTURE);
        ModuleInventory.add(currentModule);
        System.out.println("Mod Saved | Backspace to undo");
        canUndo = true;
    }
    private boolean canNotDo(Action action) {
        if (action == Action.EXIT) return false; //always allow exit
        if (action == Action.PAUSE) return state == State.CREATING_REGION; //always allow pause except when creating region
        if (action == Action.CREATE_REGION) return state != State.MODULE_REGION_SETUP;
        if (state == State.SCORING || state == State.CREATING_REGION || state == State.CAPTURING || state == State.PAUSED) return true; //busy can not do;
        return !switch (action) {
            case SCORE -> ModuleInventory.MODULES != null &&
                          ModuleInventory.size() >= 4;
            case SAVE -> state == State.READY_TO_SAVE;
            case UNDO_SAVE -> canUndo;
            case CAPTURE -> moduleType != null;
            default -> true;
        };
    }
    private void score() {
        if (canNotDo(Action.SCORE)) {
            System.out.println("Need at least 4 modules to score.");
            return;
        }
        updateState(State.SCORING);
        try {
            ScoreModules.score(ModuleInventory.MODULES);
        } finally {
            updateState(State.READY);
        }
    }
    private static void updateState(State newState) {
        previousState = state;
        state = newState;
    }
    private static void armNextRegionSelection() {
        REGION_ARM[0].countDown();
    }
    private static void createRegion() {
        System.out.println("Drag a box on the screen...");
        try {
            RegionSelect selector = new RegionSelect();
            Stored.REGION.set(null);
            selector.makeRegion();
        } catch (Exception e) {
            System.out.println("Error creating region: " + e.getMessage());
            System.out.println("Try Again");
            createRegion();
        } finally {
            var r = Stored.REGION.get();
            boolean valid = r != null && r.getWidth() > 0 && r.getHeight() > 0;
            if (!valid) {
                System.err.println("NULL or < (1x1).");
                System.out.println("Try Again");
                createRegion();
            }
        }
        armNextRegionSelection();
    }

    private void captureMod() throws TesseractException, AWTException {
        updateState(State.CAPTURING);
        System.out.println("Capturing: " + moduleType.getName());
        List<LinkEffect> effects = new ArrayList<>();
        List<Rectangle> rectangles = RegionPersistence.REGION_MAP.get(moduleType);
        LinkEffect effect;
        for (int i = 0; i < moduleType.requiredRegions(); i++) {
            Robot robot = new Robot();
            BufferedImage capture = robot.createScreenCapture(rectangles.get(i));
            effect = OCRTesting.beginOCR(capture);
            effects.add(effect);
        }
        try {
            currentModule = new Module(effects);
        } catch (Exception e) {
            System.out.println("Failed to capture module, try again");
            updateState(State.READY_TO_CAPTURE);
            return;
        }
        updateState(State.READY_TO_SAVE);
        System.out.println(currentModule.toString());
        System.out.println("To save press SPACE. Otherwise capture another mod with right-click");
    }



    private void exitProgram() {
        System.out.println("ESC PRESSED, ENDING PROGRAM");
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
    private boolean onCoolDown(long lastMs) {
        long now = System.currentTimeMillis();
        if (now - lastMs < (long) 300) return true;
        lastCaptureMs = now;
        return false;
    }



    private static void createRegionPresets() {
        updateState(State.MODULE_REGION_SETUP);
        ModuleType curr = ModuleType.A;
        EnumMap<ModuleType, List<Rectangle>> map = new EnumMap<>(ModuleType.class);
        while (curr != null) {
            List<Rectangle> regionList = new ArrayList<>();
            int regionCount = curr.requiredRegions();
            System.out.println("Create " + regionCount + " region(s) for the " + curr.getName());
            System.out.println("When ready, press F7 to begin selecting region...");
            for (int i = 1; i <= regionCount; i++) {
                try {
                    REGION_ARM[0].await(); // HARD STOP until F7
                } catch (InterruptedException ignored) {}
                REGION_ARM[0] = new java.util.concurrent.CountDownLatch(1);
                // reset for next region *before* opening selection, so next iteration can wait again
                regionList.add(Stored.REGION.get());
                if (i == regionCount) {
                    System.out.println("All regions captured for the " + curr.getName());
                } else {
                    System.out.println("Region " + (i) + " created. " + (regionCount-i) + " more remaining. Press F7 to begin selecting region...");
                }
            }
            map.put(curr, regionList);
            curr = curr.nextType();
        }
        updateState(State.READY);
        RegionPersistence.save(map);
    }


    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException("Failed to register native hook", e);
        }
        ModuleCaptureKey listener = new ModuleCaptureKey();
        GlobalScreen.addNativeKeyListener(listener);
        GlobalScreen.addNativeMouseListener(listener);
        if(!RegionPersistence.load()) {
            createRegionPresets();
        }
        updateState(State.READY_TO_CAPTURE);
    }


}
