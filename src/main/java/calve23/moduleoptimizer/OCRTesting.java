package calve23.moduleoptimizer;
import net.sourceforge.tess4j.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OCRTesting {

    private static final Tesseract tesseract = create();

    private static Tesseract create() {
        Tesseract t = new Tesseract();
        t.setDatapath("tess_data");
        t.setLanguage("eng");
        t.setVariable(        "tessedit_char_whitelist",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+0123456789& ");
        return t;
    }
    public static void showImage(BufferedImage img, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static LinkEffect beginOCR(BufferedImage image) throws TesseractException {
        LinkEffect e = getLinkEffectValues(image);
        if (e != null) return e;

        e = getLinkEffectValues(cropTop(image));
        if (e != null) return e;

        return getLinkEffectValues(cropBottom(image));
    }

    public static LinkEffect getLinkEffectValues(BufferedImage image) throws TesseractException {
        BufferedImage filtered = filter(image);

        String result = tesseract.doOCR(filtered)
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replaceAll("\\s+", " ")
                .trim();

        Matcher matcher = Pattern.compile("([A-Za-z ]+?)\\s*\\+\\s*(\\d+)").matcher(result);

        if (!matcher.find()) return null;
        String name = cleanName(matcher.group(1));
        int value = Integer.parseInt(matcher.group(2));

        if (value < 1 || value > 20) return null;
        if (name.length() < 4 || name.length() > 30) return null;

        try {
            return new LinkEffect(LinkEffectName.valueOf(name), value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static BufferedImage cropTop(BufferedImage src) {
        if (30 >= src.getHeight()) {
            return src; // nothing to crop or invalid
        }

        return src.getSubimage(
                0,
                30,
                src.getWidth(),
                src.getHeight() - 30
        );
    }
    private static BufferedImage cropBottom(BufferedImage src) {
        if (30 >= src.getHeight()) {
            return src; // invalid crop, return original
        }

        return src.getSubimage(
                0,
                0,
                src.getWidth(),
                src.getHeight() - 30
        );
    }
    private static BufferedImage filter(BufferedImage src) {
        int scale = 3;

        // 1) Scale up
        BufferedImage scaled = new BufferedImage(
                src.getWidth() * scale,
                src.getHeight() * scale,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, scaled.getWidth(), scaled.getHeight(), null);
        g.dispose();
        // 2) Grayscale
        BufferedImage gray = new BufferedImage(
                scaled.getWidth(),
                scaled.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );
        Graphics2D g2 = gray.createGraphics();
        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();

        // 3) Threshold (BLACK text on WHITE background)
        BufferedImage binary = new BufferedImage(
                gray.getWidth(),
                gray.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY
        );
        for (int y = 0; y < gray.getHeight(); y++) {
            for (int x = 0; x < gray.getWidth(); x++) {
                int pixel = gray.getRaster().getSample(x, y, 0);
                // Dark pixels -> WHITE (0)
                // Light pixels -> BLACK (255)
                // INVERSE for black text
                int value = (pixel > 140) ? 0 : 255;

                binary.getRaster().setSample(x, y, 0, value);
            }
        }

        return binary;
    }

    private static String cleanName(String name) {
        return name
                .replaceAll("^(?:[A-Za-z]\\s+)+", "") // remove repeated single-letter junk
                .replaceAll("^[^A-Za-z]+", "")   // remove non-letter junk prefix
                .replaceAll("[^A-Za-z ]+$", "")  // remove junk suffix
                .trim()
                .toUpperCase()
                .replaceAll(" ", "_");
    }
}
