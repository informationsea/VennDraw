package info.informationsea.venn;

import java.util.*;

/**
 * venndrawer
 * Copyright (C) 2016 OKAMURA Yasunobu
 * Created on 2016/02/21.
 */
public class SampleCombinationGenerator {

    public static final Map<Set<String>, Set<String>> twoCombinationResult =
            Collections.unmodifiableMap(createTwoCombinationResult());
    public static final Map<String, Set<String>> twoCombinationValues =
            Collections.unmodifiableMap(createCombinationValueFromResult(createTwoCombinationResult()));

    public static final Map<Set<String>, Set<String>> threeCombinationResult =
            Collections.unmodifiableMap(createThreeCombinationResult());
    public static final Map<String, Set<String>> threeCombinationValues =
            Collections.unmodifiableMap(createCombinationValueFromResult(createThreeCombinationResult()));

    public static final Map<Set<String>, Set<String>> fourCombinationResult =
            Collections.unmodifiableMap(createFourCombinationResult());
    public static final Map<String, Set<String>> fourCombinationValues =
            Collections.unmodifiableMap(createCombinationValueFromResult(createFourCombinationResult()));


    public static Map<Set<String>, Set<String>> createTwoCombinationResult() {
        Map<Set<String>, Set<String>> result = new HashMap<>();
        result.put(asSet("Group 1"), asSet("TMC4"));
        result.put(asSet("Group 2"), asSet("FLG", "TNS4"));
        result.put(asSet("Group 2", "Group 1"), asSet("KRT5", "KRT6A", "ERBB3"));
        return result;
    }

    public static Map<Set<String>, Set<String>> createThreeCombinationResult() {
        Map<Set<String>, Set<String>> result = new HashMap<>();
        result.put(asSet("Group 1"), asSet("TMC4"));
        result.put(asSet("Group 2"), asSet("FLG", "TNS4"));
        result.put(asSet("Group 3"), asSet("KRT5", "KRT6A", "ERBB3"));
        result.put(asSet("Group 1", "Group 2"), asSet("KRT10", "PRSS8", "KRT14", "KLK10"));
        result.put(asSet("Group 2", "Group 3"), asSet("PARD6B", "COL17A1", "KRT17", "KRT18", "SPINT1"));
        result.put(asSet("Group 3", "Group 1"), asSet("DSG2", "EPB41L4B", "CALML3", "GYLTL1B", "MUC15", "KIAA1522"));
        result.put(asSet("Group 1", "Group 2", "Group 3"), asSet("KRT1", "SPRR3", "RPTN", "LAD1", "CAMSAP3", "CAPNS2", "USP43"));
        return result;
    }

    public static Map<Set<String>, Set<String>> createFourCombinationResult() {
        Map<Set<String>, Set<String>> fourResult = new HashMap<>();
        fourResult.put(asSet("A"), asSet("CDKN1A"));
        fourResult.put(asSet("B"), asSet("GP6", "BST2"));
        fourResult.put(asSet("C"), asSet("USP18", "FOS", "MYCT1"));
        fourResult.put(asSet("D"), asSet("PADI4", "GLRX5", "RAMP2", "CEBPB"));
        fourResult.put(asSet("A", "B"), asSet("ERG", "DDX58", "AICDA", "MZB1", "HLA-DOB"));
        fourResult.put(asSet("A", "C"), asSet("PECAM1", "DOK3", "HLA-G", "BLNK", "CLEC1B", "PER1"));
        fourResult.put(asSet("A", "D"), asSet("SOX18", "NFKB2", "FLT1", "GPR65", "TNIP1", "ARHGAP30", "DTX3L"));
        fourResult.put(asSet("B", "C"), asSet("FASLG", "GMIP", "NR4A1", "PPP1R15A", "ACVRL1", "EPX", "ACAP1", "KLF4"));
        fourResult.put(asSet("B", "D"), asSet("HSPA12B", "ADAR", "FAM129C", "MS4A7", "LRG1", "SLC25A37", "IL23A", "AFAP1L1", "KLF2"));
        fourResult.put(asSet("C", "D"), asSet("TXK", "EOMES", "FZD4", "TUBB1", "ZBP1", "GFI1B", "CCNL1", "CSRNP1", "CCR2", "SLAMF6"));
        fourResult.put(asSet("A", "B", "C"), asSet("SAMD9L", "UBA7", "PRG3", "FCER2", "FCGR2B", "HP", "IFITM3", "FAM210B", "PIM1", "GRAP2", "FECH"));
        fourResult.put(asSet("A", "B", "D"), asSet("ABCA13", "TRPV2", "RHCE", "AIF1", "CCL1", "RHD", "HLA-B", "SLA2", "TIPARP", "CCL7", "GPR116", "ALAS2"));
        fourResult.put(asSet("A", "C", "D"), asSet("PLCG2", "CCL20", "MMP10", "GJA4", "CYTIP", "CXCL6", "CXCL11", "XCL1", "LY86", "HIGD1B", "TREML1", "PLK3", "ALOX5AP"));
        fourResult.put(asSet("B", "C", "D"), asSet("ARHGEF6", "SDPR", "HMBS", "MAFF", "MPO", "SELP", "SELPLG", "DNAJB1", "ROBO4", "DDIT4", "VPREB1", "ADCY4", "ZC3H12A", "VWF"));
        fourResult.put(asSet("A", "B", "C", "D"), asSet("KLHL6", "ANGPT2", "ANK1", "KLF6", "P2RY12", "PHLDA1", "PILRA", "PODXL", "MAP3K8", "DDX60", "FOSB", "CCR9", "FPR1", "ICAM1", "ICAM2"));
        return fourResult;
    }

    public static <T, U> Map<T, Set<U>> createCombinationValueFromResult(Map<Set<T>, Set<U>> result) {
        Set<T> keySet = new HashSet<>();
        for (Set<T> one : result.keySet()) {
            keySet.addAll(one);
        }

        Map<T, Set<U>> value = new HashMap<>();
        for (T one : keySet) {
            Set<U> set = new HashSet<>();
            for (Map.Entry<Set<T>, Set<U>> oneEntry : result.entrySet()) {
                if (oneEntry.getKey().contains(one)) {
                    set.addAll(oneEntry.getValue());
                }
            }
            value.put(one, set);
        }

        return value;
    }

    @SafeVarargs
    public static <X> Set<X> asSet(X... x) {
        return VennFigureCreator.asSet(x);
    }
}
