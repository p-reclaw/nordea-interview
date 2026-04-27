package org.amen.nordea.assignment.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
class TextTest {

    @Test
    void getSentenceLength_shouldReturnZeroForNullSentence() throws Exception {
        Text text = new Text();

        int result = invokeGetSentenceLength(text, null);

        assertEquals(0, result);
    }

    @Test
    void getSentenceLength_shouldReturnZeroForSentenceWithNullWords() throws Exception {
        Text text = new Text();
        Sentence sentence = new Sentence(null);

        int result = invokeGetSentenceLength(text, sentence);

        assertEquals(0, result);
    }

    @Test
    void getSentenceLength_shouldReturnNumberOfWords() throws Exception {
        Text text = new Text();
        Sentence sentence = new Sentence(List.of("Hello", "world"));

        int result = invokeGetSentenceLength(text, sentence);

        assertEquals(2, result);
    }

    private int invokeGetSentenceLength(Text text, Sentence sentence) throws Exception {
        Method method = Text.class.getDeclaredMethod("getSentenceLength", Sentence.class);
        method.setAccessible(true);

        return (int) method.invoke(text, sentence);
    }

    @ParameterizedTest
    @MethodSource("sentenceLengthCases")
    void addSentence_shouldUpdateMaxLength(Sentence sentence, int expectedMaxLength) {
        Text text = new Text(new ArrayList<>());

        text.addSentence(sentence);

        assertEquals(expectedMaxLength, text.getMaxLength());
    }

    static Stream<Arguments> sentenceLengthCases() {
        return Stream.of(
                Arguments.of(new Sentence(List.of()), 0),
                Arguments.of(new Sentence(List.of("Hello")), 1),
                Arguments.of(new Sentence(List.of("Hello", "world")), 2),
                Arguments.of(new Sentence(List.of("This", "is", "a", "test")), 4),
                Arguments.of(new Sentence(null), 0)
        );
    }

    @ParameterizedTest
    @NullSource
    void addSentence_shouldIgnoreNullSentence(Sentence sentence) {
        Text text = new Text(new ArrayList<>());

        text.addSentence(sentence);

        assertEquals(0, text.getMaxLength());
        assertTrue(text.getSentences().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("multipleSentencesCases")
    void addSentence_shouldKeepHighestMaxLength(
            List<Sentence> sentences,
            int expectedMaxLength
    ) {
        Text text = new Text(new ArrayList<>());

        for (Sentence sentence : sentences) {
            text.addSentence(sentence);
        }

        assertEquals(expectedMaxLength, text.getMaxLength());
    }

    static Stream<Arguments> multipleSentencesCases() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                new Sentence(List.of("One")),
                                new Sentence(List.of("Two", "words")),
                                new Sentence(List.of("Three", "word", "sentence"))
                        ),
                        3
                ),
                Arguments.of(
                        List.of(
                                new Sentence(List.of("Short")),
                                new Sentence(List.of("This", "is", "longer")),
                                new Sentence(List.of("Mid", "size"))
                        ),
                        3
                ),
                Arguments.of(
                        List.of(
                                new Sentence(List.of()),
                                new Sentence(List.of("Only")),
                                new Sentence(List.of())
                        ),
                        1
                )
        );
    }

    @ParameterizedTest
    @MethodSource("removeSentenceCases")
    void removeSentence_shouldRecalculateMaxLengthAfterRemovingSentence(
            List<Sentence> initialSentences,
            Sentence sentenceToRemove,
            boolean expectedRemoved,
            int expectedMaxLength
    ) {
        Text text = new Text(new ArrayList<>());

        for (Sentence sentence : initialSentences) {
            text.addSentence(sentence);
        }

        boolean removed = text.removeSentence(sentenceToRemove);

        assertEquals(expectedRemoved, removed);
        assertEquals(expectedMaxLength, text.getMaxLength());
    }

    static Stream<Arguments> removeSentenceCases() {
        Sentence oneWord = new Sentence(List.of("One"));
        Sentence twoWords = new Sentence(List.of("Two", "words"));
        Sentence threeWords = new Sentence(List.of("Three", "word", "sentence"));
        Sentence notExisting = new Sentence(List.of("Not", "existing"));

        return Stream.of(
                Arguments.of(
                        List.of(oneWord, twoWords, threeWords),
                        threeWords,
                        true,
                        2
                ),
                Arguments.of(
                        List.of(oneWord, twoWords, threeWords),
                        twoWords,
                        true,
                        3
                ),
                Arguments.of(
                        List.of(oneWord, twoWords, threeWords),
                        oneWord,
                        true,
                        3
                ),
                Arguments.of(
                        List.of(oneWord, twoWords, threeWords),
                        notExisting,
                        false,
                        3
                ),
                Arguments.of(
                        List.of(oneWord, twoWords, threeWords),
                        null,
                        false,
                        3
                )
        );
    }

    @ParameterizedTest
    @MethodSource("removeSentenceByIndexCases")
    void removeSentenceByIndex_shouldRecalculateMaxLength(
            List<Sentence> initialSentences,
            int indexToRemove,
            Sentence expectedRemovedSentence,
            int expectedMaxLength
    ) {
        Text text = new Text();

        for (Sentence sentence : initialSentences) {
            text.addSentence(sentence);
        }

        Sentence removedSentence = text.removeSentence(indexToRemove);

        assertEquals(expectedRemovedSentence, removedSentence);
        assertEquals(expectedMaxLength, text.getMaxLength());
    }

    static Stream<Arguments> removeSentenceByIndexCases() {
        Sentence oneWord = new Sentence(List.of("One"));
        Sentence twoWords = new Sentence(List.of("Two", "words"));
        Sentence threeWords = new Sentence(List.of("Three", "word", "sentence"));

        return Stream.of(
                Arguments.of(
                        List.of(oneWord, twoWords, threeWords),
                        0,
                        oneWord,
                        3
                ),
                Arguments.of(
                        List.of(oneWord, twoWords, threeWords),
                        1,
                        twoWords,
                        3
                ),
                Arguments.of(
                        List.of(oneWord, twoWords, threeWords),
                        2,
                        threeWords,
                        2
                )
        );
    }

    @Test
    void removeSentenceByIndex_shouldThrowExceptionWhenSentencesIsNull() {
        Text text = new Text(null);

        assertThrows(IndexOutOfBoundsException.class, () -> text.removeSentence(0));
    }

    @ParameterizedTest
    @MethodSource("invalidIndexCases")
    void removeSentenceByIndex_shouldThrowExceptionForInvalidIndex(int index) {
        Text text = new Text(new ArrayList<>());
        text.addSentence(new Sentence(List.of("Hello")));

        assertThrows(IndexOutOfBoundsException.class, () -> text.removeSentence(index));
    }

    static Stream<Integer> invalidIndexCases() {
        return Stream.of(-1, 1, 2);
    }
}