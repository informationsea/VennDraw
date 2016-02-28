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
import org.junit.Assert;
import org.junit.Test;

public class JoinedConverterTest {

    @Test
    public void testConvert() throws Exception {
        PointConverter.JoinedConverter converter = new PointConverter.JoinedConverter();
        converter.addFirst(new PointConverter.Rotate(Math.PI / 2));
        VennFigure.Point point = new VennFigure.Point(1, 1);

        VennFigure.Point converted = converter.convert(point);
        Assert.assertEquals(-1.0, converted.getX(), 1e-15);
        Assert.assertEquals(1.0, converted.getY(), 1e-15);

        converter.addFirst(new PointConverter.Translate(1, 1));
        converted = converter.convert(point);
        Assert.assertEquals(-2.0, converted.getX(), 1e-15);
        Assert.assertEquals(2.0, converted.getY(), 1e-15);

        converter.addLast(new PointConverter.Scale(2, 4));
        converted = converter.convert(point);
        Assert.assertEquals(-4.0, converted.getX(), 1e-15);
        Assert.assertEquals(8.0, converted.getY(), 1e-15);
    }
}