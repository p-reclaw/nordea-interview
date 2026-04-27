package org.amen.nordea.assignment.service.formatter;


import org.amen.nordea.assignment.domain.Sentence;
import org.amen.nordea.assignment.domain.Text;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class XMLFormatterTest {

    private final XMLFormatter formatter = new XMLFormatter();

    @ParameterizedTest
    @MethodSource("xmlOutputCases")
    void writeToOutput_shouldWriteXmlWithLineBreaksOnlyAfterEachSentence(
            Text text,
            String expectedXml
    ) throws Exception {
        String actualXml = formatter.writeToOutput(text);

        assertEquals(
                normalizeLineEndings(expectedXml),
                normalizeLineEndings(actualXml)
        );
    }

    static Stream<Arguments> xmlOutputCases() {
        return Stream.of(
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("Mary", "had", "lamb"))
                        ),
                        """
                                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                <text>
                                <sentence><word>Mary</word><word>had</word><word>lamb</word></sentence>
                                </text>"""
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("Mary", "had", "lamb")),
                                new Sentence(List.of("Peter", "called", "wolf")),
                                new Sentence(List.of("Cinderella", "likes", "shoes"))
                        ),
                        """
                                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                <text>
                                <sentence><word>Mary</word><word>had</word><word>lamb</word></sentence>
                                <sentence><word>Peter</word><word>called</word><word>wolf</word></sentence>
                                <sentence><word>Cinderella</word><word>likes</word><word>shoes</word></sentence>
                                </text>"""
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("couldn't", "you'd", "Mr."))
                        ),
                        """
                                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                <text>
                                <sentence><word>couldn't</word><word>you'd</word><word>Mr.</word></sentence>
                                </text>"""
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("hello,world", "quote\"inside", "normal"))
                        ),
                        """
                                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                <text>
                                <sentence><word>hello,world</word><word>quote"inside</word><word>normal</word></sentence>
                                </text>"""
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("Tom & Jerry", "x < y", "a > b"))
                        ),
                        """
                                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                <text>
                                <sentence><word>Tom &amp; Jerry</word><word>x &lt; y</word><word>a &gt; b</word></sentence>
                                </text>"""
                )
        );
    }

    @ParameterizedTest
    @MethodSource("sentenceLineCases")
    void writeToOutput_shouldNotBreakLinesInsideSentence(
            Text text,
            String expectedSentenceLine
    ) throws Exception {
        String actualXml = normalizeLineEndings(formatter.writeToOutput(text));

        assertEquals(
                expectedSentenceLine,
                actualXml.lines()
                        .filter(line -> line.startsWith("<sentence>"))
                        .findFirst()
                        .orElseThrow()
        );

        assertFalse(
                expectedSentenceLine.contains("\n"),
                "Sentence line should not contain line breaks"
        );
    }

    static Stream<Arguments> sentenceLineCases() {
        return Stream.of(
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("Mary", "had", "lamb"))
                        ),
                        "<sentence><word>Mary</word><word>had</word><word>lamb</word></sentence>"
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("he", "shocking", "shouted", "was", "What"))
                        ),
                        "<sentence><word>he</word><word>shocking</word><word>shouted</word><word>was</word><word>What</word></sentence>"
                ),
                Arguments.of(
                        textWithSentences(
                                new Sentence(List.of("couldn't", "you'd", "Mr."))
                        ),
                        "<sentence><word>couldn't</word><word>you'd</word><word>Mr.</word></sentence>"
                )
        );
    }

    private static Text textWithSentences(Sentence... sentences) {
        Text text = new Text();

        for (Sentence sentence : sentences) {
            text.addSentence(sentence);
        }

        return text;
    }

    private static String normalizeLineEndings(String value) {
        return value.replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();
    }
}