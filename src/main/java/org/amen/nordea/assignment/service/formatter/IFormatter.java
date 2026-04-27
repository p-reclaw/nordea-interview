package org.amen.nordea.assignment.service.formatter;

import org.amen.nordea.assignment.domain.Text;

/**
 * Converts instance of processed sentence to output format (either csv or xml) and writes it to disk.
 */
public interface IFormatter {
    String writeToOutput(Text text) throws Exception;
}
