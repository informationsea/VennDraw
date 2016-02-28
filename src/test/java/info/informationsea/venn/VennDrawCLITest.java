package info.informationsea.venn;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * VennDraw
 * Copyright (C) 2016 OKAMURA Yasunobu
 * Created on 2016/02/29.
 */
public class VennDrawCLITest {

    public static final File DIST_DIR = new File(new File("build"), "test-cli");

    @Before
    public void setUp() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        DIST_DIR.mkdirs();
    }

    @Test
    public void test1() throws Exception {
        String outputs = Arrays.asList("xls", "xlsx", "csv", "png", "svg", "pdf", "pptx").stream().
                map(it -> new File(DIST_DIR, "test1."+it).toString()).collect(Collectors.joining(","));

        VennDrawCLI.main("-1", "a,b,c", "-2", "b,c,d,e,f", "-o", outputs);
    }

    @Test
    public void test2() throws Exception {
        String outputs = Arrays.asList("xls", "xlsx", "csv", "png", "svg", "pdf", "pptx").stream().
                map(it -> new File(DIST_DIR, "test2."+it).toString()).collect(Collectors.joining(","));

        VennDrawCLI.main("-1", "a,b,c", "-2", "b,c,d,e,f", "-3", "1,2,3,4,b,c", "-o", outputs);
    }
}