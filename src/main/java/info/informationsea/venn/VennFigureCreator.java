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

import java.util.*;
import java.util.stream.Collectors;

public class VennFigureCreator<T> {

    private Map<Set<T>, Integer> combinationNumber;
    private List<T> keyList;
    private List<String> colorCodes;

    private VennFigureParameters<T> vennFigureParameters;

    private VennFigureCreator(VennFigureParameters<T> vennFigureParameters) {
        this.vennFigureParameters = vennFigureParameters;
        this.combinationNumber = vennFigureParameters.getCombinationNumber();
        this.keyList = vennFigureParameters.getKeys().stream().map(VennFigureParameters.Attribute::getKey).collect(Collectors.toList());
        this.colorCodes = vennFigureParameters.getKeys().stream().map(VennFigureParameters.Attribute::getColorCode).collect(Collectors.toList());
    }

    // create venn figure

    public static <T> VennFigure<T> createVennFigure(CombinationSolver<T, ?> combinationSolver) {
        return createVennFigure(combinationSolver, new ArrayList<>(combinationSolver.getValues().keySet()));
    }


    public static <T> VennFigure<T> createVennFigure(CombinationSolver<T, ?> combinationSolver, List<T> keyOrder) {
        return createVennFigure(new VennFigureParameters<T>(combinationSolver));
    }

    public static <T> VennFigure<T> createVennFigure(VennFigureParameters<T> vennFigureParameters) {

        VennFigureCreator<T> figureCreator = new VennFigureCreator<>(vennFigureParameters);

        switch (vennFigureParameters.getKeys().size()) {
            case 2:
                return figureCreator.createVennFigure2();
            case 3:
                return figureCreator.createVennFigure3();
            case 4:
                return figureCreator.createVennFigure4();
            default:
                throw new IllegalArgumentException("Illegal Number of groups");
        }
    }

