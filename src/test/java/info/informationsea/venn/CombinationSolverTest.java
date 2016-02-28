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

import info.informationsea.venn.graphics.VennDrawGraphics2D;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

@Slf4j
public class CombinationSolverTest {

    private CombinationSolver<String, Integer> two;
    private CombinationSolver<String, Integer> three;
    private CombinationSolver<String, String> four;

    private Map<Set<String>, Set<String>> fourResult;

    public static final File DIST_DIR = new File(new File("build"), "test-images2");

    @BeforeClass
    public static void setup() throws Exception {
        //noinspection ResultOfMethodCallIgnored
        DIST_DIR.mkdirs();
    }

    @Before
    public void setUp() throws Exception {
        Map<String, Set<Integer>> twoValues = new HashMap<>();
        twoValues.put("A", asSet(1, 4, 5, 6));
        twoValues.put("B", asSet(2, 3, 4, 5, 6));

        Map<String, Set<Integer>> threeValues = new HashMap<>();
        threeValues.put("A", asSet(1, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28));
        threeValues.put("B", asSet(2, 3, 7, 8, 9, 10, 11, 12, 13, 14, 15, 22, 23, 24, 25, 26, 27, 28));
        threeValues.put("C", asSet(4, 5, 6, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28));

        two = new CombinationSolver<>(twoValues);
        three = new CombinationSolver<>(threeValues);
        four = new CombinationSolver<>(SampleCombinationGenerator.fourCombinationValues);
        fourResult = SampleCombinationGenerator.fourCombinationResult;
    }

    @Test
    public void testCombinationResult() throws Exception {
        Map<Set<String>, Set<Integer>> answerTwo = new HashMap<>();
        answerTwo.put(asSet("A"), asSet(1));
        answerTwo.put(asSet("B"), asSet(2, 3));
        answerTwo.put(asSet("A", "B"), asSet(4, 5, 6));
        Assert.assertEquals(answerTwo, two.getCombinationResult());

        BufferedImage image = prepareTestImage(800, 800);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.translate(25, 25);
        VennDrawGraphics2D.draw(VennFigureCreator.createVennFigure(two), graphics, new Dimension(750, 750));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "test-two.png"));

        Map<Set<String>, Set<Integer>> answerThree = new HashMap<>();
        answerThree.put(asSet("A"), asSet(1));
        answerThree.put(asSet("B"), asSet(2, 3));
        answerThree.put(asSet("C"), asSet(4, 5, 6));
        answerThree.put(asSet("A", "B"), asSet(7, 8, 9, 10));
        answerThree.put(asSet("B", "C"), asSet(11, 12, 13, 14, 15));
        answerThree.put(asSet("C", "A"), asSet(16, 17, 18, 19, 20, 21));
        answerThree.put(asSet("A", "B", "C"), asSet(22, 23, 24, 25, 26, 27, 28));
        Assert.assertEquals(answerThree, three.getCombinationResult());

        image = prepareTestImage(800, 800);
        graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.translate(25, 25);
        VennDrawGraphics2D.draw(VennFigureCreator.createVennFigure(three), graphics, new Dimension(750, 750));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "test-three.png"));

        Assert.assertEquals(fourResult, four.getCombinationResult());

        image = prepareTestImage(800, 800);
        graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.translate(25, 25);
        VennDrawGraphics2D.draw(VennFigureCreator.createVennFigure(four), graphics, new Dimension(750, 750));
        ImageIO.write(image, "PNG", new File(DIST_DIR, "test-four.png"));
    }

    @Test
    public void testCombinationNumber() throws Exception {
        Map<Set<String>, Integer> answerTwo = new HashMap<>();
        answerTwo.put(asSet("A"), 1);
        answerTwo.put(asSet("B"), 2);
        answerTwo.put(asSet("A", "B"), 3);
        Assert.assertEquals(answerTwo, two.combinationNumber());

        Map<Set<String>, Integer> answerThree = new HashMap<>();
        answerThree.put(asSet("A"), 1);
        answerThree.put(asSet("B"), 2);
        answerThree.put(asSet("C"), 3);
        answerThree.put(asSet("A", "B"), 4);
        answerThree.put(asSet("B", "C"), 5);
        answerThree.put(asSet("C", "A"), 6);
        answerThree.put(asSet("A", "B", "C"), 7);
        Assert.assertEquals(answerThree, three.combinationNumber());


        Map<Set<String>, Integer> answerFour = new HashMap<>();
        for (Map.Entry<Set<String>, Set<String>> one : fourResult.entrySet()) {
            answerFour.put(one.getKey(), one.getValue().size());
        }
        Assert.assertEquals(answerFour, four.combinationNumber());
    }

    @Test
    public void testKeyCombinations() throws Exception {
        Assert.assertEquals(asSet(asSet("A"), asSet("B"), asSet("A", "B")), two.keyCombinations());
        Assert.assertEquals(asSet(asSet("A"), asSet("B"), asSet("C"),
                asSet("A", "B"), asSet("B", "C"), asSet("C", "A"), asSet("A", "B", "C")), three.keyCombinations());
        Assert.assertEquals(fourResult.keySet(), four.keyCombinations());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegal() throws Exception {
        Map<String, Set<Integer>> testValue = new HashMap<>();
        testValue.put("A", asSet(1, 2));
        testValue.put("B", asSet(1, 2));
        testValue.put("C", asSet(1, 5));
        testValue.put("D", asSet(5, 6));
        testValue.put("E", asSet(8, 9));
        CombinationSolver<String, Integer> combinationSolver = new CombinationSolver<>(testValue);

        Assert.assertEquals(testValue, combinationSolver.getValues());
        VennFigureCreator.createVennFigure(combinationSolver);
    }

    @SafeVarargs
    public final <T> Set<T> asSet(T... values) {
        Set<T> s = new HashSet<>(values.length);
        Collections.addAll(s, values);
        return s;
    }

    public static BufferedImage prepareTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.BLACK);
        return image;
    }
}