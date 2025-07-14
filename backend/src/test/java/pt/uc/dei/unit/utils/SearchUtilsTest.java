package pt.uc.dei.unit.utils;

import pt.uc.dei.utils.SearchUtils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SearchUtilsTest {
    @ParameterizedTest
    @CsvSource({
        "áéíóú, aeiou",
        "ÁÉÍÓÚ, aeiou",
        "çÇ, cc",
        "ãõâêîôû, aoaeiou",
        "normal, normal"
    })
    void testNormalizeString(String input, String expected) {
        assertEquals(expected, SearchUtils.normalizeString(input));
    }

    @ParameterizedTest
    @CsvSource({
        "hello, true",
        "'   ', false",
        ", false",
        "'\t', false",
        "'abc', true"
    })
    void testIsNotBlank(String input, boolean expected) {
        assertEquals(expected, SearchUtils.isNotBlank(input));
    }

    @ParameterizedTest
    @CsvSource({
        "'\"quoted\"', true",
        "'notquoted', false",
        ", false",
        "'\"a\"', true",
        "'\"', false"
    })
    void testIsQuoted(String input, boolean expected) {
        assertEquals(expected, SearchUtils.isQuoted(input));
    }

    @ParameterizedTest
    @CsvSource({
        "'\"abc\"', abc",
        "'abc', abc",
        "'\"a\"', a"
    })
    void testStripQuotes(String input, String expected) {
        assertEquals(expected, SearchUtils.stripQuotes(input));
    }
}