    private VennFigure<T> createVennFigure2() {
        final double distance = 50;
        final double size = 100;

        VennFigure<T> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(-distance / 2, 0), 0, size, size, colorCodes.get(0), keyList.get(0)));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(distance / 2, 0), 0, size, size, colorCodes.get(1), keyList.get(1)));

        // label
        vennFigure.addShape(new VennFigure.Text<T>(new VennFigure.Point(-size / 2 - distance / 2 - 5, 0), keyList.get(0).toString(), VennFigure.TextJust.RIGHT));
        vennFigure.addShape(new VennFigure.Text<T>(new VennFigure.Point(size / 2 + distance / 2 + 5, 0), keyList.get(1).toString(), VennFigure.TextJust.LEFT));

        // number of combinations
        vennFigure.addShape(createNumberText(-size / 2, 0, Collections.singleton(keyList.get(0))));
        vennFigure.addShape(createNumberText(size / 2, 0, Collections.singleton(keyList.get(1))));
        vennFigure.addShape(createNumberText(0, 0, new HashSet<>(keyList)));
        return vennFigure;
    }


    private VennFigure<T> createVennFigure3() {
        final double distance = 35;
        final double size = 100;

        VennFigure<T> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(
                new VennFigure.Point(0, -distance), 0, size, size, colorCodes.get(0), keyList.get(0)));
        vennFigure.addShape(new VennFigure.Oval<>(
                new VennFigure.Point(distance * Math.cos(Math.PI / 6), distance / 2), 0, size, size, colorCodes.get(1), keyList.get(1)));
        vennFigure.addShape(new VennFigure.Oval<>(
                new VennFigure.Point(-distance * Math.cos(Math.PI / 6), distance / 2), 0, size, size, colorCodes.get(2), keyList.get(2)));

        // labels
        vennFigure.addShape(new VennFigure.Text<T>(
                new VennFigure.Point(0, -distance - size / 2 - 10), keyList.get(0).toString()));
        vennFigure.addShape(new VennFigure.Text<T>(
                new VennFigure.Point(distance * Math.cos(Math.PI / 6) + 5 + size / 2, distance / 2),
                keyList.get(1).toString(), VennFigure.TextJust.LEFT));
        vennFigure.addShape(new VennFigure.Text<T>(
                new VennFigure.Point(-distance * Math.cos(Math.PI / 6) - 5 - size / 2, distance / 2),
                keyList.get(2).toString(), VennFigure.TextJust.RIGHT));

        // numbers
        vennFigure.addShape(createNumberText(0, -distance * 1.5, Collections.singleton(keyList.get(0))));
        vennFigure.addShape(createNumberText(distance * Math.cos(Math.PI / 6) * 1.5, distance / 2 * 1.5, Collections.singleton(keyList.get(1))));
        vennFigure.addShape(createNumberText(-distance * Math.cos(Math.PI / 6) * 1.5, distance / 2 * 1.5, Collections.singleton(keyList.get(2))));

        vennFigure.addShape(createNumberText(0, distance, asSet(keyList.get(1), keyList.get(2))));
        vennFigure.addShape(createNumberText(distance * Math.cos(Math.PI / 6), -distance / 2, asSet(keyList.get(0), keyList.get(1))));
        vennFigure.addShape(createNumberText(-distance * Math.cos(Math.PI / 6), -distance / 2, asSet(keyList.get(0), keyList.get(2))));

        vennFigure.addShape(createNumberText(0, 0, new HashSet<>(keyList)));

        return vennFigure;
    }


    private VennFigure<T> createVennFigure4() {

        final double size = 200;
        final double size2 = size * 1.0;
        final double distance = size * 0.15;

        VennFigure<T> vennFigure = new VennFigure<>();
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(0, 0), -Math.PI / 4, size, size / 2, colorCodes.get(2), keyList.get(2)));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(0, 0), Math.PI / 4, size, size / 2, colorCodes.get(1), keyList.get(1)));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(distance * 1.5, distance), -Math.PI / 4, size2, size2 / 2, colorCodes.get(3), keyList.get(3)));
        vennFigure.addShape(new VennFigure.Oval<>(new VennFigure.Point(-distance * 1.5, distance), Math.PI / 4, size2, size2 / 2, colorCodes.get(0), keyList.get(0)));

        vennFigure.addShape(new VennFigure.Text<T>(-size / Math.sqrt(2) / 2 - 5, -size / Math.sqrt(2) / 2 - 10,
                keyList.get(1).toString(), VennFigure.TextJust.RIGHT));
        vennFigure.addShape(new VennFigure.Text<T>(size / Math.sqrt(2) / 2 + 5, -size / Math.sqrt(2) / 2 - 10,
                keyList.get(2).toString(), VennFigure.TextJust.LEFT));

        vennFigure.addShape(new VennFigure.Text<T>(size / Math.sqrt(2) / 2 + 5 + distance * 1.5, -size / Math.sqrt(2) / 2 - 10 + distance,
                keyList.get(3).toString(), VennFigure.TextJust.LEFT));
        vennFigure.addShape(new VennFigure.Text<T>(-size / Math.sqrt(2) / 2 - 5 - distance * 1.5, -size / Math.sqrt(2) / 2 - 10 + distance,
                keyList.get(0).toString(), VennFigure.TextJust.RIGHT));
        //vennFigure.addShape(new VennFigure.Text<T>(new VennFigure.Point(-size/Math.sqrt(2)/2-10, -size/Math.sqrt(2)/2-10), keyList.get(1).toString()));

        vennFigure.addShape(createNumberText(-distance * 3, -distance / 2, asSet(keyList.get(0))));
        vennFigure.addShape(createNumberText(-distance * 1.5, -distance * 2, asSet(keyList.get(1))));
        vennFigure.addShape(createNumberText(distance * 1.5, -distance * 2, asSet(keyList.get(2))));
        vennFigure.addShape(createNumberText(distance * 3, -distance / 2, asSet(keyList.get(3))));

        vennFigure.addShape(createNumberText(-distance * 2, -distance, asSet(keyList.get(0), keyList.get(1))));
        vennFigure.addShape(createNumberText(0, -distance, asSet(keyList.get(1), keyList.get(2))));
        vennFigure.addShape(createNumberText(distance * 2, -distance, asSet(keyList.get(2), keyList.get(3))));
        vennFigure.addShape(createNumberText(0, distance * 2.7, asSet(keyList.get(3), keyList.get(0))));
        vennFigure.addShape(createNumberText(-distance * 1.9, distance * 1.2, asSet(keyList.get(0), keyList.get(2))));
        vennFigure.addShape(createNumberText(distance * 1.9, distance * 1.2, asSet(keyList.get(1), keyList.get(3))));

        vennFigure.addShape(createNumberText(-distance, 0, asSet(keyList.get(0), keyList.get(1), keyList.get(2))));
        vennFigure.addShape(createNumberText(distance, 0, asSet(keyList.get(1), keyList.get(2), keyList.get(3))));
        vennFigure.addShape(createNumberText(-distance * 0.6, distance * 2, asSet(keyList.get(0), keyList.get(2), keyList.get(3))));
        vennFigure.addShape(createNumberText(distance * 0.6, distance * 2, asSet(keyList.get(0), keyList.get(1), keyList.get(3))));

        vennFigure.addShape(createNumberText(0, distance, new HashSet<>(keyList)));

        return vennFigure;
    }

    private VennFigure.Text<T> createNumberText(double x, double y, Set<T> combination) {
        return new VennFigure.Text<>(new VennFigure.Point(x, y), Integer.toString(combinationNumber.get(combination)));
    }

    @SafeVarargs
    public static <T> Set<T> asSet(T... values) {
        Set<T> s = new HashSet<>(values.length);
        Collections.addAll(s, values);
        return s;
    }
}
