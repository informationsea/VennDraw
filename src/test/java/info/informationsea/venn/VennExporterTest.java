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

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VennExporterTest {


    private VennFigureParameters<String> four;
    private VennFigureParameters<String> two;

    public static final File DIST_DIR = new File(new File("build"), "test-images3");

    @Before
    public void setUp() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        DIST_DIR.mkdirs();

        two = new VennFigureParameters<String>(new CombinationSolver<>(SampleCombinationGenerator.twoCombinationValues));
        four = new VennFigureParameters<String>(new CombinationSolver<>(SampleCombinationGenerator.fourCombinationValues));
    }

    @Test
    public void testExportAsPNG() throws Exception {
        VennExporter.exportAsPNG(two, new File(DIST_DIR, "test2.png"), 500, 10);
        VennExporter.exportAsPNG(four, new File(DIST_DIR, "test4.png"), 500, 10);
    }

    @Test
    public void testExportAsSVG() throws Exception {
        VennExporter.exportAsSVG(four, new File(DIST_DIR, "test4.svg"), new Dimension(500, 500));
    }

    @Test
    public void testExportAsPDF() throws Exception {
        VennExporter.exportAsPDF(four, new File(DIST_DIR, "test4.pdf"));
        VennExporter.exportAsPDF(two, new File(DIST_DIR, "test2.pdf"));
    }

    @Test
    public void testExportAsPowerPoint() throws Exception {
        VennExporter.exportAsPowerPoint(two, new File(DIST_DIR, "test2.pptx"));
        VennExporter.exportAsPowerPoint(four, new File(DIST_DIR, "test4.pptx"));
    }


    @SafeVarargs
    public final <T> Set<T> asSet(T... values) {
        Set<T> s = new HashSet<>(values.length);
        Collections.addAll(s, values);
        return s;
    }
}