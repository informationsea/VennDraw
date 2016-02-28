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

package info.informationsea.venn.graphics;

import info.informationsea.venn.VennFigure;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class VennDrawGraphics2DTest {

    public static final File DIST_DIR = new File(new File("build"), "test-images");

    @BeforeClass
    public static void setup() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        DIST_DIR.mkdirs();
    }

    @Test
    public void testDraw() throws Exception {
        BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 800, 800);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        VennFigure<String> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<String>(new VennFigure.Point(0, 0), 0, 100, 100));
        vennFigure.addShape(new VennFigure.Text<String>(new VennFigure.Point(0, 0), "Hello"));
        graphics.setColor(Color.BLACK);
        VennDrawGraphics2D.draw(vennFigure, graphics, new Dimension(800, 800));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "test1.png"));

        // test contains
        Assert.assertEquals(1, vennFigure.ovalsAtPoint(new VennFigure.Point(0, 0)).size());
        Assert.assertEquals(0, vennFigure.ovalsAtPoint(new VennFigure.Point(100, 100)).size());
    }

    @Test
    public void testDraw2() throws Exception {
        BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 800, 800);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        VennFigure<String> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(0, 0), 0, 50, 100));
        graphics.setColor(Color.BLACK);
        VennDrawGraphics2D.draw(vennFigure, graphics, new Dimension(800, 800));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "test2.png"));
    }

    @Test
    public void testDraw3() throws Exception {
        BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 800, 800);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        VennFigure<String> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(100, 0), 0, 100, 100));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(50, 50), 0, 50, 100));
        graphics.setColor(Color.BLACK);
        VennDrawGraphics2D.draw(vennFigure, graphics, new Dimension(800, 800));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "test3.png"));
    }

    @Test
    public void testDraw4() throws Exception {
        BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 800, 800);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        VennFigure<String> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(0, 0), 0, 100, 100));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 0), 0, 50, 20, "#00ff00ff"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 20), 0, 50, 20, "#ff0000ff"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 40), 0, 50, 20, "#0000ffff"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 60), 0, 50, 20, "#00ff0050"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 80), 0, 50, 20, "#00ff0020"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(150, 100), 0, 50, 20, "#00ff00"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(0, 0), "Normal"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(50, 50), 0, 50, 100));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(200, 200), Math.PI / 4, 50, 100));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(200, 200), "Rotated"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(100, 100), "Center"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(100, 120), "Left", VennFigure.TextJust.LEFT));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(100, 140), "Right", VennFigure.TextJust.RIGHT));
        graphics.setColor(Color.BLACK);
        VennDrawGraphics2D.draw(vennFigure, graphics, new Dimension(800, 800));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "test4.png"));
    }


    @Test
    public void testDraw5() throws Exception {
        BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 800, 800);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        final double distance = 50;
        final double size = 100;

        VennFigure<String> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(-distance / 2, 0), 0, size, size));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(distance / 2, 0), 0, size, size));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(-size / 2, 0), "A"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(size / 2, 0), "B"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(0, 0), "AB"));
        graphics.setColor(Color.BLACK);
        VennDrawGraphics2D.draw(vennFigure, graphics, new Dimension(800, 800));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "test5.png"));
    }

    @Test
    public void testDraw6() throws Exception {
        BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 800, 800);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        final double distance = 35;
        final double size = 100;

        VennFigure<String> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(0, -distance), 0, size, size));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(distance * Math.cos(Math.PI / 6), distance / 2), 0, size, size));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(-distance * Math.cos(Math.PI / 6), distance / 2), 0, size, size));

        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(0, -distance * 1.5), "A"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(distance * Math.cos(Math.PI / 6) * 1.5, distance / 2 * 1.5), "B"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(-distance * Math.cos(Math.PI / 6) * 1.5, distance / 2 * 1.5), "C"));

        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(0, distance), "BC"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(distance * Math.cos(Math.PI / 6), -distance / 2), "AB"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(-distance * Math.cos(Math.PI / 6), -distance / 2), "AC"));

        vennFigure.addShape(new VennFigure.Text<String>(new VennFigure.Point(0, 0), "ABC"));
        graphics.setColor(Color.BLACK);
        VennDrawGraphics2D.draw(vennFigure, graphics, new Dimension(800, 800));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "test6.png"));
    }

    @Test
    public void testDraw7() throws Exception {
        BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 800, 800);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        final double distance = 15;
        final double size = 100;
        final double size2 = size * 1.0;

        VennFigure<String> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(0, 0), -Math.PI / 4, size, size / 2, VennFigure.DEFUALT_COLOR, "C"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(0, 0), Math.PI / 4, size, size / 2, VennFigure.DEFUALT_COLOR, "B"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(distance * 1.5, distance), -Math.PI / 4, size2, size2 / 2, VennFigure.DEFUALT_COLOR, "D"));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(-distance * 1.5, distance), Math.PI / 4, size2, size2 / 2, VennFigure.DEFUALT_COLOR, "A"));

        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(-distance * 3, -distance / 2), "A"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(-distance * 1.5, -distance * 2), "B"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(distance * 1.5, -distance * 2), "C"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(distance * 3, -distance / 2), "D"));

        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(-distance * 2, -distance), "AB"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(0, -distance), "BC"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(distance * 2, -distance), "CD"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(0, distance * 2.7), "AD"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(-distance * 1.9, distance * 1.2), "AC"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(distance * 1.9, distance * 1.2), "BD"));

        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(-distance, 0), "ABC"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(distance, 0), "BCD"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(-distance * 0.6, distance * 2), "ACD"));
        vennFigure.addShape(new VennFigure.Text<>(new VennFigure.Point(distance * 0.6, distance * 2), "ABD"));

        vennFigure.addShape(new VennFigure.Text<String>(new VennFigure.Point(0, distance), "ABCD"));

        graphics.setColor(Color.BLACK);
        VennDrawGraphics2D.draw(vennFigure, graphics, new Dimension(800, 800));

        graphics.scale(1, 1);

        final int OFFSET = 1;

        for (int i = -130; i <= 130; i += 8) {
            for (int j = -100; j <= 120; j += 8) {
                List<VennFigure.Oval<String>> ovalList = vennFigure.ovalsAtPoint(new VennFigure.Point(i, j));
                Set<String> foundIds = new HashSet<>();
                for (VennFigure.Oval<String> one : ovalList) foundIds.add(one.getUserData());

                if (foundIds.contains("A")) {
                    graphics.setColor(Color.BLUE);
                    graphics.fillRoundRect(i - OFFSET, j - OFFSET, OFFSET, OFFSET, 0, 0);
                }
                if (foundIds.contains("B")) {
                    graphics.setColor(Color.YELLOW);
                    graphics.fillRoundRect(i, j - OFFSET, OFFSET, OFFSET, 0, 0);
                }
                if (foundIds.contains("C")) {
                    graphics.setColor(Color.RED);
                    graphics.fillRoundRect(i - OFFSET, j, OFFSET, OFFSET, 0, 0);
                }
                if (foundIds.contains("D")) {
                    graphics.setColor(Color.GREEN);
                    graphics.fillRoundRect(i, j, OFFSET, OFFSET, 0, 0);
                }

                //graphics.setColor(Color.BLACK);
                //graphics.drawRect(i-OFFSET, j-OFFSET, OFFSET*2, OFFSET*2);

                //log.info("pos {},{} {}", i, j, foundIds);
            }
        }


        ImageIO.write(image, "PNG", new File(DIST_DIR, "test7.png"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testException() throws Exception {
        VennFigure<String> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Shape<String>() {
            @Override
            public VennFigure.Point getCenter() {
                return new VennFigure.Point(0, 0);
            }

            @Override
            public String getUserData() {
                return null;
            }
        });


        BufferedImage image = new BufferedImage(800, 800, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        VennDrawGraphics2D.draw(vennFigure, graphics, new Dimension(800, 800));
    }
}