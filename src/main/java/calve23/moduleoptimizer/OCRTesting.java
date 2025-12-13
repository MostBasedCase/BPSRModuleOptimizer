package calve23.moduleoptimizer;
import net.sourceforge.tess4j.*;
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

    public static Module getLinkEffectValues(File image) throws TesseractException, IOException {
        List<LinkEffect> effects = new ArrayList<>();
        String result = tesseract.doOCR(image);

        //we see "Strength+8" or "Attack SPD+3" this is the pattern we want to catch
        //  ([A-Za-z ]+)  (\\+)  (\\d+)
        //  (1 or more letters and spaces) followed by (+) then (1 or more digits)
        Pattern pattern = Pattern.compile("([A-Za-z ]+)\\+(\\d+)");
        Matcher matcher = pattern.matcher(result);

        boolean fail = false;
        while (matcher.find()) {
            String name = matcher.group(1).trim();
            int value = Integer.parseInt(matcher.group(2));
            name = cleaName(name);
            try {
                LinkEffectName link = LinkEffectName.valueOf(name);
                effects.add(new LinkEffect(link, value));
            } catch (IllegalArgumentException e) {
                System.out.println("Error in processing: Try again or re-do box.");
                fail = true;
                break;
            }
        }
        if (fail) {
            return null;
        } else {
            return new Module(effects);
        }
    }

    private static String cleaName(String name) {
        return name
                .replaceAll("^(?:[A-Za-z]\\s+)+", "") // remove repeated single-letter junk
                .replaceAll("^[^A-Za-z]+", "")   // remove non-letter junk prefix
                .replaceAll("[^A-Za-z ]+$", "")  // remove junk suffix
                .trim()
                .toUpperCase()
                .replaceAll(" ", "_");
    }
}
