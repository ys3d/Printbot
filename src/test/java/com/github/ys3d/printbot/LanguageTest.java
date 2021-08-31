package com.github.ys3d.printbot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

/**
 * Tests if all needed information are given in the language-files
 * @author Daniel Schild
 */
@RunWith(Parameterized.class)
public class LanguageTest {

    @Parameterized.Parameters( name = "Language-file: {0}" )
    public static Collection<String> data() {
        return Arrays.asList("de.properties", "en.properties");
    }

    private static final String[] TOKENS = {
            "unknown.command",
            "help",
            "print.enabled",
            "print.cancel",
            "print.cancel.no.job",
            "illegal.file.type",
            "print.success",
            "print.fail",
            "print.another",
            "print.printer.selected",
            "print.printer.select"
    };

    private final Properties lang;

    public LanguageTest(String languageFileName) throws IOException {
        lang = new Properties();
        lang.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(languageFileName));
    }

    @Test
    public void testLanguage(){
        for (String t : TOKENS) {
            assertNotNull(lang.getProperty(t));
        }
    }
}
