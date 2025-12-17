package calve23.moduleoptimizer;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import net.sourceforge.tess4j.TesseractException;
import nu.pattern.OpenCV;
import org.opencv.core.Core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ModuleCaptureKey implements NativeKeyListener, NativeMouseListener {
    private long lastCaptureMs = 0;
    private Module currentModule;
    private static volatile boolean isScoring = false;
    private boolean canSave = false;


    public void nativeMousePressed(NativeMouseEvent m) {
        long now = System.currentTimeMillis();
        if (now - lastCaptureMs < 300) return;
        lastCaptureMs = now;

        if (m.getButton() == NativeMouseEvent.BUTTON2 && Stored.REGION.get() != null) {
            if (canSave && currentModule != null) {
                ModuleInventory.add(currentModule);
                System.out.println("Module saved");
                currentModule = null;
                canSave = false;
            } else {
                try {
                    captureMod();
                } catch (IOException | TesseractException | AWTException ex) {
                    System.err.println("Error capturing region: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        //reduce the chance for an accidental double click (remove option to hold)
        long now = System.currentTimeMillis();
        if (now - lastCaptureMs < 300) return;
        lastCaptureMs = now;

        //going to do F8 for now and allow custom later
        if (e.getKeyCode() == NativeKeyEvent.VC_F8) {

        } else if (e.getKeyCode() == NativeKeyEvent.VC_F7) {
            currentModule = null; //new region means new module to snap
            createRegion();
        } else if (e.getKeyCode() == NativeKeyEvent.VC_BACKSPACE) {
            exitProgram();
        } else if (e.getKeyCode() == NativeKeyEvent.VC_Y && currentModule != null) {
            System.out.println("Selected Module Saved... Right-Click to capture another Mod\n");
            ModuleInventory.add(currentModule);
            currentModule = null;
        } else if (e.getKeyCode() == NativeKeyEvent.VC_N && currentModule != null) {
            System.out.println("Module unselected");
            currentModule = null;
        }  else if (e.getKeyCode() == NativeKeyEvent.VC_F9 && ModuleInventory.getModules().size() >= 4) {
            // >= 4 is quick and easy but i need to check for less than 4 and fix scoring with it
            isScoring = true;
            //let it finish scoring before doing any other input
            try {
                System.out.println("Scoring Modules\n");
                ScoreModules.score(ModuleInventory.getModules());
            } finally {
                isScoring = false;
            }
        } else if (e.getKeyCode() == NativeKeyEvent.VC_F10) {
            ModuleInventory.load();
        }
    }

    private void createRegion() {
        System.out.println("Creating region...");
        RegionSelect selector = new RegionSelect();
        selector.makeRegion();
        if (Stored.REGION.get().getWidth() < 1 || Stored.REGION.get().getHeight() < 1) {
            System.err.println("Region width or height is less than 1\n");
            Stored.REGION.set(null);
            return;
        }
        System.out.println("Region created");
    }

    private void captureMod() throws IOException, TesseractException, AWTException {
        Robot robot = new Robot();
        BufferedImage capture = robot.createScreenCapture(Stored.REGION.get());

        File outFile = new File("debug_images/module_capture.png");
        ImageIO.write(capture, "png", outFile);

        //4 create module
        Module mod = OCRTesting.getLinkEffectValues(outFile);
        if (mod == null || mod.getEffects().isEmpty()) {
            System.out.println("No mod found, try re-doing box");
            return;
        }
        System.out.println("Right-Click Save or F7 to re-do region");
        System.out.println(mod.getEffects().toString());
        currentModule = mod;
        canSave = true;
    }

    private void exitProgram() {
        System.out.println("ESC PRESSED, ENDING PROGRAM");
        //unregister hook and remove listener to make sure program doesn't hang
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException ex) {
            ex.printStackTrace();
        }
        System.exit(0);
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
