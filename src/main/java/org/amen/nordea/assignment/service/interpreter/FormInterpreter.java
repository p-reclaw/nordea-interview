package org.amen.nordea.assignment.service.interpreter;


import org.amen.nordea.assignment.domain.Text;

import java.io.BufferedReader;
import java.io.StringReader;

public class FormInterpreter implements IInterpreter {
    private BufferedReader reader;
    private String text;

    public FormInterpreter(String formContent) {
        this.text = formContent;
        this.reader = new BufferedReader(new StringReader(this.text));
    }

    @Override
    public Text processString() throws Exception {
        return readTextFromReader(reader);
    }
}
