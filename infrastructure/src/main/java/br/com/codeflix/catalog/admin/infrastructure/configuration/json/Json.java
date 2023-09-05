package br.com.codeflix.catalog.admin.infrastructure.configuration.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.concurrent.Callable;

public enum Json {
    INSTANCE;

    public static  ObjectMapper mapper() {
        return INSTANCE.mapper.copy();
    }

    public static String writeValueAsString(final Object object) {
        return invoke(() -> INSTANCE.mapper.writeValueAsString(object));
    }

    public static <T> T readValue(final String json, final Class<T> clazz) {
        return invoke(() -> INSTANCE.mapper.readValue(json, clazz));
    }

    private final ObjectMapper mapper = new Jackson2ObjectMapperBuilder()
            .dateFormat(new StdDateFormat())
            .featuresToDisable(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
            )
            .modules(
                    new JavaTimeModule(),
                    new Jdk8Module(),
                    afterburnerModule()
            )
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .build();

    private AfterburnerModule afterburnerModule() {
        // Make Afterburner generate bytecode only for public getters/setters and fields
        // without this. Java 9+ complains of "Illegal reflective access"
        return new AfterburnerModule().setUseValueClassLoader(false);
    }

    private static <T> T invoke(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
