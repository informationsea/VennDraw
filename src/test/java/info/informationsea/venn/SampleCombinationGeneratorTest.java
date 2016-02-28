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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SampleCombinationGeneratorTest {

    @Test
    public void testCreateCombinationValueFromResult() throws Exception {
        Map<String, Set<String>> value =
                SampleCombinationGenerator.createCombinationValueFromResult(SampleCombinationGenerator.createTwoCombinationResult());

        Assert.assertEquals(VennFigureCreator.asSet("Group 1", "Group 2"), value.keySet());
        Assert.assertEquals(VennFigureCreator.asSet("TMC4", "KRT5", "KRT6A", "ERBB3"), value.get("Group 1"));
        Assert.assertEquals(VennFigureCreator.asSet("FLG", "TNS4", "KRT5", "KRT6A", "ERBB3"), value.get("Group 2"));
    }

    @Test
    public void testCreateCombinationValueFromResult2() throws Exception {
        Map<Set<Integer>, Set<Integer>> result = new HashMap<>();
        result.put(VennFigureCreator.asSet(1), VennFigureCreator.asSet(1));
        result.put(VennFigureCreator.asSet(2), VennFigureCreator.asSet(2));
        result.put(VennFigureCreator.asSet(3), VennFigureCreator.asSet(3));
        result.put(VennFigureCreator.asSet(1, 2), VennFigureCreator.asSet(4));
        result.put(VennFigureCreator.asSet(2, 3), VennFigureCreator.asSet(5));
        result.put(VennFigureCreator.asSet(3, 1), VennFigureCreator.asSet(6));
        result.put(VennFigureCreator.asSet(1, 2, 3), VennFigureCreator.asSet(7));

        Map<Integer, Set<Integer>> value =
                SampleCombinationGenerator.createCombinationValueFromResult(result);

        Assert.assertEquals(VennFigureCreator.asSet(1, 2, 3), value.keySet());
        Assert.assertEquals(VennFigureCreator.asSet(1, 4, 6, 7), value.get(1));
        Assert.assertEquals(VennFigureCreator.asSet(2, 4, 5, 7), value.get(2));
        Assert.assertEquals(VennFigureCreator.asSet(3, 5, 6, 7), value.get(3));
    }
}