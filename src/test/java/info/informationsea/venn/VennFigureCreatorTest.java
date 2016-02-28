/*
    Venn Draw : Draw Venn Diagram
    Copyright (C) 2016 Yasunobu OKAMURA All Rights Reserved

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package info.informationsea.venn;

import info.informationsea.venn.graphics.VennDrawGraphics2D;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class VennFigureCreatorTest {


    public static final File DIST_DIR = new File(new File("build"), "test-images4");

    @BeforeClass
    public static void setup() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        DIST_DIR.mkdirs();
    }

    @Test
    public void venn2() throws Exception {
        VennFigure<String> vennFigure =
                VennFigureCreator.createVennFigure(new CombinationSolver<>(SampleCombinationGenerator.twoCombinationValues));
        BufferedImage image = prepareTestImage(800, 800);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        VennDrawGraphics2D.draw(vennFigure, g, new Dimension(800, 800));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "two.png"));
    }

    @Test
    public void venn3() throws Exception {
        VennFigure<String> vennFigure =
                VennFigureCreator.createVennFigure(new CombinationSolver<>(SampleCombinationGenerator.threeCombinationValues));
        BufferedImage image = prepareTestImage(800, 800);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        VennDrawGraphics2D.draw(vennFigure, g, new Dimension(800, 800));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "three.png"));
    }


    public static BufferedImage prepareTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        return image;
    }

}