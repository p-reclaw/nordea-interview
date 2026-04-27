package org.amen.nordea.assignment.service.comparer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CSVComparerTest {

    private final CSVComparer comparer = new CSVComparer();

    @Test
    void compare_shouldReturnTrueForIdenticalCsv() {
        String actual = """
                , Word 1, Word 2, Word 3
                Sentence 1, Mary, had, lamb
                Sentence 2, Peter, called, wolf
                """;

        String expected = """
                , Word 1, Word 2, Word 3
                Sentence 1, Mary, had, lamb
                Sentence 2, Peter, called, wolf
                """;

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldIgnoreSpacesAroundValuesBecauseTrimIsEnabled() {
        String actual = """
                ,Word 1,Word 2,Word 3
                Sentence 1,Mary,had,lamb
                """;

        String expected = """
                , Word 1, Word 2, Word 3
                Sentence 1, Mary, had, lamb
                """;

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldIgnoreEmptyLines() {
        String actual = """
                
                , Word 1, Word 2
                
                Sentence 1, Mary, lamb
                
                """;

        String expected = """
                , Word 1, Word 2
                Sentence 1, Mary, lamb
                """;

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenValueIsDifferent() {
        String actual = """
                , Word 1, Word 2
                Sentence 1, Mary, lamb
                """;

        String expected = """
                , Word 1, Word 2
                Sentence 1, Mary, wolf
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenRowOrderIsDifferent() {
        String actual = """
                , Word 1, Word 2
                Sentence 2, Peter, wolf
                Sentence 1, Mary, lamb
                """;

        String expected = """
                , Word 1, Word 2
                Sentence 1, Mary, lamb
                Sentence 2, Peter, wolf
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenColumnOrderIsDifferent() {
        String actual = """
                , Word 1, Word 2
                Sentence 1, lamb, Mary
                """;

        String expected = """
                , Word 1, Word 2
                Sentence 1, Mary, lamb
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenNumberOfRowsIsDifferent() {
        String actual = """
                , Word 1, Word 2
                Sentence 1, Mary, lamb
                Sentence 2, Peter, wolf
                """;

        String expected = """
                , Word 1, Word 2
                Sentence 1, Mary, lamb
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenNumberOfColumnsIsDifferent() {
        String actual = """
                , Word 1, Word 2, Word 3
                Sentence 1, Mary, had, lamb
                """;

        String expected = """
                , Word 1, Word 2
                Sentence 1, Mary, had
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldHandleQuotedCommasCorrectly() {
        String actual = """
                , Word 1, Word 2
                Sentence 1, "hello,world", lamb
                """;

        String expected = """
                , Word 1, Word 2
                Sentence 1, "hello,world", lamb
                """;

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldHandleEscapedQuotesCorrectly() {
        String actual = """
                , Word 1
                Sentence 1, "quote ""inside""
                """;

        String expected = """
                , Word 1
                Sentence 1, "quote ""inside""
                """;

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseForInvalidCsv() {
        String actual = """
                , Word 1
                Sentence 1, "unclosed quote
                """;

        String expected = """
                , Word 1
                Sentence 1, valid
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenActualIsNull() {
        String expected = """
                , Word 1
                Sentence 1, Mary
                """;

        assertFalse(comparer.compare(null, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenExpectedIsNull() {
        String actual = """
                , Word 1
                Sentence 1, Mary
                """;

        assertFalse(comparer.compare(actual, null));
    }

    @Test
    void compare_shouldReturnTrueForTwoEmptyInputs() {
        assertTrue(comparer.compare("", ""));
    }

    @Test
    void compare_shouldReturnTrueForOnlyEmptyLinesBecauseEmptyLinesAreIgnored() {
        String actual = "\n\n\n";
        String expected = "";

        assertTrue(comparer.compare(actual, expected));
    }
}