package calve23.moduleoptimizer;

import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicReference;

/** we will store the region captured for ease of access with AtomicReference
 * why atomic reference, the region selection UI  and jnative are on separate threads
 * so now with our saved region we can guarantee when the user clicks a keybind to
 * grab the next module the region will stay the same and the user wont have to make
 * another rectangle again
*/
public final class StoredRegion {

    public static final AtomicReference<Rectangle> REGION =
            new AtomicReference<>();
}
