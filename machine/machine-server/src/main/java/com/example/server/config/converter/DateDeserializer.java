package com.example.server.config.converter;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author : whz
 */
public class DateDeserializer extends JsonDeserializer<LocalDateTime> {

	@Override
	public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		try {
			if (jsonParser == null || jsonParser.getText() == null) {
				return null;
			}
			String s = jsonParser.getText();
			//return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-ddThh:mm:ss"));
			return LocalDateTime.parse(s);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
