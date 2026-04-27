package org.amen.nordea.assignment.service.comparer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CSVComparer implements IOutputComparer {
    @Override
    public boolean compare(String actual, String expected) {
        try {
            List<List<String>> expectedRows = parseCsv(expected);
            List<List<String>> actualRows = parseCsv(actual);

            return expectedRows.equals(actualRows);
        } catch (IOException | IllegalArgumentException exception) {
            return false;
        }
    }

    private List<List<String>> parseCsv(String csv) throws IOException {
        if (csv == null) {
            return List.of();
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .get();

        try (Reader reader = new StringReader(csv); CSVParser parser = format.parse(reader)) {
            List<List<String>> rows = new ArrayList<>();

            for (CSVRecord record : parser) {
                List<String> row = new ArrayList<>();

                for (String value : record) {
                    row.add(value);
                }

                rows.add(row);
            }

            return rows;
        }
    }
}
