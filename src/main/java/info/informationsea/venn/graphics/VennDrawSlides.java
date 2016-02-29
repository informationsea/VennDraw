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
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.poi.xslf.usermodel.*;
import org.openxmlformats.schemas.presentationml.x2006.main.impl.CTShapeImpl;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

@Value @AllArgsConstructor
public class VennDrawSlides<T> {
    private VennFigure<T> vennFigure;
    private XSLFSlide slide;
    private int marginTop = 120;
    private int marginBottom = 20;
    private int marginLeft = 20;
    private int marginRight = 20;
    private double fontSize = 12;

    public void draw() {
        Dimension slideSize = slide.getSlideShow().getPageSize();
        Dimension availableSize = new Dimension(slideSize.width - marginLeft - marginRight,
                slideSize.height - marginBottom - marginTop);

        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) image.getGraphics();
        Font font = new Font("SansSerif", Font.PLAIN, (int)fontSize);

        // Calculate draw area
        Rectangle2D drawRect = vennFigure.drawRect(str -> font.getStringBounds(str, g.getFontRenderContext()));

        // resize graphics
        double resizeFactorX = drawRect.getWidth() / availableSize.getWidth();
        double resizeFactorY = drawRect.getHeight() / availableSize.getHeight();
        double resizeFactor = 1/Math.max(resizeFactorX, resizeFactorY);

        /*
        {
            XSLFAutoShape autoShape = slide.createAutoShape();
            autoShape.setShapeType(XSLFShapeType.RECT);
            autoShape.setAnchor(new Rectangle(new Point(marginLeft, marginTop), availableSize));
            autoShape.setLineColor(Color.BLACK);
            autoShape.setLineWidth(1);
        }
        */

        // fill first
        for (VennFigure.Shape<T> shape : vennFigure.getShapes()) {

            if (shape instanceof VennFigure.Oval) {
                VennFigure.Oval<T> oval = (VennFigure.Oval<T>) shape;
                Color fillColor = VennDrawGraphics2D.decodeColor(oval.getColor());

                if (fillColor.getAlpha() != 0) {
                    XSLFAutoShape autoShape = slide.createAutoShape();
                    autoShape.setShapeType(XSLFShapeType.ELLIPSE);

                    autoShape.setFillColor(fillColor);
                    if (fillColor.getAlpha() != 255) {
                        CTShapeImpl obj = (CTShapeImpl) autoShape.getXmlObject();
                        obj.getSpPr().getSolidFill().getSrgbClr().addNewAlpha().setVal(100000 * fillColor.getAlpha() / 255);
                    }

                    autoShape.setAnchor(new Rectangle(
                            (int) ((oval.getCenter().getX() - oval.getWidth() / 2 - drawRect.getMinX()) * resizeFactor + marginLeft),
                            (int) ((oval.getCenter().getY() - oval.getHeight() / 2 - drawRect.getMinY()) * resizeFactor + marginTop),
                            (int) (oval.getWidth() * resizeFactor),
                            (int) (oval.getHeight() * resizeFactor)));
                    autoShape.setRotation(oval.getTheta() / Math.PI * 180);
                }
            }
        }

        // stroke next
        for (VennFigure.Shape<T> shape : vennFigure.getShapes()) {
            if (shape instanceof VennFigure.Oval) {
                VennFigure.Oval<T> oval = (VennFigure.Oval<T>) shape;
                XSLFAutoShape autoShape = slide.createAutoShape();
                autoShape.setShapeType(XSLFShapeType.ELLIPSE);
                autoShape.setLineColor(Color.BLACK);
                autoShape.setLineWidth(1);

                autoShape.setAnchor(new Rectangle(
                        (int)((oval.getCenter().getX() - oval.getWidth()/2 - drawRect.getMinX())*resizeFactor + marginLeft),
                        (int)((oval.getCenter().getY() - oval.getHeight()/2 - drawRect.getMinY())*resizeFactor + marginTop),
                        (int)(oval.getWidth()*resizeFactor),
                        (int)(oval.getHeight()*resizeFactor)));
                autoShape.setRotation(oval.getTheta()/Math.PI*180);
            } else if (shape instanceof VennFigure.Text) {
                VennFigure.Text<T> text = (VennFigure.Text<T>) shape;
                XSLFTextBox textBox = slide.createTextBox();

                XSLFTextParagraph paragraph = textBox.addNewTextParagraph();

                XSLFTextRun textRun = paragraph.addNewTextRun();
                textRun.setText(text.getText());
                textRun.setFontSize(fontSize*resizeFactor);

                final double RESIZE_FACTOR = 1.1;
                final double TEXT_OFFSET = 10;

                Rectangle2D textSizeActual = font.getStringBounds(text.getText(), g.getFontRenderContext());

                textBox.setAnchor(new Rectangle2D.Double(0, 0, textSizeActual.getWidth()*RESIZE_FACTOR*resizeFactor + TEXT_OFFSET, 100*resizeFactor));
                Rectangle2D textSize = textBox.resizeToFitText();

                switch (text.getJust()) {
                    case CENTER:
                    default:
                        paragraph.setTextAlign(TextAlign.CENTER);
                        textBox.setAnchor(new Rectangle(
                                (int)((text.getCenter().getX() - drawRect.getMinX())*resizeFactor - textSize.getWidth()/2 + marginLeft),
                                (int)((text.getCenter().getY() - drawRect.getMinY())*resizeFactor - textSize.getHeight()/2 + marginTop),
                                (int)(textSize.getWidth()),
                                (int)(textSize.getHeight())));
                        break;
                    case LEFT:
                        paragraph.setTextAlign(TextAlign.LEFT);
                        textBox.setAnchor(new Rectangle(
                                (int)((text.getCenter().getX() - drawRect.getMinX())*resizeFactor + marginLeft),
                                (int)((text.getCenter().getY() - drawRect.getMinY())*resizeFactor - textSize.getHeight()/2 + marginTop),
                                (int)(textSize.getWidth()),
                                (int)(textSize.getHeight())));
                        break;
                    case RIGHT:
                        paragraph.setTextAlign(TextAlign.RIGHT);
                        textBox.setAnchor(new Rectangle(
                                (int)((text.getCenter().getX() - drawRect.getMinX())*resizeFactor - textSize.getWidth() + marginLeft),
                                (int)((text.getCenter().getY() - drawRect.getMinY())*resizeFactor - textSize.getHeight()/2 + marginTop),
                                (int)(textSize.getWidth()),
                                (int)(textSize.getHeight())));
                        break;
                }
            }
        }
    }
}
