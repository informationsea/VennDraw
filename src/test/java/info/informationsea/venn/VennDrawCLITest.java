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

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VennDrawCLITest {

    public static final File DIST_DIR = new File(new File("build"), "test-cli");

    @Before
    public void setUp() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        DIST_DIR.mkdirs();
    }

    @Test
    public void test1() throws Exception {
        String outputs = Arrays.asList("xls", "xlsx", "csv", "png", "svg", "pdf", "pptx").stream().
                map(it -> new File(DIST_DIR, "test1."+it).toString()).collect(Collectors.joining(","));

        VennDrawCLI.main("-1", "a,b,c", "-2", "b,c,d,e,f", "-o", outputs);
    }

    @Test
    public void test2() throws Exception {
        String outputs = Arrays.asList("xls", "xlsx", "csv", "png", "svg", "pdf", "pptx").stream().
                map(it -> new File(DIST_DIR, "test2."+it).toString()).collect(Collectors.joining(","));

        VennDrawCLI.main("-1", "a,b,c", "-2", "b,c,d,e,f", "-3", "1,2,3,4,b,c", "-o", outputs);
    }
}