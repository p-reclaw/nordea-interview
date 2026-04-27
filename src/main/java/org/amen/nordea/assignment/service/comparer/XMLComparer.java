package org.amen.nordea.assignment.service.comparer;

import lombok.extern.log4j.Log4j2;
import org.xmlunit.XMLUnitException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

@Log4j2
public class XMLComparer implements IOutputComparer {
    @Override
    public boolean compare(String actual, String expected) {
        try {
            Diff diff = DiffBuilder.compare(expected)
                    .withTest(actual)
                    .ignoreWhitespace()
                    .checkForSimilar()
                    .build();
            return !diff.hasDifferences();
        } catch (XMLUnitException e){
            log.error("Failed to compare XML", e);
            return false;
        }
    }
}
