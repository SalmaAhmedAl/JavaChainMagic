package com.example.demo;

import java.time.Duration;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class _07_ConversationalChain {

	public static void main(String[] args) {
		Duration duration = Duration.ofSeconds(60);
		OpenAiChatModel model = OpenAiChatModel.builder().apiKey(ApiKeys.OPENAI_API_KEY).timeout(duration).build();

		ConversationalChain chain = ConversationalChain.builder().chatLanguageModel(model)
				// .chatMemory(...) // you can override default chat memory
				.build();

		String userMessage1 = "Can you give a brief explanation of the Agile methodology, 3 lines max?";
		System.out.println("[User]: " + userMessage1);

		String answer1 = chain.execute(userMessage1);
		System.out.println("[LLM]: " + answer1);

		String userMessage2 = "What are good tools for that? 3 lines max.";
		System.out.println("[User]: " + userMessage2);

		String answer2 = chain.execute(userMessage2);
		System.out.println("[LLM]: " + answer2);

	}

}
