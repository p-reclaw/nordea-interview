package org.amen.nordea.assignment.service.formatter;

import org.amen.nordea.assignment.domain.Sentence;
import org.amen.nordea.assignment.domain.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CSVFormatterTest {

    private final CSVFormatter formatter = new CSVFormatter();

    @ParameterizedTest
    @MethodSource("headerCases")
    void writeHeader_shouldCreateHeaderBasedOnMaxSentenceLength(
            Text text,
            String[] expectedHeader
    ) {
        String[] result = formatter.writeHeader(text);

        assertArrayEquals(expectedHeader, result);
    }

    static Stream<Arguments> headerCases() {
        return Stream.of(
                Arguments.of(
                        textWithSentences(),
                        new String[]{""}
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("Mary"))
                        ),
                        new String[]{"", "Word 1"}
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("Mary", "had")),
                                new Sentence(List.of("Peter", "called", "wolf"))
                        ),
                        new String[]{"", "Word 1", "Word 2", "Word 3"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("rowCases")
    void writeRow_shouldCreateCsvRowFromSentence(
            int rowNumber,
            Sentence sentence,
            String[] expectedRow
    ) {
        String[] result = formatter.writeRow(rowNumber, sentence);

        assertArrayEquals(expectedRow, result);
    }

    static Stream<Arguments> rowCases() {
        return Stream.of(
                Arguments.of(
                        1,
                        new Sentence(List.of("Mary")),
                        new String[]{"Sentence 1", "Mary"}
                ),
                Arguments.of(
                        2,
                        new Sentence(List.of("Peter", "called", "wolf")),
                        new String[]{"Sentence 2", "Peter", "called", "wolf"}
                ),
                Arguments.of(
                        3,
                        new Sentence(List.of()),
                        new String[]{"Sentence 3"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("writeCases")
    void write_shouldFormatRowAsCsvLine(
            String[] row,
            String expectedCsvLine
    ) throws Exception {
        String result = formatter.write(row);

        assertEquals(expectedCsvLine, result);
    }

    static Stream<Arguments> writeCases() {
        return Stream.of(
                Arguments.of(
                        new String[]{"Sentence 1", "Mary", "had", "lamb"},
                        "Sentence 1, Mary, had, lamb\r\n"
                ),
                Arguments.of(
                        new String[]{"", "Word 1", "Word 2"},
                        ", Word 1, Word 2\r\n"
                ),
                Arguments.of(
                        new String[]{"Sentence 1"},
                        "Sentence 1\r\n"
                ),
                Arguments.of(
                        new String[]{"Sentence 1", "hello,world", "normal"},
                        "Sentence 1, \"hello,world\", normal\r\n"
                ),
                Arguments.of(
                        new String[]{"Sentence 1", "quote\"inside"},
                        "Sentence 1, \"quote\"\"inside\"\r\n"
                ),
                Arguments.of(
                        new String[]{"Sentence 1", "line\nbreak"},
                        "Sentence 1, \"line\nbreak\"\r\n"
                ),
                Arguments.of(
                        new String[]{"Sentence 1", null, "word"},
                        "Sentence 1, , word\r\n"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("escapeCsvCases")
    void escapeCsv_shouldEscapeValuesCorrectly(
            String input,
            String expected
    ) {
        String result = formatter.escapeCsv(input);

        assertEquals(expected, result);
    }

    static Stream<Arguments> escapeCsvCases() {
        return Stream.of(
                Arguments.of(null, ""),
                Arguments.of("", ""),
                Arguments.of("word", "word"),
                Arguments.of("hello world", "hello world"),
                Arguments.of("hello,world", "\"hello,world\""),
                Arguments.of("quote\"inside", "\"quote\"\"inside\""),
                Arguments.of("line\nbreak", "\"line\nbreak\""),
                Arguments.of("line\rbreak", "\"line\rbreak\""),
                Arguments.of("hello,\"world\"", "\"hello,\"\"world\"\"\"")
        );
    }

    @ParameterizedTest
    @MethodSource("writeToOutputCases")
    void writeToOutput_shouldFormatTextAsCsv(
            Text text,
            String expectedOutput
    ) throws Exception {
        String result = formatter.writeToOutput(text);

        assertEquals(expectedOutput, result);
    }

    static Stream<Arguments> writeToOutputCases() {
        return Stream.of(
                Arguments.of(
                        textWithSentences(),
                        "\r\n"
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("Mary", "had", "lamb"))
                        ),
                        """
                                , Word 1, Word 2, Word 3\r
                                Sentence 1, Mary, had, lamb\r
                                """.replace("\n", "\n")
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("Mary", "had", "lamb")),
                                new Sentence(List.of("Peter", "called", "wolf")),
                                new Sentence(List.of("Cinderella", "likes", "shoes"))
                        ),
                        """
                                , Word 1, Word 2, Word 3\r
                                Sentence 1, Mary, had, lamb\r
                                Sentence 2, Peter, called, wolf\r
                                Sentence 3, Cinderella, likes, shoes\r
                                """.replace("\n", "\n")
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("hello,world", "quote\"inside"))
                        ),
                        """
                                , Word 1, Word 2\r
                                Sentence 1, "hello,world", "quote""inside"\r
                                """.replace("\n", "\n")
                )
        );
    }

    @Test
    void writeToOutput_shouldSkipNullSentences() throws Exception {
        Text text = textWithSentences(
                new Sentence(List.of("Mary", "had")),
                null,
                new Sentence(List.of("Peter"))
        );

        String result = formatter.writeToOutput(text);

        String expected = ""
                + ", Word 1, Word 2\r\n"
                + "Sentence 1, Mary, had\r\n"
                + "Sentence 2, Peter\r\n";

        assertEquals(expected, result);
    }

    @Test
    void writeToOutput_shouldReturnOnlyHeaderWhenSentencesListIsNull() throws Exception {
        Text text = new Text();
        text.setSentences(null);

        String result = formatter.writeToOutput(text);

        assertEquals("\r\n", result);
    }

    @ParameterizedTest
    @NullSource
    void writeToOutput_shouldThrowExceptionWhenTextIsNull(Text text) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> formatter.writeToOutput(text)
        );

        assertEquals("Text cannot be null", exception.getMessage());
    }

    private static Text textWithSentences(Sentence... sentences) {
        Text text = new Text();

        for (Sentence sentence : sentences) {
            text.addSentence(sentence);
        }

        return text;
    }
}