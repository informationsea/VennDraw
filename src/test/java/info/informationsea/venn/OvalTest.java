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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class OvalTest {

    @Test
    public void testRotate() throws Exception {
        VennFigure.Oval oval1 = new VennFigure.Oval(new VennFigure.Point(1, 1), 0, 1, 2);
        VennFigure.Oval rotated = oval1.rotate(Math.PI / 4);
        Assert.assertEquals(0, rotated.getCenter().getX(), 1e-15);
        Assert.assertEquals(Math.sqrt(2), rotated.getCenter().getY(), 1e-15);
        Assert.assertEquals(0, rotated.getTheta(), 1e-15);
    }

    @Test
    public void testRotateOval() throws Exception {
        VennFigure.Oval oval1 = new VennFigure.Oval(new VennFigure.Point(1, 1), 0, 1, 2);
        VennFigure.Oval rotated = oval1.rotateOval(Math.PI / 4);
        Assert.assertEquals(1, rotated.getCenter().getX(), 1e-15);
        Assert.assertEquals(1, rotated.getCenter().getY(), 1e-15);
        Assert.assertEquals(Math.PI / 4, rotated.getTheta(), 1e-15);
    }

    @Test
    public void testTranslate() throws Exception {
        VennFigure.Oval oval1 = new VennFigure.Oval(new VennFigure.Point(2, 1), 0, 1, 2);
        VennFigure.Oval translated = oval1.translate(2, 3);
        Assert.assertEquals(new VennFigure.Point(4, 4), translated.getCenter());
    }

    @Test
    public void testScale() throws Exception {
        VennFigure.Oval oval1 = new VennFigure.Oval(new VennFigure.Point(1, 1), 0, 1, 2);
        VennFigure.Oval scaled = oval1.scale(2, 3);
        Assert.assertEquals(new VennFigure.Point(2, 3), scaled.getCenter());
        Assert.assertEquals(0, scaled.getTheta(), 1e-20);
        Assert.assertEquals(1, scaled.getWidth(), 1e-20);
        Assert.assertEquals(2, scaled.getHeight(), 1e-20);
    }

    @Test
    public void testContains() throws Exception {
        {
            VennFigure.Oval oval1 = new VennFigure.Oval(new VennFigure.Point(0, 0), 0, 2, 2);
            Assert.assertFalse(oval1.contains(new VennFigure.Point(1, 1)));
            Assert.assertFalse(oval1.contains(new VennFigure.Point(1 / Math.sqrt(2), 1 / Math.sqrt(2) + 0.01)));
            Assert.assertTrue(oval1.contains(new VennFigure.Point(0.5, 0.5)));
            Assert.assertTrue(oval1.contains(new VennFigure.Point(0, 0)));
        }

        {
            VennFigure.Oval oval = new VennFigure.Oval(new VennFigure.Point(0, 0), 0, 4, 2);
            Assert.assertFalse(oval.contains(new VennFigure.Point(1, 1)));
            Assert.assertFalse(oval.contains(new VennFigure.Point(1 / Math.sqrt(2) * 2, 1 / Math.sqrt(2) + 0.01)));
            Assert.assertTrue(oval.contains(new VennFigure.Point(0.5, 0.5)));
            Assert.assertTrue(oval.contains(new VennFigure.Point(0, 0)));
            Assert.assertTrue(oval.contains(new VennFigure.Point(1.4, 0)));
            Assert.assertFalse(oval.contains(new VennFigure.Point(0, 1.4)));
        }

        {
            VennFigure.Oval oval = new VennFigure.Oval(new VennFigure.Point(0, 0), Math.PI / 2, 4, 2);
            Assert.assertFalse(oval.contains(new VennFigure.Point(1, 1)));
            Assert.assertFalse(oval.contains(new VennFigure.Point(1 / Math.sqrt(2) + 0.01, 1 / Math.sqrt(2) * 2)));
            Assert.assertTrue(oval.contains(new VennFigure.Point(0.5, 0.5)));
            Assert.assertTrue(oval.contains(new VennFigure.Point(0, 0)));
            Assert.assertFalse(oval.contains(new VennFigure.Point(1.4, 0)));
            Assert.assertTrue(oval.contains(new VennFigure.Point(0, 1.4)));
        }

        {
            VennFigure.Oval oval = new VennFigure.Oval(new VennFigure.Point(0, 0), Math.PI / 4, 4, 2);

            Assert.assertTrue(oval.contains(new VennFigure.Point(1, 1)));
            Assert.assertTrue(oval.contains(new VennFigure.Point(Math.sqrt(2), Math.sqrt(2) - 0.01)));
            Assert.assertFalse(oval.contains(new VennFigure.Point(Math.sqrt(2), Math.sqrt(2) + 0.01)));
            Assert.assertFalse(oval.contains(new VennFigure.Point(0, 2.0 * Math.sqrt(2) / Math.sqrt(5) + 0.01)));
            Assert.assertTrue(oval.contains(new VennFigure.Point(0, 2.0 * Math.sqrt(2) / Math.sqrt(5) - 0.01)));
        }
    }

    @Test
    public void testUserData() {
        VennFigure.Oval<String> oval = new VennFigure.Oval<>(new VennFigure.Point(0, 0), 0, 10, 10, "#ffffff00", "Test");
        Assert.assertEquals("Test", oval.getUserData());
    }

    @Test
    public void testToPolygon() {
        VennFigure.Oval<String> oval = new VennFigure.Oval<>(new VennFigure.Point(0, 0), Math.PI / 4, 4, 2);
        List<VennFigure.Point> polygon = oval.toPolygon();
        polygon.forEach(it -> {
            Assert.assertEquals(1, oval.normalizedDistanceFromCenter(it), 1e-10);
        });
    }
}