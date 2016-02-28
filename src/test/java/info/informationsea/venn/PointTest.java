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

public class PointTest {
    @Test
    public void testRotate() throws Exception {
        VennFigure.Point point = new VennFigure.Point(1, 0);

        VennFigure.Point point90 = point.rotate(Math.PI / 2);
        Assert.assertEquals(1.0, point90.getY(), 1.0E-10);
        Assert.assertEquals(0.0, point90.getX(), 1.0E-10);

        VennFigure.Point point30 = point.rotate(Math.PI / 6);
        Assert.assertEquals(0.5, point30.getY(), 1.0E-10);
        Assert.assertEquals(Math.sqrt(3) / 2, point30.getX(), 1.0E-10);

        VennFigure.Point point45 = point.rotate(Math.PI / 4);
        Assert.assertEquals(1 / Math.sqrt(2), point45.getY(), 1.0E-10);
        Assert.assertEquals(1 / Math.sqrt(2), point45.getX(), 1.0E-10);

        VennFigure.Point point60 = point.rotate(Math.PI / 3);
        Assert.assertEquals(Math.sqrt(3) / 2, point60.getY(), 1.0E-10);
        Assert.assertEquals(0.5, point60.getX(), 1.0E-10);
    }

    @Test
    public void testRotate2() throws Exception {
        VennFigure.Point point = new VennFigure.Point(0, 1);

        VennFigure.Point point90 = point.rotate(Math.PI / 2);
        Assert.assertEquals(-1.0, point90.getX(), 1.0E-10);
        Assert.assertEquals(0.0, point90.getY(), 1.0E-10);

        VennFigure.Point point30 = point.rotate(Math.PI / 6);
        Assert.assertEquals(-0.5, point30.getX(), 1.0E-10);
        Assert.assertEquals(Math.sqrt(3) / 2, point30.getY(), 1.0E-10);

        VennFigure.Point point45 = point.rotate(Math.PI / 4);
        Assert.assertEquals(-1 / Math.sqrt(2), point45.getX(), 1.0E-10);
        Assert.assertEquals(1 / Math.sqrt(2), point45.getY(), 1.0E-10);

        VennFigure.Point point60 = point.rotate(Math.PI / 3);
        Assert.assertEquals(-Math.sqrt(3) / 2, point60.getX(), 1.0E-10);
        Assert.assertEquals(0.5, point60.getY(), 1.0E-10);
    }

    @Test
    public void testTranslate() throws Exception {
        VennFigure.Point point = new VennFigure.Point(1, 0);

        Assert.assertEquals(new VennFigure.Point(2, 2), point.translate(1, 2));
        Assert.assertEquals(new VennFigure.Point(-1, -4), point.translate(-2, -4));
    }

    @Test
    public void testScale() throws Exception {
        VennFigure.Point point = new VennFigure.Point(1, 0);

        Assert.assertEquals(new VennFigure.Point(2, 2), point.translate(1, 2));
        Assert.assertEquals(new VennFigure.Point(-1, -4), point.translate(-2, -4));
    }
}