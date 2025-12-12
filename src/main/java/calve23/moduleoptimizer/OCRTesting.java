package calve23.moduleoptimizer;
import net.sourceforge.tess4j.*;
import org.opencv.core.*;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OCRTesting {

    private static final Tesseract tesseract = create();

    private static Tesseract create() {
        Tesseract t = new Tesseract();
        t.setDatapath("tess_data");
        t.setLanguage("eng");
        return t;
    }
    private static LinkEffect makeEffect(String name, int value) {
        //replace junk text with "" and trim around the word
        LinkEffect le = null;
        String cleanedName = name
                .replaceAll("^(?:[A-Za-z]\\s+)+", "") // remove repeated single-letter junk
                .replaceAll("^[^A-Za-z]+", "")   // remove non-letter junk prefix
                .replaceAll("[^A-Za-z ]+$", "")  // remove junk suffix
                .trim()
                .toUpperCase()
                .replaceAll(" ", "_");
        System.out.println("Before: (" + name + ") After: (" +cleanedName + ")");
        le = new LinkEffect(LinkEffectName.valueOf(cleanedName), value);
        return le;
    }

    public static Module getLinkEffectValues(File image) throws TesseractException, IOException {
        Module m = null;
        LinkEffect le = null;
        List<LinkEffect> effects = new ArrayList<>();

        String result = tesseract.doOCR(image);

        //we see "Strength+8" or "Attack SPD+3" this is the pattern we want to catch
        //  ([A-Za-z ]+)  (\\+)  (\\d+)
        //  (1 or more letters and spaces) followed by (+) then (1 or more digits)
        Pattern pattern = Pattern.compile("([A-Za-z ]+)\\+(\\d+)");
        Matcher matcher = pattern.matcher(result);

        while (matcher.find()) {
            String name = matcher.group(1).trim();
            int value = Integer.parseInt(matcher.group(2));
             //seems consist will make checks for potential errors
            effects.add(makeEffect(name, value));
        }
        m = new Module(effects);
        return  m;
    }
}
