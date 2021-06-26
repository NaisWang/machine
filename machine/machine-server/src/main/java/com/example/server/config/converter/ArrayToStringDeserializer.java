package com.example.server.config.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.util.Converter;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * @author : whz
 */
public class ArrayToStringDeserializer extends JsonDeserializer<String> {

	@Override
	public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		try {
			if (jsonParser == null || jsonParser.getText() == null) {
				return null;
			}
			String s = jsonParser.getText();
			System.out.println("aaaaaaaaaaaaaaaaaaaaa");
			System.out.println(s);
			System.out.println("aaaaaaaaaaaaaaaaaaaaa");
			return null;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
