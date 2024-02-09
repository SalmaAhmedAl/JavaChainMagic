package com.example.demo;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class _02_PromptTemplates {
	public static void main(String[] args) {
		Duration duration = Duration.ofSeconds(60);
		ChatLanguageModel model = OpenAiChatModel.builder()
				.apiKey(ApiKeys.OPENAI_API_KEY)
				.timeout(duration)
				.build();
		String template = "Create a recipe for a {{dishType}} with the following ingredients: {{ingredients}}";
		PromptTemplate promptTemplate = PromptTemplate.from(template);
		Map<String, Object> variables = new HashMap<>();
		variables.put("dishType", "oven dish");
		variables.put("ingredients", "pottato, tomato, onion, olive oil");
		
		Prompt prompt = promptTemplate.apply(variables);
		String response = model.generate(prompt.text());
		
		System.out.println(response);
	}

}
