package info.informationsea.venn.graphics;

import info.informationsea.venn.VennFigure;
import org.apache.poi.xslf.usermodel.SlideLayout;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * venndraw
 * Copyright (C) 2016 OKAMURA Yasunobu
 * Created on 2016/02/25.
 */
public class VennDrawSlidesTest {

    public static final File DIST_DIR = new File(new File("build"), "test-slides");

    @BeforeClass
    public static void setup() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        DIST_DIR.mkdirs();
    }

    @Test
    public void testDraw() throws Exception {
        VennFigure<String> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(0, 0), 0, 100, 100));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(0, 0), "Normal"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(50, 50), 0, 50, 100));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(200, 200), Math.PI / 4, 50, 100));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 0), 0, 50, 20, "#00ff00ff"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 20), 0, 50, 20, "#ff0000ff"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 40), 0, 50, 20, "#0000ffff"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 60), 0, 50, 20, "#00ff0050"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 80), 0, 50, 20, "#00ff0020"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 100), 0, 50, 20, "#00ff00"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(200, 200), "Rotated"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(100, 100), "Center"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(100, 120), "Left", VennFigure.TextJust.LEFT));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(100, 140), "Right", VennFigure.TextJust.RIGHT));

        XMLSlideShow slideShow = new XMLSlideShow();
        XSLFSlideMaster slideMaster = slideShow.getSlideMasters()[0];
        VennDrawSlides<String> drawSlides = new VennDrawSlides<>(vennFigure, slideShow.createSlide(slideMaster.getLayout(SlideLayout.TITLE_ONLY)));
        drawSlides.draw();

        try (FileOutputStream fos = new FileOutputStream(new File(DIST_DIR, "test.pptx"))) {
            slideShow.write(fos);
        }
    }
}