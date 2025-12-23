package calve23.moduleoptimizer;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

public final class ConsoleOn {

    private ConsoleOn() {}

    private static final HWND HWND_TOPMOST = new HWND(new com.sun.jna.Pointer(-1));

    private static final int SWP_NOMOVE = 0x0002;
    private static final int SWP_NOSIZE = 0x0001;

    public static void top() {
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) return;

        User32.INSTANCE.SetWindowPos(
                hwnd,
                HWND_TOPMOST,
                0, 0, 0, 0,
                SWP_NOMOVE | SWP_NOSIZE
        );
    }
}