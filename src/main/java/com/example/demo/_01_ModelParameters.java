package com.example.demo;

import java.time.Duration;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class _01_ModelParameters {
	public static void main(String [] args) {
        Duration duration = Duration.ofSeconds(60);
		ChatLanguageModel model = OpenAiChatModel.builder()
				.apiKey(ApiKeys.OPENAI_KEY)
				.temperature(0.3)
				.timeout(duration)
				.logRequests(true)
				.logResponses(true)
				.build();
		
		String prompt = "Explain in three lines how to make a code cleaner";
		String response = model.generate(prompt);
		System.out.println(response);
		
	}

}
