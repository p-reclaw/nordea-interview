package org.amen.nordea.assignment.sample;

import org.amen.nordea.assignment.TestUtilities;
import org.amen.nordea.assignment.domain.Text;
import org.amen.nordea.assignment.service.formatter.CSVFormatter;
import org.amen.nordea.assignment.service.formatter.XMLFormatter;
import org.amen.nordea.assignment.service.interpreter.ResourceFileInterpreter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SampleFilesTest {

    private final TestUtilities testUtilities = new TestUtilities();

    @ParameterizedTest
    @ValueSource(strings = {"small", "large"})
    void processString_shouldReadInputAndProduceExpectedXmlOutput(String sampleName) throws Exception {
        ResourceFileInterpreter inputProcessor =
                new ResourceFileInterpreter("sample-files/" + sampleName + ".in");

        XMLFormatter outputProcessor = new XMLFormatter();

        Text text = inputProcessor.processString();

        String actualXml = outputProcessor.writeToOutput(text);
        String expectedXml = testUtilities.readResource("sample-files/" + sampleName + ".xml");

        Diff diff = DiffBuilder.compare(expectedXml)
                .withTest(actualXml)
                .ignoreWhitespace()
                .checkForSimilar()
                .build();

        assertFalse(
                diff.hasDifferences(),
                () -> "XML output differs for sample: " + sampleName + "\n" + diff
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"small", "large"})
    void processString_shouldReadInputAndProduceExpectedCsvOutput(String sampleName) throws Exception {
        ResourceFileInterpreter inputProcessor =
                new ResourceFileInterpreter("sample-files/" + sampleName + ".in");

        CSVFormatter outputProcessor = new CSVFormatter();

        Text text = inputProcessor.processString();

        String actualCsv = outputProcessor.writeToOutput(text);
        String expectedCsv = testUtilities.readResource("sample-files/" + sampleName + ".csv");

        assertEquals(
                testUtilities.normalizeLineEndings(expectedCsv),
                testUtilities.normalizeLineEndings(actualCsv),
                "CSV output differs for sample: " + sampleName
        );
    }
}