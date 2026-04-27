package org.amen.nordea.assignment.controller;

import org.amen.nordea.assignment.domain.Text;
import org.amen.nordea.assignment.domain.TextFormContent;
import org.amen.nordea.assignment.service.comparer.IOutputComparer;
import org.amen.nordea.assignment.service.formatter.IFormatter;
import org.amen.nordea.assignment.service.comparer.CSVComparer;
import org.amen.nordea.assignment.service.formatter.CSVFormatter;
import org.amen.nordea.assignment.service.interpreter.FormInterpreter;
import org.amen.nordea.assignment.service.comparer.XMLComparer;
import org.amen.nordea.assignment.service.formatter.XMLFormatter;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

@Controller
public class TextController {

    @GetMapping("/convert")
    public String getForm(Model model) {
        model.addAttribute("textFormContent", new TextFormContent());
        model.addAttribute("comparisonDone", false);
        return "textForm";
    }

    @PostMapping("/convert")
    public String convert(@ModelAttribute TextFormContent textFormContent, @RequestParam String format, Model model) throws Exception {
        FormInterpreter inputProcessor = new FormInterpreter(textFormContent.getInputText());
        Text text = inputProcessor.processString();

        String normalizedInput = format.trim().toLowerCase(Locale.ROOT);

        IFormatter outputProcessor = getOutputProcessor(normalizedInput);
        IOutputComparer outputComparer = getOutputComparer(normalizedInput);

        textFormContent.setActualResult(outputProcessor.writeToOutput(text));
        boolean resultsMatch = outputComparer.compare(textFormContent.getActualResult(), textFormContent.getExpectedResult());

        model.addAttribute("textFormContent", textFormContent);
        model.addAttribute("comparisonDone", true);
        model.addAttribute("resultsMatch", resultsMatch);
        return "textForm";
    }

    private static @NonNull IOutputComparer getOutputComparer(String normalizedInput) {
        return switch (normalizedInput) {
            case "xml" -> new XMLComparer();
            case "csv" -> new CSVComparer();
            default -> throw new IllegalArgumentException("Wrong format");
        };
    }

    private static @NonNull IFormatter getOutputProcessor(String normalizedInput) {
        return switch (normalizedInput) {
            case "xml" -> new XMLFormatter();
            case "csv" -> new CSVFormatter();
            default -> throw new IllegalArgumentException("Wrong format");
        };
    }
}
