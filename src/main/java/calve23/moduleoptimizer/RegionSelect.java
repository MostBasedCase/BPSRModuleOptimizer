package calve23.moduleoptimizer;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegionSelect extends JWindow implements NativeKeyListener {
    private Point startPoint;
    private Point endPoint;
    private Rectangle region;

    public RegionSelect() {
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 1));
        setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                startPoint = e.getPoint();
                endPoint = startPoint;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                endPoint = e.getPoint();
                region = makeRectangle(startPoint, endPoint);
                Stored.REGION.set(region);//saving the region with atomic reference

                dispose();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endPoint = e.getPoint();
                repaint();
            }
        });
    }
    private Rectangle makeRectangle(Point startPoint, Point endPoint) {
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);
        int w = Math.abs(endPoint.x - startPoint.x);
        int h = Math.abs(endPoint.y - startPoint.y);
        int padding = 50;

        int newY = Math.max(0, y - padding);
        int newH = h + (y - newY) + padding;

        return new Rectangle(x, newY, w, newH);
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (startPoint != null && endPoint != null) {
            Rectangle r = makeRectangle(startPoint, endPoint);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.fillRect(0, 0, getWidth(), r.y);
            g2d.fillRect(0, r.y + r.height, getWidth(), getHeight() - (r.y + r.height));
            g2d.fillRect(0, r.y, r.x, r.height);
            g2d.fillRect(r.x + r.width, r.y, getWidth() - (r.x + r.width), r.height);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(r.x, r.y, r.width, r.height);
        }
    }
    public void makeRegion() {
        setVisible(true);
        while (region == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
                System.out.println("Interrupted");
            }
        }
    }
}
