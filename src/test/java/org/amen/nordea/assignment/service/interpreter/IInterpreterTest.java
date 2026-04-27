package org.amen.nordea.assignment.service.interpreter;

import static org.junit.jupiter.api.Assertions.*;


import org.amen.nordea.assignment.domain.Text;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

class IInterpreterTest {

    private final IInterpreter processor = new TestInterpreter();

    private static class TestInterpreter implements IInterpreter {
        @Override
        public Text processString() throws Exception {
            return null;
        }
    }

    @Test
    void readTextFromReader_shouldParseSentencesAndSortWords() throws Exception {
        String input = """
                 Mary had        a        little    lamb .
                 
                Peter called for the wolf , and Aesop came .
                Cinderella likes shoes..
                
                
                """;

        Text result = processor.readTextFromReader(reader(input));

        assertEquals(3, result.getSentences().size());
        assertEquals(8, result.getMaxLength());

        assertEquals(
                List.of("a", "had", "lamb", "little", "Mary"),
                result.getSentences().get(0).getWords()
        );

        assertEquals(
                List.of("Aesop", "and", "called", "came", "for", "Peter", "the", "wolf"),
                result.getSentences().get(1).getWords()
        );

        assertEquals(
                List.of("Cinderella", "likes", "shoes"),
                result.getSentences().get(2).getWords()
        );
    }

    @Test
    void readTextFromReader_shouldNotSplitSentenceAfterMrAbbreviation() throws Exception {
        String input = """
                I was just standing there watching Mr. Young marching around - he
                was    furious.
                
                
                """;

        Text result = processor.readTextFromReader(reader(input));

        assertEquals(1, result.getSentences().size());

        assertEquals(
                List.of(
                        "around",
                        "furious",
                        "he",
                        "I",
                        "just",
                        "marching",
                        "Mr.",
                        "standing",
                        "there",
                        "was",
                        "was",
                        "watching",
                        "Young"
                ),
                result.getSentences().get(0).getWords()
        );
    }

    @Test
    void readTextFromReader_shouldNormalizeCurlyApostrophe() throws Exception {
        String input = """
                In fact - in all of the Nordics, you’d have a hard time finding a product range as strong and diversified as ours.
                
                
                """;

        Text result = processor.readTextFromReader(reader(input));

        assertEquals(1, result.getSentences().size());

        assertTrue(result.getSentences().get(0).getWords().contains("you'd"));
        assertFalse(result.getSentences().get(0).getWords().contains("you’d"));
    }

    @Test
    void readTextFromReader_shouldStopAfterTwoEmptyLines() throws Exception {
        String input = """
                First sentence.
                
                
                This should not be read.
                """;

        Text result = processor.readTextFromReader(reader(input));

        assertEquals(1, result.getSentences().size());
        assertEquals(List.of("First", "sentence"), result.getSentences().get(0).getWords());
    }

    @Test
    void readTextFromReader_shouldAllowSingleEmptyLineInsideInput() throws Exception {
        String input = """
                First sentence.
                
                Second sentence.
                
                
                """;

        Text result = processor.readTextFromReader(reader(input));

        assertEquals(2, result.getSentences().size());
        assertEquals(List.of("First", "sentence"), result.getSentences().get(0).getWords());
        assertEquals(List.of("Second", "sentence"), result.getSentences().get(1).getWords());
    }

    @Test
    void readTextFromReader_shouldHandleRemainingSentenceWithoutEndingDot() throws Exception {
        String input = """
                Sentence without ending delimiter
                
                
                """;

        Text result = processor.readTextFromReader(reader(input));

        assertEquals(1, result.getSentences().size());
        assertEquals(
                List.of("delimiter", "ending", "Sentence", "without"),
                result.getSentences().get(0).getWords()
        );
    }

    @Test
    void extractWords_shouldExtractUnicodeWords() {
        List<String> result = processor.extractWords(
                "停在那儿, 你这肮脏的掠夺者! I couldn't understand."
        );

        assertEquals(
                List.of("停在那儿", "你这肮脏的掠夺者", "I", "couldn't", "understand"),
                result
        );
    }

    @Test
    void extractWords_shouldKeepDotAfterKnownAbbreviation() {
        List<String> result = processor.extractWords("Mr. Young was here.");

        assertEquals(
                List.of("Mr.", "Young", "was", "here"),
                result
        );
    }

    @Test
    void normalizeWord_shouldReplaceCurlyApostrophesWithAsciiApostrophe() {
        assertEquals("you'd", processor.normalizeWord("you’d"));
        assertEquals("don't", processor.normalizeWord("don‘t"));
        assertEquals("rock'n'roll", processor.normalizeWord("rockʼnʼroll"));
    }

    @Test
    void normalizeWord_shouldReturnNullForNullInput() {
        assertNull(processor.normalizeWord(null));
    }

    @ParameterizedTest
    @ValueSource(chars = {'.', '!', '?', '。', '！', '？'})
    void isSentenceDelimiter_shouldReturnTrueForSentenceDelimiters(char character) {
        assertTrue(processor.isSentenceDelimiter(character));
    }

    @ParameterizedTest
    @ValueSource(chars = {',', ';', ':', '-', 'a', ' '})
    void isSentenceDelimiter_shouldReturnFalseForNonSentenceDelimiters(char character) {
        assertFalse(processor.isSentenceDelimiter(character));
    }

    @ParameterizedTest
    @CsvSource({
            "Mr., true",
            "Mrs., true",
            "Dr., true",
            "Prof., true",
            "Young., false",
            "furious., false"
    })
    void isNonSentenceEndingAbbreviation_shouldDetectKnownAbbreviations(
            String input,
            boolean expected
    ) {
        StringBuilder buffer = new StringBuilder(input);
        int dotIndex = input.indexOf('.');

        boolean result = processor.isNonSentenceEndingAbbreviation(buffer, dotIndex);

        assertEquals(expected, result);
    }

    @Test
    void isRealSentenceEnd_shouldReturnFalseForMrDot() {
        StringBuilder buffer = new StringBuilder("I saw Mr. Young");

        int dotIndex = buffer.indexOf(".");

        assertFalse(processor.isRealSentenceEnd(buffer, dotIndex));
    }

    @Test
    void isRealSentenceEnd_shouldReturnTrueForNormalDot() {
        StringBuilder buffer = new StringBuilder("He was furious.");

        int dotIndex = buffer.indexOf(".");

        assertTrue(processor.isRealSentenceEnd(buffer, dotIndex));
    }

    @Test
    void addSentenceIfNotEmpty_shouldNotAddEmptySentence() {
        Text text = new Text();

        processor.addSentenceIfNotEmpty(text, "   ,,, ---   ");

        assertTrue(text.getSentences().isEmpty());
        assertEquals(0, text.getMaxLength());
    }

    @Test
    void addSentenceIfNotEmpty_shouldSortWordsUsingExpectedCaseOrder() {
        Text text = new Text();

        processor.addSentenceIfNotEmpty(
                text,
                "Nordea Markets is the leading international capital markets operator and investment banking partner in the Nordic and Baltic Sea regions"
        );

        assertEquals(
                List.of(
                        "and",
                        "and",
                        "Baltic",
                        "banking",
                        "capital",
                        "in",
                        "international",
                        "investment",
                        "is",
                        "leading",
                        "markets",
                        "Markets",
                        "Nordea",
                        "Nordic",
                        "operator",
                        "partner",
                        "regions",
                        "Sea",
                        "the",
                        "the"
                ),
                text.getSentences().get(0).getWords()
        );
    }

    private BufferedReader reader(String input) {
        return new BufferedReader(new StringReader(input));
    }
}