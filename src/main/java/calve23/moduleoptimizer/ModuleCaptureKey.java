package calve23.moduleoptimizer;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import net.sourceforge.tess4j.TesseractException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ModuleCaptureKey implements NativeKeyListener, NativeMouseListener {
    private volatile State state = State.READY;
    private volatile boolean canUndo = false;
    private long lastCaptureMs = 0;
    private Module currentModule;

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
    private void handle(Action action) throws TesseractException, IOException, AWTException {
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

        }
    }

    private void undoSave() {
                System.out.println("Removed: " + ModuleInventory.removeLast().toString());
        canUndo = false;
    }

    private void saveMod() {
        state = State.READY_TO_CAPTURE;
        ModuleInventory.add(currentModule);
        System.out.println("Mod Saved | Backspace to undo");
        canUndo = true;
    }

    private boolean canNotDo(Action action) {
        if (action == Action.EXIT) return false; //always allow exit
        if (state == State.SCORING || state == State.CREATING_REGION || state == State.CAPTURING) return true; //busy

        return !switch (action) {
            case SCORE -> ModuleInventory.MODULES != null &&
                          ModuleInventory.size() >= 4;
            case SAVE -> currentModule != null && state == State.READY_TO_SAVE;
            case UNDO_SAVE -> canUndo;
            default -> true;
        };
    }
    private void score() {
        if (canNotDo(Action.SCORE)) {
            System.out.println("Need at least 4 modules to score.");
            return;
        }
        state = State.SCORING;
        try {
            ScoreModules.score(ModuleInventory.MODULES);
        } finally {
            state = State.READY;
        }
    }
    private void createRegion() {
        state = State.CREATING_REGION;
        try {
            System.out.println("Creating a region...");
            RegionSelect selector = new RegionSelect();
            selector.makeRegion();
        } catch (Exception e) {
            System.out.println("Error creating region: " + e.getMessage());
        } finally {
            var r = Stored.REGION.get();
            boolean valid = r != null && r.getWidth() > 0 && r.getHeight() > 0;

            if (!valid) {
                System.err.println("NULL or < (1x1).\n");
                Stored.REGION.set(null);
                state = State.READY;
            } else {
                System.out.println("Right-Click to capture | F7 to re-do region");
                state = State.READY_TO_CAPTURE;
            }
        }
    }

    private void captureMod() throws TesseractException, IOException, AWTException {
        state = State.CAPTURING;

        Robot robot = new Robot();
        BufferedImage capture = robot.createScreenCapture(Stored.REGION.get());

        File outFile = new File("debug_images/module_capture.png");
        ImageIO.write(capture, "png", outFile);

        Module mod = OCRTesting.getLinkEffectValues(outFile); //fails state should go back to READY
        if (mod == null || mod.getEffects().isEmpty()) {
            System.out.println("No mod found, try re-doing box");
            state = State.READY;
            return;
        }
        state = State.READY_TO_SAVE;
        System.out.println("Right-Click re-capture | Space-bar to save | F7 to re-do region");
        System.out.println(mod.getEffects().toString());
        currentModule = mod;
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


    public static void main(String[] args) {

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException("Failed to register native hook", e);
        }
        ModuleCaptureKey listener = new ModuleCaptureKey();
        GlobalScreen.addNativeKeyListener(listener);
        GlobalScreen.addNativeMouseListener(listener);
        System.out.println("F10 to load | F9 to Score | F7 to select region | F6 for skill priority | ESC to exit");
    }
}
