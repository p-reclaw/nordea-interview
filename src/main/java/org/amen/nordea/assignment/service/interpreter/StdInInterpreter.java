package org.amen.nordea.assignment.service.interpreter;

import org.amen.nordea.assignment.domain.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StdInInterpreter implements IInterpreter {
    private BufferedReader reader;

    public StdInInterpreter() {
        this.reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }

    @Override
    public Text processString() throws Exception {
        return readTextFromReader(reader);
    }
}
