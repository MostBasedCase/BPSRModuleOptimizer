package calve23.moduleoptimizer;
import net.sourceforge.tess4j.*;
import org.opencv.core.*;

import org.opencv.imgcodecs.Imgcodecs;

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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        return t;
    }


    private static void displayModifiedImage(Mat image, String path) {
        //save the modified matrix to see
        Imgcodecs.imwrite(path, image);

        //Opening the file for debugging on run
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (IOException e) {
            System.out.println("Could not open the image automatically. Please open it manually.");
        }
    }
    private static LinkEffect makeEffect(String name, int value) {
        String cleanedName = name.replaceAll("^[A-Za-z]{1,2}\\s+", "").trim();
        LinkEffect le = null;
        cleanedName = cleanedName.toLowerCase();
        if(cleanedName.contains("strength boost")) le = new LinkEffect(LinkEffectName.STRENGTH_BOOST, value);
        if(cleanedName.contains("ability boost")) le = new LinkEffect(LinkEffectName.AGILITY_BOOST, value);
        if(cleanedName.contains("intellect boost")) le = new LinkEffect(LinkEffectName.INTELLECT_BOOST, value);
        if(cleanedName.contains("special attack")) le = new LinkEffect(LinkEffectName.SPECIAL_ATTACK, value);
        if(cleanedName.contains("elite strike")) le = new LinkEffect(LinkEffectName.ELITE_STRIKE, value);
        if(cleanedName.contains("healing boost")) le = new LinkEffect(LinkEffectName.HEALING_BOOST, value);
        if(cleanedName.contains("healing enhance")) le = new LinkEffect(LinkEffectName.HEALING_ENHANCE, value);
        if(cleanedName.contains("armor")) le = new LinkEffect(LinkEffectName.ARMOR, value);
        if(cleanedName.contains("resistance")) le = new LinkEffect(LinkEffectName.RESISTANCE, value);
        if(cleanedName.contains("cast focus")) le = new LinkEffect(LinkEffectName.CAST_FOCUS, value);
        if(cleanedName.contains("attack spd")) le = new LinkEffect(LinkEffectName.ATTACK_SPD, value);
        if(cleanedName.contains("crit focus")) le = new LinkEffect(LinkEffectName.CRIT_FOCUS, value);
        if(cleanedName.contains("luck focus")) le = new LinkEffect(LinkEffectName.LUCK_FOCUS, value);
        return le;
    }



    private static Module getLinkEffectValues(File image) throws TesseractException {
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

    public static void main(String[] args) throws TesseractException {
        try {
            //1 user picks a region
            RegionSelect selector = new RegionSelect();
            System.out.println("Drag a box on the screen...");
            Rectangle region = selector.makeRegion();

            //2 Robot captures region
            Robot robot = new Robot();

            //**Need error checking for width and height to be > 0**
            BufferedImage capture = robot.createScreenCapture(region);
            System.out.println("Atomic reference: " + StoredRegion.REGION.get());

            //3 save image for ocr processing/debugging
            //when good to go I will stop saving images and just read
            File outFile = new File("debug_images/module_capture.png");
            ImageIO.write(capture, "png", outFile);

            //4 create module
            Module mod = getLinkEffectValues(outFile);
            System.out.println(mod.getEffects().toString());

            System.out.println("Saved screenshot to: " + outFile.getAbsolutePath());

        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }

    }
}
