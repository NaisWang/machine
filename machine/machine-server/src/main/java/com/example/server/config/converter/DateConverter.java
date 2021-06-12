package com.example.server.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author : whz
 */
@Component
public class DateConverter implements Converter<String, LocalDate> {
	@Override
	public LocalDate convert(String s) {
		//s = s.substring(0, 10);
		//System.out.println(s);
		try {
			if (s.equals("")) return null;
			return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
