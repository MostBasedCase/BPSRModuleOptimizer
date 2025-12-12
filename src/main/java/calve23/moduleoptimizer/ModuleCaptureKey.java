package calve23.moduleoptimizer;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ModuleCaptureKey implements NativeKeyListener {
    private long lastCaptureMs = 0;

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        //reduce the chance for an accidental double click (remove option to hold)
        long now = System.currentTimeMillis();
        if (now - lastCaptureMs < 300) return;
        lastCaptureMs = now;

        //going to do F8 for now and allow custom later
        if (e.getKeyCode() == NativeKeyEvent.VC_F8) {
            if (Stored.REGION.get() != null) {
                try {
                    captureMod();
                } catch (IOException | TesseractException | AWTException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                System.out.println("No capture found");
            }
        } else if (e.getKeyCode() == NativeKeyEvent.VC_F7) {
            createRegion();
        } else if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            exitProgram();
        }
    }

    private void createRegion() {
        System.out.println("Creating region...");
        RegionSelect selector = new RegionSelect();
        selector.makeRegion();
        System.out.println("Region created: " + Stored.REGION.get());
    }

    private void captureMod() throws IOException, TesseractException, AWTException {
        Robot robot = new Robot();
        BufferedImage capture = robot.createScreenCapture(Stored.REGION.get());

        File outFile = new File("debug_images/module_capture.png");
        ImageIO.write(capture, "png", outFile);

        //4 create module
        Module mod = OCRTesting.getLinkEffectValues(outFile);
        if (!mod.getEffects().isEmpty()) {
            System.out.println(mod.getEffects().toString());
        } else {
            System.out.println("Mod not found");
        }

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
        GlobalScreen.addNativeKeyListener(new ModuleCaptureKey());
        System.out.println("Listening... Press F7 to create region, Press F8 anywhere or Esc to end.");
    }
}
