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

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class VennFigureParametersTest {

    @Test
    public void testConstructor1() throws Exception {
        CombinationSolver<String, String> combinationSolver =
                new CombinationSolver<>(SampleCombinationGenerator.fourCombinationValues);

        VennFigureParameters<String> vennFigureParameters = new VennFigureParameters<>(combinationSolver);
        Assert.assertEquals(combinationSolver.combinationNumber(), vennFigureParameters.getCombinationNumber());
        Assert.assertEquals(SampleCombinationGenerator.fourCombinationValues.keySet(),
                vennFigureParameters.getKeys().stream().map(VennFigureParameters.Attribute::getKey).collect(Collectors.toSet()));
        Assert.assertEquals(0, vennFigureParameters.getKeys().stream().filter(it -> !it.getColorCode().equals("#ffffff00")).count());
    }

    @Test
    public void testConstructor2() throws Exception {
        CombinationSolver<String, String> combinationSolver =
                new CombinationSolver<>(SampleCombinationGenerator.fourCombinationValues);

        VennFigureParameters<String> vennFigureParameters = new VennFigureParameters<>(combinationSolver.combinationNumber());
        Assert.assertEquals(combinationSolver.combinationNumber(), vennFigureParameters.getCombinationNumber());
        Assert.assertEquals(SampleCombinationGenerator.fourCombinationValues.keySet(),
                vennFigureParameters.getKeys().stream().map(VennFigureParameters.Attribute::getKey).collect(Collectors.toSet()));
        Assert.assertEquals(0, vennFigureParameters.getKeys().stream().filter(it -> !it.getColorCode().equals("#ffffff00")).count());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor3() throws Exception {
        CombinationSolver<String, String> combinationSolver =
                new CombinationSolver<>(SampleCombinationGenerator.fourCombinationValues);

        new VennFigureParameters<>(combinationSolver, new ArrayList<>());
    }


}