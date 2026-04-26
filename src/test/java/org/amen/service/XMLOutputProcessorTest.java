package org.amen.service;

import org.amen.domain.Sentence;
import org.amen.domain.Text;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XMLOutputProcessorTest {

    private final XMLOutputProcessor processor = new XMLOutputProcessor();

    @Test
    void writeToOutput_shouldBreakLineOnlyAfterEachSentence() throws Exception {
        Text text = new Text();

        text.addSentence(new Sentence(List.of("Mary", "had", "lamb")));
        text.addSentence(new Sentence(List.of("Peter", "called", "wolf")));
        text.addSentence(new Sentence(List.of("Cinderella", "likes", "shoes")));

        String result = processor.writeToOutput(text);

        String expected = String.join("\n",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
                "<text>",
                "<sentence><word>Mary</word><word>had</word><word>lamb</word></sentence>",
                "<sentence><word>Peter</word><word>called</word><word>wolf</word></sentence>",
                "<sentence><word>Cinderella</word><word>likes</word><word>shoes</word></sentence>",
                "</text>"
        );

        assertEquals(expected, normalizeLineEndings(result));
    }

    @Test
    void writeToOutput_shouldNotBreakLinesInsideSentence() throws Exception {
        Text text = new Text();

        text.addSentence(new Sentence(List.of(
                "he",
                "shocking",
                "shouted",
                "was",
                "What"
        )));

        String result = normalizeLineEndings(processor.writeToOutput(text));

        String[] lines = result.split("\n");

        assertEquals(4, lines.length);

        assertEquals(
                "<sentence><word>he</word><word>shocking</word><word>shouted</word><word>was</word><word>What</word></sentence>",
                lines[2]
        );

        assertTrue(lines[2].startsWith("<sentence>"));
        assertTrue(lines[2].endsWith("</sentence>"));
    }

    private String normalizeLineEndings(String value) {
        return value.replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();
    }
}