package org.amen.service;

import org.amen.TextTestUtilities;
import org.amen.domain.Sentence;
import org.amen.domain.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CSVOutputProcessorTest {

    private final TextTestUtilities textTestUtilities = new TextTestUtilities();
    private final CSVOutputProcessor processor = new CSVOutputProcessor();

    @ParameterizedTest
    @MethodSource("headerCases")
    void writeHeader_shouldCreateHeaderBasedOnTextMaxLength(int maxLength, String[] expectedHeader) {
        Text text = new Text();
        text.addSentence(textTestUtilities.generateSentenceOfLength(maxLength));

        String[] result = processor.writeHeader(text);

        assertArrayEquals(expectedHeader, result);
    }

    static Stream<Arguments> headerCases() {
        return Stream.of(
                Arguments.of(
                        0,
                        new String[]{""}
                ),
                Arguments.of(
                        1,
                        new String[]{"", "Word 1"}
                ),
                Arguments.of(
                        2,
                        new String[]{"", "Word 1", "Word 2"}
                ),
                Arguments.of(
                        4,
                        new String[]{"", "Word 1", "Word 2", "Word 3", "Word 4"}
                )
        );
    }

    @Test
    void writeToOutput_shouldWriteTextAsCsv() throws Exception {
        Text text = new Text();

        text.addSentence(new Sentence(List.of(
                "a", "had", "lamb", "little", "Mary"
        )));

        text.addSentence(new Sentence(List.of(
                "Aesop", "and", "called", "came", "for", "Peter", "the", "wolf"
        )));

        text.addSentence(new Sentence(List.of(
                "Cinderella", "likes", "shoes"
        )));

        String result = processor.writeToOutput(text);

        String expected = String.join(System.lineSeparator(),
                ", Word 1, Word 2, Word 3, Word 4, Word 5, Word 6, Word 7, Word 8",
                "Sentence 1, a, had, lamb, little, Mary",
                "Sentence 2, Aesop, and, called, came, for, Peter, the, wolf",
                "Sentence 3, Cinderella, likes, shoes"
        ) + System.lineSeparator();

        assertEquals(expected, result);
    }

    @Test
    void writeToOutput_shouldWriteOnlyHeaderWhenThereAreNoSentences() throws Exception {
        Text text = new Text();

        String result = processor.writeToOutput(text);

        String expected = System.lineSeparator();

        assertEquals(expected, result);
    }

    @Test
    void writeToOutput_shouldEscapeCsvValues() throws Exception {
        Text text = new Text();

        text.addSentence(new Sentence(List.of(
                "hello,world",
                "quote\"test",
                "normal"
        )));

        String result = processor.writeToOutput(text);

        String expected = String.join(System.lineSeparator(),
                ", Word 1, Word 2, Word 3",
                "Sentence 1, \"hello,world\", \"quote\"\"test\", normal"
        ) + System.lineSeparator();

        assertEquals(expected, result);
    }

    @Test
    void writeToOutput_shouldThrowExceptionWhenTextIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> processor.writeToOutput(null)
        );
    }
}