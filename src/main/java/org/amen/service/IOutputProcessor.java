package org.amen.service;

import jakarta.xml.bind.JAXBException;
import org.amen.domain.Text;

/**
 * Converts instance of processed sentence to output format (either csv or xml) and writes it to disk.
 */
public interface IOutputProcessor {
    String writeToOutput(Text text) throws Exception;
}
