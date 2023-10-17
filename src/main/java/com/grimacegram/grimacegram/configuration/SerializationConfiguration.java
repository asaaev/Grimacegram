package com.grimacegram.grimacegram.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

import java.io.IOException;
/**
 * Configuration class to define custom serialization settings for the application.
 * Particularly used for customizing the serialization of Page objects to provide
 * a consistent API response format.
 */
@Configuration
public class SerializationConfiguration {
    /**
     * Provides a custom JSON serializer for Page objects.
     * This is useful for ensuring that API responses conform to a consistent format,
     * regardless of the structure of the Page objects being serialized.
     *
     * @return A Jackson Module containing the custom serializer for Page objects.
     */
    @Bean
    public Module springDataPageModule(){
        JsonSerializer<Page> pageJsonSerializer = new JsonSerializer<Page>() {
            @Override
            public void serialize(Page page, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeNumberField("numberOfElements", page.getNumberOfElements());
                jsonGenerator.writeNumberField("totalElements", page.getTotalElements());
                jsonGenerator.writeNumberField("totalPages", page.getTotalPages());
                jsonGenerator.writeNumberField("number", page.getNumber());
                jsonGenerator.writeNumberField("size", page.getSize());
                jsonGenerator.writeBooleanField("first", page.isFirst());
                jsonGenerator.writeBooleanField("last", page.isLast());
                jsonGenerator.writeBooleanField("next", page.hasNext());
                jsonGenerator.writeBooleanField("previous", page.hasPrevious());

                jsonGenerator.writeFieldName("content");
                serializerProvider.defaultSerializeValue(page.getContent(), jsonGenerator);
                jsonGenerator.writeEndObject();
            }
        };
        return new SimpleModule().addSerializer(Page.class, pageJsonSerializer);
    }
}
