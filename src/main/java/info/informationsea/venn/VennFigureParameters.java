package info.informationsea.venn;

import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * venndraw
 * Copyright (C) 2016 OKAMURA Yasunobu
 * Created on 2016/02/27.
 */
@Value
public class VennFigureParameters<T> {

    private Map<Set<T>, Integer> combinationNumber;
    private List<Attribute<T>> keys;

    public <U> VennFigureParameters(CombinationSolver<T, U> combinationSolver) {
        this.combinationNumber = combinationSolver.combinationNumber();
        this.keys = combinationSolver.getValues().keySet().stream().
                map(it -> new Attribute<>(it, "#ffffff00")).collect(Collectors.toList());
    }

    public <U> VennFigureParameters(CombinationSolver<T, U> combinationSolver, List<Attribute<T>> keys) {
        this.combinationNumber = combinationSolver.combinationNumber();
        this.keys = keys;

        if (!combinationSolver.getValues().keySet().equals(keys.stream().map(Attribute::getKey).collect(Collectors.toSet()))) {
            throw new IllegalArgumentException("Combination solver key set is not match to keys");
        }
    }

    public VennFigureParameters(Map<Set<T>, Integer> combinationNumber) {
        this.combinationNumber = combinationNumber;
        this.keys = combinationNumber.keySet().stream().flatMap(Collection::stream).
                map(it -> new Attribute<>(it, "#ffffff00")).collect(Collectors.toList());
    }

    @Value
    public static class Attribute<T> {
        private T key;
        private String colorCode;
    }
}
