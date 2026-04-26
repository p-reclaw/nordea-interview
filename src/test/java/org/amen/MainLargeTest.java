package org.amen;

import org.amen.domain.Text;
import org.amen.service.CSVOutputProcessor;
import org.amen.service.ResourceFileInputProcessor;
import org.amen.service.XMLOutputProcessor;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MainLargeTest {
    private final TextTestUtilities textTestUtilities = new TextTestUtilities();

    @Test
    void processString_shouldReadLargeInputAndProduceExpectedXmlOutput() throws Exception {
        ResourceFileInputProcessor inputProcessor = new ResourceFileInputProcessor("sample-files/large.in");

        XMLOutputProcessor outputProcessor = new XMLOutputProcessor();

        Text text = inputProcessor.processString();
        String actualXml = outputProcessor.writeToOutput(text);

//      Create truth file
//        Files.writeString(Path.of("/tmp/large.xml"), actualXml, StandardCharsets.UTF_8);

        String expectedXml = textTestUtilities.readResource("sample-files/large.xml");

//      To ignore irrelevant formatting
        Diff diff = DiffBuilder.compare(expectedXml)
                .withTest(actualXml)
                .ignoreWhitespace()
                .checkForSimilar()
                .build();

        assertFalse(
                diff.hasDifferences(),
                () -> "XML output differs:\n" + diff
        );

//        assertEquals(
//                textTestUtilities.normalizeLineEndings(expectedXml),
//                textTestUtilities.normalizeLineEndings(actualXml)
//        );
    }

    @Test
    void processString_shouldReadLargeInputAndProduceExpectedCsvOutput() throws Exception {
        ResourceFileInputProcessor inputProcessor = new ResourceFileInputProcessor("sample-files/large.in");

        CSVOutputProcessor outputProcessor = new CSVOutputProcessor();

        Text text = inputProcessor.processString();
        String actualCsv = outputProcessor.writeToOutput(text);

//      Create truth file
//        Files.writeString(Path.of("/tmp/large.csv"), actualCsv, StandardCharsets.UTF_8);

        String expectedCsv = textTestUtilities.readResource("sample-files/large.csv");

        assertEquals(
                textTestUtilities.normalizeLineEndings(expectedCsv),
                textTestUtilities.normalizeLineEndings(actualCsv)
        );
    }


}