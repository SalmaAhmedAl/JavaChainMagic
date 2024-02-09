package com.example.demo;

import io.github.cdimascio.dotenv.Dotenv;

public class ApiKeys {
    private static final Dotenv dotenv = Dotenv.load();

	public static final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");


}
