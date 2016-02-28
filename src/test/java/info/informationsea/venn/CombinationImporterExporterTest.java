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

import info.informationsea.tableio.TableRecord;
import info.informationsea.tableio.csv.TableCSVReader;
import info.informationsea.tableio.csv.TableCSVWriter;
import info.informationsea.tableio.excel.ExcelSheetReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.*;

@Slf4j
public class CombinationImporterExporterTest {


    public static final File DIST_DIR = new File(new File("build"), "test-io");
    private CombinationSolver<String, String> combinationSolver;
    private CombinationSolver<String, String> four;

    @BeforeClass
    public static void setupClass() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        DIST_DIR.mkdirs();
    }

    @Before
    public void setup() throws Exception {
        combinationSolver = new CombinationSolver<>(SampleCombinationGenerator.twoCombinationValues);
        four = new CombinationSolver<>(SampleCombinationGenerator.fourCombinationValues);
    }


    @Test
    public void testExport() throws Exception {
        try (Writer writer = new FileWriter(new File(DIST_DIR, "export.csv"));
             TableCSVWriter csvWriter = new TableCSVWriter(writer)) {

            CombinationImporterExporter.export(csvWriter, combinationSolver, new VennFigureParameters<String>(combinationSolver).getKeys());
        }

        try (Reader reader = new FileReader(new File(DIST_DIR, "export.csv"));
             TableCSVReader csvReader = new TableCSVReader(reader)) {
            csvReader.setUseHeader(true);

            CombinationSolver<String, String> solver = CombinationImporterExporter.importCombination(csvReader);
            Assert.assertEquals(combinationSolver.getValues(), solver.getValues());
        }
    }

    public void testExcel(String filename) throws Exception {
        CombinationImporterExporter.export(new File(DIST_DIR, filename), combinationSolver, new VennFigureParameters<String>(combinationSolver).getKeys());

        Workbook workbook = WorkbookFactory.create(new File(DIST_DIR, filename));
        Sheet sheet = workbook.getSheetAt(0);

        ExcelSheetReader reader = new ExcelSheetReader(sheet);
        reader.setUseHeader(true);

        CombinationSolver<String, String> solver = CombinationImporterExporter.importCombination(reader);
        Assert.assertEquals(combinationSolver.getValues(), solver.getValues());

        sheet = workbook.getSheetAt(1);
        reader = new ExcelSheetReader(sheet);
        reader.setUseHeader(true);

        Map<Set<String>, Set<String>> combinations = new HashMap<>();
        for (TableRecord record : reader) {
            String[] key = record.get(0).toString().split(", ");
            Set<String> values = new HashSet<>();
            for (int i = 2; i < record.size(); i++) {
                values.add(record.get(i).toString());
            }

            Assert.assertEquals(values.size(), Integer.parseInt(record.get(1).toString()));

            combinations.put(asSet(key), values);
        }

        Assert.assertEquals(combinations, combinationSolver.getCombinationResult());
        Assert.assertEquals(solver.getCombinationResult(), combinationSolver.getCombinationResult());
    }

    @Test
    public void testExport2() throws Exception {
        testExcel("export.xls");
        testExcel("export.xlsx");
    }

    @Test
    public void testExport3() throws Exception {
        CombinationImporterExporter.export(new File(DIST_DIR, "combination4.xlsx"), four, new VennFigureParameters<String>(four).getKeys());
    }

    @Test
    public void testImport() throws Exception {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("importTest.csv"));
             TableCSVReader csvReader = new TableCSVReader(reader)) {
            csvReader.setUseHeader(true);

            CombinationSolver<String, String> solver =
                    CombinationImporterExporter.importCombination(csvReader);

            Assert.assertEquals(asSet("1", "3", "4"), solver.getValues().get("A"));
            Assert.assertEquals(asSet("2", "3", "4"), solver.getValues().get("B"));
            Assert.assertEquals(asSet("4"), solver.getValues().get("C"));

        }
    }

    @Test(expected = RuntimeException.class)
    public void testImport2() throws Exception {
        try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("importTest2.csv"));
             TableCSVReader csvReader = new TableCSVReader(reader)) {
            csvReader.setUseHeader(true);

            CombinationSolver<String, String> solver =
                    CombinationImporterExporter.importCombination(csvReader);
        }
    }


    @SafeVarargs
    public static <T> Set<T> asSet(T... values) {
        Set<T> s = new HashSet<>(values.length);
        Collections.addAll(s, values);
        return s;
    }
}