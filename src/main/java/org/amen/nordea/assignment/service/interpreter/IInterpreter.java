package org.amen.nordea.assignment.service.interpreter;

import org.amen.nordea.assignment.domain.Sentence;
import org.amen.nordea.assignment.domain.Text;

import java.io.BufferedReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface IInterpreter {
    enum OutputFormat {
        XML,
        CSV
    }

    static final int EMPTY_LINES_TO_FINISH = 2;
    static final Pattern WORD_PATTERN = Pattern.compile("[\\p{L}\\p{N}]+(?:['’\\-][\\p{L}\\p{N}]+)*");
    static final Set<String> NON_SENTENCE_ENDING_ABBREVIATIONS = Set.of(
            "mr",
            "mrs",
            "ms",
            "dr",
            "prof",
            "sr",
            "jr",
            "st",
            "vs"
    );

    default Text readTextFromReader(BufferedReader reader) throws Exception {
        Text text = new Text();
        StringBuilder sentenceBuffer = new StringBuilder();

        int emptyLineCount = 0;
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                emptyLineCount++;

                if (emptyLineCount >= EMPTY_LINES_TO_FINISH) {
                    break;
                }

                sentenceBuffer.append(' ');
            } else {
                emptyLineCount = 0;
                sentenceBuffer.append(line).append(' ');
            }

            extractCompleteSentences(sentenceBuffer, text);
        }

        extractRemainingSentence(sentenceBuffer, text);

        return text;
    }

    default void extractCompleteSentences(StringBuilder sentenceBuffer, Text text) {
        int sentenceStart = 0;

        for (int i = 0; i < sentenceBuffer.length(); i++) {
            char current = sentenceBuffer.charAt(i);

            if (isSentenceDelimiter(current) && isRealSentenceEnd(sentenceBuffer, i)) {
                String rawSentence = sentenceBuffer.substring(sentenceStart, i);
                addSentenceIfNotEmpty(text, rawSentence);

                int delimiterEnd = i + 1;

                while (delimiterEnd < sentenceBuffer.length()
                        && isSentenceDelimiter(sentenceBuffer.charAt(delimiterEnd))) {
                    delimiterEnd++;
                }

                sentenceStart = delimiterEnd;
                i = delimiterEnd - 1;
            }
        }

        if (sentenceStart > 0) {
            sentenceBuffer.delete(0, sentenceStart);
        }
    }

    default boolean isRealSentenceEnd(StringBuilder sentenceBuffer, int delimiterIndex) {
        char delimiter = sentenceBuffer.charAt(delimiterIndex);

        if (delimiter != '.') {
            return true;
        }

        return !isNonSentenceEndingAbbreviation(sentenceBuffer, delimiterIndex);
    }

    default boolean isNonSentenceEndingAbbreviation(
            StringBuilder sentenceBuffer,
            int dotIndex
    ) {
        int tokenStart = dotIndex - 1;

        while (tokenStart >= 0 && Character.isLetter(sentenceBuffer.charAt(tokenStart))) {
            tokenStart--;
        }

        String token = sentenceBuffer.substring(tokenStart + 1, dotIndex)
                .toLowerCase(Locale.ROOT);

        return NON_SENTENCE_ENDING_ABBREVIATIONS.contains(token);
    }

    default void extractRemainingSentence(StringBuilder sentenceBuffer, Text text) {
        String rawSentence = sentenceBuffer.toString();
        addSentenceIfNotEmpty(text, rawSentence);
        sentenceBuffer.setLength(0);
    }

    default void addSentenceIfNotEmpty(Text text, String rawSentence) {
        List<String> words = extractWords(rawSentence);
        words.sort(
                String.CASE_INSENSITIVE_ORDER
                        .thenComparing(Comparator.reverseOrder())
        );

        if (!words.isEmpty()) {
            text.addSentence(new Sentence(words));
        }
    }

    default String normalizeWord(String word) {
        if (word == null) {
            return null;
        }

        return word
                .replace('’', '\'')
                .replace('‘', '\'')
                .replace('ʼ', '\'');
    }

    default List<String> extractWords(String rawSentence) {
        List<String> words = new ArrayList<>();

        Matcher matcher = WORD_PATTERN.matcher(rawSentence);

        while (matcher.find()) {
            String word = normalizeWord(matcher.group());

            if (isAbbreviationFollowedByDot(rawSentence, matcher.end(), word)) {
                words.add(word + ".");
            } else {
                words.add(word);
            }
        }

        return words;
    }

    default boolean isAbbreviationFollowedByDot(
            String rawSentence,
            int wordEndIndex,
            String word
    ) {
        if (wordEndIndex >= rawSentence.length()) {
            return false;
        }

        if (rawSentence.charAt(wordEndIndex) != '.') {
            return false;
        }

        return NON_SENTENCE_ENDING_ABBREVIATIONS.contains(
                word.toLowerCase(Locale.ROOT)
        );
    }

    default boolean isSentenceDelimiter(char character) {
        return character == '.'
                || character == '!'
                || character == '?'
                || character == '。'
                || character == '！'
                || character == '？';
    }

    Text processString() throws Exception;
}
