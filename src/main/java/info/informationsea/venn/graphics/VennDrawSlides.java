package info.informationsea.venn.graphics;

import info.informationsea.venn.VennFigure;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.poi.xslf.usermodel.*;
import org.openxmlformats.schemas.presentationml.x2006.main.impl.CTShapeImpl;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * venndraw
 * Copyright (C) 2016 OKAMURA Yasunobu
 * Created on 2016/02/25.
 */
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

        for (VennFigure.Shape<T> shape : vennFigure.getShapes()) {
            if (shape instanceof VennFigure.Oval) {
                VennFigure.Oval<T> oval = (VennFigure.Oval<T>) shape;
                XSLFAutoShape autoShape = slide.createAutoShape();
                autoShape.setShapeType(XSLFShapeType.ELLIPSE);
                autoShape.setLineColor(Color.BLACK);
                autoShape.setLineWidth(1);

                Color fillColor = VennDrawGraphics2D.decodeColor(oval.getColor());
                if (fillColor.getAlpha() != 0) {
                    autoShape.setFillColor(fillColor);
                    if (fillColor.getAlpha() != 255) {
                        // http://osdir.com/ml/user-poi.apache.org/2015-02/msg00030.html
                        CTShapeImpl obj = (CTShapeImpl)autoShape.getXmlObject();
                        obj.getSpPr().getSolidFill().getSrgbClr().addNewAlpha().setVal(100000*fillColor.getAlpha()/255);
                    }
                }

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
