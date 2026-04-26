package org.amen;

import org.amen.domain.Sentence;
import org.amen.domain.Text;
import org.amen.service.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.amen.service.IInputProcessor.OutputFormat.CSV;

public class Main {


    public static void main(String[] args) throws Exception {
        System.out.println("Paste text. Finish input with two empty lines:");

        var reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        IInputProcessor inputProcessor = new StdInInputProcessor();

        Text text = inputProcessor.processString();

        System.out.println("Parsed sentences: " + text.getSentences().size());
        System.out.println("Max sentence length: " + text.getMaxLength());

        IInputProcessor.OutputFormat outputFormat = askOutputFormat(reader);

        IOutputProcessor outputProcessor = switch (outputFormat) {
            case XML -> new XMLOutputProcessor();
            case CSV -> new CSVOutputProcessor();
        };

        String output = outputProcessor.writeToOutput(text);

        System.out.println();
        System.out.println("Generated " + outputFormat + " output:");
        System.out.println(output);
    }

    static IInputProcessor.OutputFormat askOutputFormat(BufferedReader reader) throws Exception {
        while (true) {
            System.out.print("Choose output format [xml/csv]: ");

            String input = reader.readLine();

            if (input == null) {
                throw new IllegalArgumentException("No output format provided");
            }

            String normalizedInput = input.trim().toLowerCase(Locale.ROOT);

            switch (normalizedInput) {
                case "xml":
                    return IInputProcessor.OutputFormat.XML;
                case "csv":
                    return IInputProcessor.OutputFormat.CSV;
                default:
                    System.out.println("Invalid format. Please type 'xml' or 'csv'.");
            }
        }
    }


}