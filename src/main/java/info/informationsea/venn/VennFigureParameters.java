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

import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
