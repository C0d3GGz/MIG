package de.familiep.mobileinformationgain.utils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.StringTokenizer;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class StringTokenizerTest {

    public Object[] strings() {
        return new Object[]{
                new Object[]{"Hello World", 2},
                new Object[]{"10 Ziegen ziehen Fliegen.", 4},
                new Object[]{"http://www.mydomain.ltd.uk/blah/some/page.html", 1},
                new Object[]{"That's awesome, thank you.", 4},
                new Object[]{"10:35", 1},
                new Object[]{"Monday, April 24", 3},
                new Object[]{"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam", 10},
                new Object[]{"Willkommen\nHallo", 2},
                new Object[]{"Durch Berühren von \"OK\" wird Mobile Information Gain beendet.", 9}
        };
    }

    @Test
    @Parameters (method = "strings")
    public void shouldReturnCorrectWordCount(String input, int expectedWordcount){
        assertEquals(expectedWordcount, new StringTokenizer(input).countTokens());
    }
}
