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
import info.informationsea.venn.graphics.VennDrawPDF;
import info.informationsea.venn.graphics.VennDrawSlides;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VennExporter {
    public static <T> void exportAsPNG(VennFigureParameters<T> parameters, File file, double width, double margin) throws IOException {
        try (FileOutputStream os = new FileOutputStream(file)) {
            exportAsPNG(parameters, os, width, margin);
        }
    }

    public static <T, U> void exportAsPNG(VennFigureParameters<T> parameters, OutputStream outputStream, double width, double margin) throws IOException {
        BufferedImage dummy = new BufferedImage(10, 10, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D dummyGraphics = (Graphics2D) dummy.getGraphics();

        VennFigure<T> vennFigure = VennFigureCreator.createVennFigure(parameters);
        Rectangle2D drawRect = vennFigure.drawRect(str -> dummyGraphics.getFont().getStringBounds(str, dummyGraphics.getFontRenderContext()));

        Dimension size = new Dimension((int) (width - margin * 2),
                (int) (width / drawRect.getWidth() * drawRect.getHeight() - margin * 2));

        BufferedImage image = new BufferedImage((int) (size.width + margin * 2), (int) (size.height + margin * 2),
                BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        /*
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, size.width, size.height);
        */
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.BLACK);
        graphics.translate(margin, margin);

        VennDrawGraphics2D.draw(vennFigure, graphics, size);
        ImageIO.write(image, "PNG", outputStream);
    }

    public static <T, U> void exportAsSVG(VennFigureParameters<T> parameters, File file, Dimension size) throws IOException {

        // Get a DOMImplementation.
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
        VennDrawGraphics2D.draw(VennFigureCreator.createVennFigure(parameters), svgGenerator, size);
        try (Writer writer = new FileWriter(file)) {
            svgGenerator.stream(writer, true);
        }
    }

    public static <T, U> void exportAsPDF(VennFigureParameters<T> parameters, File file) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            VennDrawPDF.draw(VennFigureCreator.createVennFigure(parameters), doc);
            doc.save(file);
        }
    }

    public static <T, U> void exportAsPowerPoint(VennFigureParameters<T> parameters, File file) throws IOException {
        XMLSlideShow slideShow = new XMLSlideShow();
        XSLFSlide slide = slideShow.createSlide();
        VennDrawSlides<T> vennDrawSlides = new VennDrawSlides<>(VennFigureCreator.createVennFigure(parameters), slide);
        vennDrawSlides.draw();
        try (OutputStream os = new FileOutputStream(file)) {
            slideShow.write(os);
        }
    }

}
