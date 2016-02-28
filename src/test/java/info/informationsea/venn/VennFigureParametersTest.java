package info.informationsea.venn;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * venndraw
 * Copyright (C) 2016 OKAMURA Yasunobu
 * Created on 2016/02/27.
 */
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