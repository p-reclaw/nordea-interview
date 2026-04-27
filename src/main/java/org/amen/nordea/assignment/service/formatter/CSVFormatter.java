package org.amen.nordea.assignment.service.formatter;


import org.amen.nordea.assignment.domain.Sentence;
import org.amen.nordea.assignment.domain.Text;

import java.io.IOException;
import java.io.StringWriter;

public class CSVFormatter implements IFormatter {
    private final static String WORD_ = "Word ";
    private final static String SENTENCE_ = "Sentence ";

// This won't work due to the fact CSV format requires char so we need our own impl. of row write
//    private final CSVFormat format = CSVFormat.newFormat(", ").builder();

    String[] writeHeader(Text text){
        String[] header = new String[text.getMaxLength()+1];
        header[0] = "";
        for (int i = 1; i <= text.getMaxLength(); i++) {
            header[i] = WORD_+i;
        }
        return header;
    }

    String[] writeRow(int rowNumber, Sentence sentence) {
        var words = sentence.getWords();
        String[] row = new String[words.size()+1];
        row[0] = SENTENCE_ + rowNumber;

        for (int i = 0; i < words.size(); i++) {
            row[i+1] = words.get(i);
        }

        return row;
    }

    String write(String[] row) throws IOException {
        StringWriter writer = new StringWriter();

        for (int i = 0; i < row.length; i++) {
            if (i > 0) {
                writer.append(", ");
            }

            writer.append(escapeCsv(row[i]));
        }

// This won't work due to the fact CSV format requires char so we need our own impl. of row write
//        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
//            csvPrinter.printRecord((Object[]) row);
//        }

        writer.append("\r\n");
        return writer.toString();
    }

    String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        boolean mustBeQuoted =
                value.contains(",") ||
                        value.contains("\"") ||
                        value.contains("\n") ||
                        value.contains("\r");

        String escaped = value.replace("\"", "\"\"");

        return mustBeQuoted ? "\"" + escaped + "\"" : escaped;
    }

    public String writeToOutput(Text text) throws Exception {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        StringBuilder output = new StringBuilder();

        output.append(write(writeHeader(text)));
        if (text.getSentences() == null) {
            return output.toString();
        }

        for (int i = 0; i < text.getSentences().size(); i++) {
            Sentence sentence = text.getSentences().get(i);

            if (sentence == null) {
                continue;
            }

            output.append(write(writeRow(i + 1, sentence)));
        }

        return output.toString();
    }
}
