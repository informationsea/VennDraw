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
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

public interface PointConverter {

    VennFigure.Point convert(VennFigure.Point point);

    @Value
    class JoinedConverter implements PointConverter {

        private List<PointConverter> converterList = new ArrayList<>();

        public void addFirst(PointConverter one) {
            converterList.add(0, one);
        }

        public void addLast(PointConverter one) {
            converterList.add(converterList.size(), one);
        }

        public VennFigure.Point convert(VennFigure.Point point) {
            for (PointConverter converter : converterList) {
                point = converter.convert(point);
            }
            return point;
        }
    }

    @Value
    class Rotate implements PointConverter {
        private double theta;

        @Override
        public VennFigure.Point convert(VennFigure.Point point) {
            return point.rotate(theta);
        }
    }

    @Value
    class Translate implements PointConverter {
        private double x, y;

        @Override
        public VennFigure.Point convert(VennFigure.Point point) {
            return point.translate(x, y);
        }
    }

    @Value
    class Scale implements PointConverter {
        private double x, y;

        @Override
        public VennFigure.Point convert(VennFigure.Point point) {
            return point.scale(x, y);
        }
    }

}
