package calve23.moduleoptimizer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class RegionSelect extends JWindow {

    private Point startPoint;
    private Point endPoint;
    private Rectangle region;

    public RegionSelect() {
        //keep window on top of others
        setAlwaysOnTop(true);

        //using alpha 1 since events won't work if alpha 0
        setBackground(new Color(0, 0, 0, 1));

        //full screen usable
        setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Drag a box on the screen...");
                startPoint = e.getPoint();
                endPoint = startPoint;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                endPoint = e.getPoint();
                //**Need error checking for width and height to be > 0**
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
        return new Rectangle(x, y, w, h);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (startPoint != null && endPoint != null) {
            Rectangle r = makeRectangle(startPoint, endPoint);
            Graphics2D g2d = (Graphics2D) g;

            //Dim outside of rectangle
            g2d.setColor(new Color(0, 0, 0, 100)); // dim color

            //Top dim area
            g2d.fillRect(0, 0, getWidth(), r.y);

            //Bottom dim area
            g2d.fillRect(0, r.y + r.height, getWidth(), getHeight() - (r.y + r.height));

            //Left dim area
            g2d.fillRect(0, r.y, r.x, r.height);

            //Right dim area
            g2d.fillRect(r.x + r.width, r.y, getWidth() - (r.x + r.width), r.height);

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(r.x, r.y, r.width, r.height);

        }
    }
    /**
     * show overlay and wait until user drags and releases
     * then we can return the region the user wants
     */
    public Rectangle makeRegion() {
        setVisible(true);
        while (region == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
                System.out.println("Interrupted");
            }
        }
        return region;
    }
}
