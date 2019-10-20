package com.jchmiel.roche.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zalando.jackson.datatype.money.MoneyModule;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Configuration
public class JacksonConfig implements WebMvcConfigurer {

	private static final String DATE_FORMAT = "dd-MM-yyyy";

	@Bean
	public MoneyModule moneyModule() {
		return new MoneyModule();
	}

	@Bean
	public JavaTimeModule javaTimeModule() {
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
		return javaTimeModule;
	}

	@Bean
	public Formatter<LocalDate> localDateFormatter() {
		return new LocalDateFormatter();
	}

	static class LocalDateFormatter implements Formatter<LocalDate> {

		@Override
		public LocalDate parse(String text, Locale locale) {
			return LocalDate.parse(text, DateTimeFormatter.ofPattern(DATE_FORMAT));
		}

		@Override
		public String print(LocalDate object, Locale locale) {
			return DateTimeFormatter.ofPattern(DATE_FORMAT).format(object);
		}
	}

	static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

		@Override
		public LocalDate deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
			return LocalDate.parse(parser.getValueAsString(), DateTimeFormatter.ofPattern(DATE_FORMAT));
		}
	}

	static class LocalDateSerializer extends JsonSerializer<LocalDate> {

		@Override
		public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
			gen.writeString(value.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
		}
	}
}
