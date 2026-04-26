package org.amen;

import org.amen.domain.Text;
import org.amen.service.CSVOutputProcessor;
import org.amen.service.ResourceFileInputProcessor;
import org.amen.service.XMLOutputProcessor;
import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class MainSmallTest {
    private final TextTestUtilities textTestUtilities = new TextTestUtilities();

    @Test
    void processString_shouldReadSmallInputAndProduceExpectedXmlOutput() throws Exception {
        ResourceFileInputProcessor inputProcessor =
                new ResourceFileInputProcessor("sample-files/small.in");

        XMLOutputProcessor outputProcessor = new XMLOutputProcessor();

        Text text = inputProcessor.processString();

        String actualXml = outputProcessor.writeToOutput(text);
        String expectedXml = textTestUtilities.readResource("sample-files/small.xml");

        Diff diff = DiffBuilder.compare(expectedXml)
                .withTest(actualXml)
                .ignoreWhitespace()
                .checkForSimilar()
                .build();

        assertFalse(
                diff.hasDifferences(),
                () -> "XML output differs:\n" + diff
        );
    }

    @Test
    void processString_shouldReadSmallInputAndProduceExpectedCsvOutput() throws Exception {
        ResourceFileInputProcessor inputProcessor =
                new ResourceFileInputProcessor("sample-files/small.in");

        CSVOutputProcessor outputProcessor = new CSVOutputProcessor();

        Text text = inputProcessor.processString();
        String actualCsv = outputProcessor.writeToOutput(text);

        String expectedCsv = textTestUtilities.readResource("sample-files/small.csv");

        assertEquals(
                textTestUtilities.normalizeLineEndings(expectedCsv),
                textTestUtilities.normalizeLineEndings(actualCsv)
        );
    }


}