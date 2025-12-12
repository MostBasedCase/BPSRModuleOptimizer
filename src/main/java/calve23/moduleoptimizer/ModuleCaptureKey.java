package calve23.moduleoptimizer;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.event.KeyListener;
import java.util.logging.*;

public class ModuleCaptureKey implements NativeKeyListener {
    private long lastCaptureMs = 0;
    public static void main(String[] args) throws Exception {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException("Failed to register native hook", e);
        }

        GlobalScreen.addNativeKeyListener(new ModuleCaptureKey());
        System.out.println("Listening... Press F8 anywhere or Esc to end.");
    }
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        //reduce the chance for a accidental double click
        long now = System.currentTimeMillis();
        if (now - lastCaptureMs < 300) return;
        lastCaptureMs = now;
        if (e.getKeyCode() == NativeKeyEvent.VC_F8) {
            System.out.println("F8 PRESSED");

        } else if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
            System.out.println("ESC PRESSED, ENDING PROGRAM");
            System.exit(0);
        }
    }
}
