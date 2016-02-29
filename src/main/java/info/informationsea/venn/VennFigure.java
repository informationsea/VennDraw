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

import info.informationsea.venn.graphics.PointConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VennFigure<T> {

    public static final String DEFUALT_COLOR = "#ffffff00";

    public static final String[] DEFAULT_COLOR_LIST = {
            "#bce2e8a0",
            "#fef263a0",
            "#d8e698a0",
            "#fddea5a0",
            "#f6bfbca0",
    };


    @Getter
    private List<Shape<T>> shapes;

    public VennFigure() {
        shapes = new ArrayList<>();
    }

    public void addShape(Shape<T> shape) {
        shapes.add(shape);
    }

    public List<Oval<T>> ovalsAtPoint(Point point) {
        List<Oval<T>> ovalList = new ArrayList<>();

        for (Shape<T> one : shapes) {
            if (!(one instanceof Oval)) continue;
            if (((Oval) one).contains(point))
                ovalList.add((Oval<T>) one);
        }

        return ovalList;
    }

    public Rectangle2D drawRect(StringBounds stringBounds) {
        double maximumX = 0, minimumX = 0;
        double maximumY = 0, minimumY = 0;
        for (VennFigure.Shape<T> oneShape : getShapes()) {
            Rectangle2D rect;
            if (oneShape instanceof VennFigure.Oval) {
                rect = ((VennFigure.Oval) oneShape).range();

            } else if (oneShape instanceof VennFigure.Text) {
                VennFigure.Text oneText = (VennFigure.Text) oneShape;
                Rectangle2D textRect = stringBounds.getStringBounds(oneText.getText());
                switch (oneText.getJust()) {
                    case CENTER:
                    default:
                        rect = new Rectangle2D.Double(
                                oneText.getCenter().getX() - textRect.getWidth() / 2,
                                oneText.getCenter().getY() - textRect.getHeight() / 2,
                                textRect.getWidth(), textRect.getHeight());
                        break;
                    case LEFT:
                        rect = new Rectangle2D.Double(
                                oneText.getCenter().getX(),
                                oneText.getCenter().getY() - textRect.getHeight() / 2,
                                textRect.getWidth(), textRect.getHeight());
                        break;
                    case RIGHT:
                        rect = new Rectangle2D.Double(
                                oneText.getCenter().getX() - textRect.getWidth(),
                                oneText.getCenter().getY() - textRect.getHeight() / 2,
                                textRect.getWidth(), textRect.getHeight());
                        break;
                }
            } else {
                throw new UnsupportedOperationException();
            }

            maximumX = Math.max(maximumX, rect.getMaxX());
            minimumX = Math.min(minimumX, rect.getMinX());
            maximumY = Math.max(maximumY, rect.getMaxY());
            minimumY = Math.min(minimumY, rect.getMinY());
        }

        return new Rectangle2D.Double(minimumX, minimumY, maximumX - minimumX, maximumY - minimumY);
    }

    public interface StringBounds {
        Rectangle2D getStringBounds(String str);
    }

    public interface Shape<T> {
        Point getCenter();

        T getUserData();
    }

    @Data
    @AllArgsConstructor
    public static class Oval<T> implements Shape<T> {
        private Point center;
        private double theta;
        private double width;
        private double height;
        private String color;
        private T userData;

        private static final int POLYGON_STEP = 500;

        public Oval(Point center, double theta, double width, double height) {
            this.center = center;
            this.theta = theta;
            this.width = width;
            this.height = height;
            this.color = DEFUALT_COLOR;
        }

        public Oval(Point center, double theta, double width, double height, String color) {
            this.center = center;
            this.theta = theta;
            this.width = width;
            this.height = height;

            if (color.startsWith("#") && (color.length() == 7 || color.length() == 9))
                this.color = color;
            else
                throw new IllegalArgumentException("Invalid color");
        }

        public Rectangle2D.Double range() {
            double maximum = Math.max(width, height);
            return new Rectangle2D.Double(center.getX() - maximum / 2, center.getY() - maximum / 2,
                    maximum, maximum);
        }

        public Oval<T> rotate(double theta) {
            return new Oval<>(center.rotate(theta), this.theta, width, height, color, userData);
        }

        public Oval<T> rotateOval(double theta) {
            return new Oval<>(center, this.theta + theta, width, height, color, userData);
        }

        public Oval<T> translate(double x, double y) {
            return new Oval<>(center.translate(x, y), theta, width, height, color, userData);
        }

        public Oval<T> scale(double sx, double sy) {
            return new Oval<>(center.scale(sx, sy), theta, width, height, color, userData);
        }

        /**
         * Calculate normalized distance from center point. This function does not return actual distance from
         * center point, but returns normalized distance. If an argument point is
         *
         * @param point point
         * @return normalized distance from center
         */
        public double normalizedDistanceFromCenter(Point point) {
            Point uniformedPoint = point.translate(-center.getX(), -center.getY()).rotate(-theta).scale(2 / width, 2 / height);
            return Math.sqrt(Math.pow(uniformedPoint.getX(), 2) + Math.pow(uniformedPoint.getY(), 2));
        }

        public boolean contains(Point point) {
            return normalizedDistanceFromCenter(point) <= 1;
        }

        public List<Point> toPolygon() {
            List<Point> polygon = new ArrayList<>();
            PointConverter.JoinedConverter converter = new PointConverter.JoinedConverter();
            converter.addLast(new PointConverter.Scale(width/2, height/2));
            converter.addLast(new PointConverter.Rotate(theta));
            converter.addLast(new PointConverter.Translate(center.getX(), center.getY()));

            for (int i = 0; i < POLYGON_STEP; i++) {
                polygon.add(converter.convert(new Point(
                        Math.cos(Math.PI*2/POLYGON_STEP*i),
                        Math.sin(Math.PI*2/POLYGON_STEP*i))));
            }

            return polygon;
        }
    }

    public enum TextJust {
        LEFT, CENTER, RIGHT
    }

    @Data
    @AllArgsConstructor
    public static class Text<T> implements Shape<T> {
        private Point center;
        private String text;
        private TextJust just = TextJust.CENTER;
        private T userData;

        public Text(Point center, String text) {
            this.center = center;
            this.text = text;
        }

        public Text(Point center, String text, TextJust just) {
            this.center = center;
            this.text = text;
            this.just = just;
        }

        public Text(double x, double y, String text) {
            this.center = new Point(x, y);
            this.text = text;
        }

        public Text(double x, double y, String text, TextJust just) {
            this.center = new Point(x, y);
            this.text = text;
            this.just = just;
        }


        public Text<T> rotate(double theta) {
            return new Text<>(center.rotate(theta), text, just, userData);
        }

        public Text<T> translate(double x, double y) {
            return new Text<>(center.translate(x, y), text, just, userData);
        }

        public Text<T> scale(double sx, double sy) {
            return new Text<>(center.scale(sx, sy), text, just, userData);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Point {
        private double x;
        private double y;

        public Point rotate(double theta) {
            return new Point(x * Math.cos(theta) - y * Math.sin(theta), x * Math.sin(theta) + y * Math.cos(theta));
        }

        public Point translate(double x, double y) {
            return new Point(this.x + x, this.y + y);
        }

        public Point scale(double sx, double sy) {
            return new Point(x * sx, y * sy);
        }
    }
}
