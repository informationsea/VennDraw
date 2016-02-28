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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class CombinationSolver<T, U> {

    @Getter
    private Map<T, Set<U>> values;

    @Getter
    private Map<Set<T>, Set<U>> combinationResult;

    private List<T> keyList;

    public CombinationSolver(Map<T, Set<U>> values) {
        this.values = values;
        this.keyList = new ArrayList<>(values.keySet());
        this.combinationResult = calculateCombinationResult();
    }

    private Map<Set<T>, Set<U>> calculateCombinationResult() {
        Collection<Set<T>> keys = keyCombinations();
        Map<Set<T>, Set<U>> result = new HashMap<>();
        for (Set<T> oneKeySet : keys) {
            List<T> oneKeyList = new ArrayList<>(oneKeySet);
            Set<U> valueSet = new HashSet<>(values.get(oneKeyList.get(0)));
            for (T oneKey : oneKeyList.subList(1, oneKeyList.size()))
                valueSet.retainAll(values.get(oneKey));

            Set<T> excludeKeySet = new HashSet<>(values.keySet());
            excludeKeySet.removeAll(oneKeySet);
            for (T oneKey : excludeKeySet) valueSet.removeAll(values.get(oneKey));

            result.put(oneKeySet, valueSet);
        }
        return result;
    }

    public Map<Set<T>, Integer> combinationNumber() {
        Map<Set<T>, Integer> result = new HashMap<>();
        for (Map.Entry<Set<T>, Set<U>> entry : combinationResult.entrySet()) {
            result.put(entry.getKey(), entry.getValue().size());
        }
        return result;
    }

    public Collection<Set<T>> keyCombinations() {
        Set<Set<T>> combinations = new HashSet<>();
        createCombination(new ArrayList<>(), combinations);
        return combinations;
    }

    private void createCombination(List<Boolean> current, Collection<Set<T>> combinations) {
        if (current.size() == keyList.size()) {
            Set<T> newSet = new HashSet<>();
            for (int i = 0; i < keyList.size(); i++) {
                if (current.get(i))
                    newSet.add(keyList.get(i));
            }

            if (newSet.size() > 0)
                combinations.add(newSet);
        } else {
            List<Boolean> trueList = new ArrayList<>(current);
            trueList.add(true);
            createCombination(trueList, combinations);

            List<Boolean> falseList = new ArrayList<>(current);
            falseList.add(false);
            createCombination(falseList, combinations);
        }
    }
}
