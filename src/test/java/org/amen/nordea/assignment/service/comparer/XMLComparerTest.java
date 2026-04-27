package org.amen.nordea.assignment.service.comparer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XMLComparerTest {
    private final XMLComparer comparer = new XMLComparer();

    @Test
    void compare_shouldReturnTrueForIdenticalXml() {
        String actual = """
                <text>
                    <sentence>
                        <word>Mary</word>
                        <word>had</word>
                    </sentence>
                </text>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Mary</word>
                        <word>had</word>
                    </sentence>
                </text>
                """;

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldIgnoreWhitespaceFormattingDifferences() {
        String actual = """
                <text>
                    <sentence>
                        <word>Mary</word>
                        <word>had</word>
                    </sentence>
                </text>
                """;

        String expected = "<text><sentence><word>Mary</word><word>had</word></sentence></text>";

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldTreatEscapedApostropheAndRawApostropheAsEqual() {
        String actual = """
                <text>
                    <sentence>
                        <word>couldn't</word>
                    </sentence>
                </text>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>couldn&apos;t</word>
                    </sentence>
                </text>
                """;

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenElementTextIsDifferent() {
        String actual = """
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                </text>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Peter</word>
                    </sentence>
                </text>
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenElementOrderIsDifferent() {
        String actual = """
                <text>
                    <sentence>
                        <word>had</word>
                        <word>Mary</word>
                    </sentence>
                </text>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Mary</word>
                        <word>had</word>
                    </sentence>
                </text>
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenSentenceOrderIsDifferent() {
        String actual = """
                <text>
                    <sentence>
                        <word>Peter</word>
                    </sentence>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                </text>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                    <sentence>
                        <word>Peter</word>
                    </sentence>
                </text>
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenElementIsMissing() {
        String actual = """
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                </text>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Mary</word>
                        <word>had</word>
                    </sentence>
                </text>
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseWhenRootElementIsDifferent() {
        String actual = """
                <document>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                </document>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                </text>
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnTrueWhenXmlDeclarationDiffersOnlyByPresence() {
        String actual = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                </text>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                </text>
                """;

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnTrueForCompactSentenceLineFormatAndPrettyFormat() {
        String actual = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <text>
                <sentence><word>Mary</word><word>had</word><word>lamb</word></sentence>
                <sentence><word>Peter</word><word>called</word><word>wolf</word></sentence>
                </text>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Mary</word>
                        <word>had</word>
                        <word>lamb</word>
                    </sentence>
                    <sentence>
                        <word>Peter</word>
                        <word>called</word>
                        <word>wolf</word>
                    </sentence>
                </text>
                """;

        assertTrue(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseForInvalidActualXml() {
        String actual = """
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                </text>
                """;

        assertFalse(comparer.compare(actual, expected));
    }

    @Test
    void compare_shouldReturnFalseForInvalidExpectedXml() {
        String actual = """
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                </text>
                """;

        String expected = """
                <text>
                    <sentence>
                        <word>Mary</word>
                    </sentence>
                """;

        assertFalse(comparer.compare(actual, expected));
    }
}