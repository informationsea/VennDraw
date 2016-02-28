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

import info.informationsea.tableio.ImageSheetWriter;
import info.informationsea.tableio.TableReader;
import info.informationsea.tableio.TableRecord;
import info.informationsea.tableio.TableWriter;
import info.informationsea.tableio.csv.TableCSVReader;
import info.informationsea.tableio.csv.TableCSVWriter;
import info.informationsea.tableio.excel.ExcelImageSheetWriter;
import info.informationsea.tableio.excel.ExcelSheetReader;
import info.informationsea.tableio.excel.ExcelSheetWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.*;
import java.util.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CombinationImporterExporter {
    public static <T, U> void export(TableWriter writer, CombinationSolver<T, U> combinationSolver,
                                     List<VennFigureParameters.Attribute<T>> keyList) throws Exception {

        // create item set
        Set<U> itemSet = new HashSet<>();
        for (Map.Entry<T, Set<U>> one : combinationSolver.getValues().entrySet())
            itemSet.addAll(one.getValue());

        // create key set
        List<String> header = new ArrayList<>();
        header.add("");
        for (VennFigureParameters.Attribute<T> one : keyList) header.add(one.getKey().toString());

        writer.printRecord(header.toArray());

        for (U one : itemSet) {
            List<Object> row = new ArrayList<>();
            row.add(one.toString());

            for (VennFigureParameters.Attribute<T> oneKey : keyList) {
                row.add(combinationSolver.getValues().get(oneKey.getKey()).contains(one));
            }

            writer.printRecord(row.toArray());
        }
    }


    public static <T, U> void export(File file, CombinationSolver<T, U> combinationSolver,
                                     List<VennFigureParameters.Attribute<T>> keyList) throws Exception {
        if (file.getName().endsWith(".csv")) {
            try (FileWriter writer = new FileWriter(file);
                 TableCSVWriter csvWriter = new TableCSVWriter(writer)) {
                export(csvWriter, combinationSolver, keyList);
            }
        } else if (file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls")) {
            Workbook workbook;
            if (file.getName().endsWith(".xlsx"))
                workbook = new SXSSFWorkbook();
            else
                workbook = new HSSFWorkbook();

            // Export dataset
            Sheet sheet = workbook.createSheet("Data");
            try (ExcelSheetWriter writer = new ExcelSheetWriter(sheet)) {
                writer.setPrettyTable(true);
                export(writer, combinationSolver, keyList);
            }

            // Export combinations
            sheet = workbook.createSheet("Combinations");
            try (ExcelSheetWriter writer = new ExcelSheetWriter(sheet)) {
                writer.setPrettyTable(false);
                writer.setEnableHeaderStyle(true);
                writer.setAlternativeBackground(true);

                writer.printRecord("Combination", "# of items", "items");

                for (Map.Entry<Set<T>, Set<U>> entry : combinationSolver.getCombinationResult().entrySet()) {
                    String keys = "";
                    for (T one : entry.getKey()) {
                        if (keys.length() > 0) keys += ", ";
                        keys += one.toString();
                    }

                    List<Object> row = new ArrayList<>();
                    row.add(keys);
                    row.add(entry.getValue().size());

                    for (U one : entry.getValue()) {
                        row.add(one.toString());
                    }

                    writer.printRecord(row.toArray());
                }
            }

            // Export venn diagram
            switch (combinationSolver.getValues().size()) {
                case 2:
                case 3:
                case 4:
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    VennExporter.exportAsPNG(new VennFigureParameters<T>(combinationSolver), outputStream, 800, 10);
                    outputStream.close();

                    sheet = workbook.createSheet("Venn");
                    try (ExcelImageSheetWriter imageSheetWriter = new ExcelImageSheetWriter(sheet)) {
                        imageSheetWriter.addImage(ImageSheetWriter.ImageType.TYPE_PNG, outputStream.toByteArray());
                    }
                    break;
                default: // Do nothing
                    break;
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }

        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    public static CombinationSolver<String, String> importCombination(TableReader reader) throws Exception {
        String[] header = reader.getHeader();
        Map<String, Set<String>> value = new LinkedHashMap<>();
        for (int i = 1; i < header.length; i++) value.put(header[i], new HashSet<String>());

        for (TableRecord record : reader) {
            if (record.size() != header.length) {
                throw new RuntimeException("Invalid format file");
            }

            String item = record.get(0).toString();
            for (int i = 1; i < header.length; i++) {
                String v = record.get(header[i]).toString().toLowerCase();
                if (!v.equals("false") && !v.equals("")) {
                    value.get(header[i]).add(item);
                }
            }
        }

        return new CombinationSolver<>(value);
    }

    public static CombinationSolver<String, String> importCombination(File file) throws Exception {
        if (file.getName().endsWith(".csv")) {
            try (Reader reader = new FileReader(file);
                 TableCSVReader csvReader = new TableCSVReader(reader)) {
                csvReader.setUseHeader(true);
                return importCombination(csvReader);
            }
        } else if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            try (ExcelSheetReader sheetReader = new ExcelSheetReader(sheet)) {
                sheetReader.setUseHeader(true);
                return importCombination(sheetReader);
            }
        } else {
            throw new IllegalArgumentException("Unsupported file");
        }
    }
}
