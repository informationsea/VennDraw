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

import info.informationsea.venn.fx.GroupViewController;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class VennDrawCLI {
    @Option(name = "--group1", aliases = {"-1"}, usage = "Items of group 1 (use , as separator)", required = true)
    String group1;

    @Option(name = "--group2", aliases = {"-2"}, usage = "Items of group 2 (use , as separator)", required = true)
    String group2;

    @Option(name = "--group3", aliases = {"-3"}, usage = "Items of group 3 (use , as separator)", required = false)
    String group3;

    @Option(name = "--group4", aliases = {"-4"}, usage = "Items of group 4 (use , as separator)", required = false)
    String group4;

    @Option(name = "--group-names", aliases = {"-n"}, usage = "Group names (use , as separator)")
    String names = "Group 1, Group 2, Group 3, Group 4";

    @Option(name = "--colors", aliases = {"-c"}, usage = "Color codes (use , as separator")
    String colors = String.join(", ", GroupViewController.DEFAULT_COLORS);

    @Option(name = "--output", aliases = {"-o"}, usage = "Output files. xlsx, xls, csv, pptx, PDF, png and svg formats are acceptable. (use , as separator)", required = true)
    String output;

    public static void main(String ... args) {
        VennDrawCLI vennDrawCLI = new VennDrawCLI();
        CmdLineParser parser = new CmdLineParser(vennDrawCLI);

        boolean showHelp = false;

        if (args.length == 0) showHelp = true;

        if (args.length == 1)
            if (args[0].equals("-h") || args[0].equals("--help") || args[0].equals("-v") || args[0].equals("--version") || args[0].equals("-?"))
                showHelp = true;

        if (!showHelp) {
            try {
                parser.parseArgument(args);
                vennDrawCLI.run();
            } catch (CmdLineException e) {
                e.printStackTrace();
                showHelp = true;
            }
        }

        if (showHelp) {
            System.out.println("VennDraw " + VersionResolver.getVersion());
            System.out.println("Git Commit: " + VersionResolver.getGitCommit());
            System.out.println("Build Date: " + VersionResolver.getBuildDate());
            System.out.println("\n");
            System.out.print("venndraw ");
            parser.printSingleLineUsage(System.out);
            System.out.println();
            System.out.println();
            parser.printUsage(System.out);
        }
    }

    public void run() {
        List<String> gNames = Stream.of(names.split(",")).map(String::trim).collect(Collectors.toList());
        List<String> gColors = Stream.of(colors.split(",")).map(String::trim).collect(Collectors.toList());
        List<String> gOutputs = Stream.of(output.split(",")).map(String::trim).collect(Collectors.toList());

        int numberOfGroups = 2;
        Set<String> g1items = Stream.of(group1.split(",")).map(String::trim).collect(Collectors.toSet());
        Set<String> g2items = Stream.of(group2.split(",")).map(String::trim).collect(Collectors.toSet());
        Set<String> g3items = Collections.emptySet();
        Set<String> g4items = Collections.emptySet();
        if (group3 != null) {
            g3items = Stream.of(group3.split(",")).map(String::trim).collect(Collectors.toSet());
            numberOfGroups = 3;
        }
        if (group4 != null) {
            if (group3 == null) throw new RuntimeException("Group 3 is missing");
            g4items = Stream.of(group4.split(",")).map(String::trim).collect(Collectors.toSet());
            numberOfGroups = 4;
        }

        if (gNames.size() < numberOfGroups)
            throw new RuntimeException("Number of group names is smaller than number of groups");
        if (gColors.size() < numberOfGroups)
            throw new RuntimeException("Number of color codes is smaller than number of groups");

        Map<String, Set<String>> value = new HashMap<>();
        value.put(gNames.get(0), g1items);
        value.put(gNames.get(1), g2items);
        if (numberOfGroups >= 3)
            value.put(gNames.get(2), g3items);
        if (numberOfGroups >= 4)
            value.put(gNames.get(3), g4items);
        List<VennFigureParameters.Attribute<String>> keyList = new ArrayList<>();
        for (int i = 0; i < numberOfGroups; i++)
            keyList.add(new VennFigureParameters.Attribute<>(gNames.get(i), gColors.get(i)));

        CombinationSolver<String, String> combinationSolver = new CombinationSolver<>(value);

        VennFigureParameters<String> paramaters = new VennFigureParameters<String>(combinationSolver, keyList);

        try {
            for (String one : gOutputs) {
                if (one.endsWith(".xlsx") || one.endsWith(".xls") || one.endsWith(".csv")) {
                    CombinationImporterExporter.export(new File(one), combinationSolver, keyList);
                } else if (one.endsWith(".pptx")) {
                    VennExporter.exportAsPowerPoint(paramaters, new File(one));
                } else if (one.endsWith(".png")) {
                    VennExporter.exportAsPNG(paramaters, new File(one), 800, 10);
                } else if (one.endsWith(".svg")) {
                    VennExporter.exportAsSVG(paramaters, new File(one), new Dimension(800, 800));
                } else if (one.endsWith(".pdf")) {
                    VennExporter.exportAsPDF(paramaters, new File(one));
                } else {
                    throw new IllegalArgumentException("Unsupported file type");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
