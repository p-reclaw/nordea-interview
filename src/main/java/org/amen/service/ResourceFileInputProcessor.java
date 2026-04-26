package org.amen.service;

import org.amen.domain.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ResourceFileInputProcessor implements IInputProcessor {

    private final String resourcePath;
    private final ClassLoader classLoader;

    public ResourceFileInputProcessor(String resourcePath) {
        this(resourcePath, Thread.currentThread().getContextClassLoader());
    }

    public ResourceFileInputProcessor(String resourcePath, ClassLoader classLoader) {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("Resource path cannot be null or blank");
        }

        this.resourcePath = normalizeResourcePath(resourcePath);
        this.classLoader = classLoader;
    }

    @Override
    public Text processString() throws Exception {
        InputStream inputStream = classLoader.getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalArgumentException("Resource file not found: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
        )) {
            return readTextFromReader(reader);
        }
    }

    private String normalizeResourcePath(String resourcePath) {
        if (resourcePath.startsWith("/")) {
            return resourcePath.substring(1);
        }

        return resourcePath;
    }
}