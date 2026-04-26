package org.amen.service;

import org.amen.domain.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class StdInInputProcessor implements IInputProcessor {
    private BufferedReader reader;

    public StdInInputProcessor() {
        this.reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    }

    @Override
    public Text processString() throws Exception {
        return readTextFromReader(reader);
    }
}
