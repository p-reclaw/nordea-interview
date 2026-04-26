package org.amen;

import net.datafaker.Faker;
import org.amen.domain.Sentence;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TextTestUtilities {
    // Be careful, this affects tests
    private final static int SEED = 0;

    public Sentence generateSentenceOfLength(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Sentence length cannot be negative");
        }

        List<String> words = new Faker(new Random()).lorem().words(length);

        return new Sentence(words);
    }


    public String readResource(String resourcePath) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            assertNotNull(inputStream, "Missing test resource: " + resourcePath);

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public String normalizeLineEndings(String value) {
        return value.replace("\r\n", "\n")
                .replace("\r", "\n");
    }
}
