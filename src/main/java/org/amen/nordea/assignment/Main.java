package org.amen.nordea.assignment;


import org.amen.nordea.assignment.domain.Text;
import org.amen.nordea.assignment.service.formatter.CSVFormatter;
import org.amen.nordea.assignment.service.formatter.IFormatter;
import org.amen.nordea.assignment.service.formatter.XMLFormatter;
import org.amen.nordea.assignment.service.interpreter.IInterpreter;
import org.amen.nordea.assignment.service.interpreter.StdInInterpreter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class Main {


    public static void main(String[] args) throws Exception {
        System.out.println("Paste text. Finish input with two empty lines:");

        var reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        IInterpreter inputProcessor = new StdInInterpreter();

        Text text = inputProcessor.processString();

        System.out.println("Parsed sentences: " + text.getSentences().size());
        System.out.println("Max sentence length: " + text.getMaxLength());

        IInterpreter.OutputFormat outputFormat = askOutputFormat(reader);

        IFormatter outputProcessor = switch (outputFormat) {
            case XML -> new XMLFormatter();
            case CSV -> new CSVFormatter();
        };

        String output = outputProcessor.writeToOutput(text);

        System.out.println();
        System.out.println("Generated " + outputFormat + " output:");
        System.out.println(output);
    }

    static IInterpreter.OutputFormat askOutputFormat(BufferedReader reader) throws Exception {
        while (true) {
            System.out.print("Choose output format [xml/csv]: ");

            String input = reader.readLine();

            if (input == null) {
                throw new IllegalArgumentException("No output format provided");
            }

            String normalizedInput = input.trim().toLowerCase(Locale.ROOT);

            switch (normalizedInput) {
                case "xml":
                    return IInterpreter.OutputFormat.XML;
                case "csv":
                    return IInterpreter.OutputFormat.CSV;
                default:
                    System.out.println("Invalid format. Please type 'xml' or 'csv'.");
            }
        }
    }


}