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
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

@NoArgsConstructor
@Slf4j
public class VennDrawGraphics2D {

    public static Color decodeColor(String colorCode) {
        Color c = Color.decode(colorCode.substring(0, 7));
        if (colorCode.length() == 9) {
            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), Integer.parseInt(colorCode.substring(7), 16));
        } else {
            c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0xa0);
        }
        return c;
    }

    public static <T> PointConverter draw(VennFigure<T> figure, final Graphics2D g, Dimension2D size) {
        // Calculate draw area
        Rectangle2D drawRect = figure.drawRect(str -> g.getFont().getStringBounds(str, g.getFontRenderContext()));

        // resize graphics
        double resizeFactorX = drawRect.getWidth() / size.getWidth();
        double resizeFactorY = drawRect.getHeight() / size.getHeight();
        double resizeFactor = Math.max(resizeFactorX, resizeFactorY);

        g.scale(1 / resizeFactor, 1 / resizeFactor);
        g.translate(-drawRect.getMinX(), -drawRect.getMinY());

        PointConverter.JoinedConverter converter = new PointConverter.JoinedConverter();
        converter.addLast(new PointConverter.Scale(resizeFactor, resizeFactor));
        converter.addLast(new PointConverter.Translate(drawRect.getMinX(), drawRect.getMinY()));


        //log.info("initial translate: {} {}  scale: {}", minimumX, minimumY, 1/resizeFactor);

        //g.drawRect((int)minimumX, (int)minimumY, (int)(maximumX - minimumX), (int)(maximumY - minimumY));

        // fill first
        for (VennFigure.Shape<T> oneShape : figure.getShapes()) {
            if (oneShape instanceof VennFigure.Oval) {
                VennFigure.Oval oneOval = (VennFigure.Oval) oneShape;
                Color color = decodeColor(oneOval.getColor());

                if (color.getAlpha() == 0) continue;

                g.translate(oneOval.getCenter().getX(), oneOval.getCenter().getY());
                g.rotate(oneOval.getTheta());

                Ellipse2D ellipse2D = new Ellipse2D.Double(-oneOval.getWidth() / 2, -oneOval.getHeight() / 2,
                        oneOval.getWidth(), oneOval.getHeight());

                g.setPaint(decodeColor(oneOval.getColor()));
                g.fill(ellipse2D);

                g.rotate(-oneOval.getTheta());
                g.translate(-oneOval.getCenter().getX(), -oneOval.getCenter().getY());
            }
        }

        // stroke next
        for (VennFigure.Shape<T> oneShape : figure.getShapes()) {
            if (oneShape instanceof VennFigure.Oval) {
                VennFigure.Oval oneOval = (VennFigure.Oval) oneShape;
                g.translate(oneOval.getCenter().getX(), oneOval.getCenter().getY());
                g.rotate(oneOval.getTheta());

                Ellipse2D ellipse2D = new Ellipse2D.Double(-oneOval.getWidth() / 2, -oneOval.getHeight() / 2,
                        oneOval.getWidth(), oneOval.getHeight());

                g.setPaint(Color.BLACK);
                g.draw(ellipse2D);

                g.rotate(-oneOval.getTheta());
                g.translate(-oneOval.getCenter().getX(), -oneOval.getCenter().getY());
            } else if (oneShape instanceof VennFigure.Text) {
                VennFigure.Text oneText = (VennFigure.Text) oneShape;
                Rectangle2D textRect = g.getFont().getStringBounds(oneText.getText(), g.getFontRenderContext());

                switch (oneText.getJust()) {
                    case LEFT:
                        g.drawString(oneText.getText(),
                                (float) (oneText.getCenter().getX()),
                                (float) (oneText.getCenter().getY() + textRect.getHeight() / 2));
                        break;
                    case RIGHT:
                        g.drawString(oneText.getText(),
                                (float) (oneText.getCenter().getX() - textRect.getWidth()),
                                (float) (oneText.getCenter().getY() + textRect.getHeight() / 2));
                        break;
                    case CENTER:
                    default:
                        g.drawString(oneText.getText(),
                                (float) (oneText.getCenter().getX() - textRect.getWidth() / 2),
                                (float) (oneText.getCenter().getY() + textRect.getHeight() / 2));
                        break;
                }
            } else {
                throw new UnsupportedOperationException("Not implemented");
            }
        }

        //g.drawRect((int)-minimumX, (int)-minimumY, (int)(maximumX - minimumX), (int)(maximumY - minimumY));
        return converter;
    }
}
