package com.example.demo;

import java.time.Duration;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class _03_StructuredPromptTemplates {
	@StructuredPrompt({
	    "Design a medical insurance plan for {{coverageType}} with the following features:",
	    "Structure your answer as follows:",
	    
	    "Insurance Plan Name: ...",
	    "Description: ...",
	    "Coverage Duration: ...",
	    
	    "Covered Medical Services:",
	    "- ...",
	    "- ...",
	    
	    "Financial Details:",
	    "Premium: ...",
	    "Deductible: ...",
	    
	    "Claims Process:",
	    "- ...",
	    "- ..."
	})
	static class CreateMedicalInsurancePlan{
		String coverageType;
		CreateMedicalInsurancePlan(String coverageType){
			this.coverageType = coverageType;
		}
		
	}
	public static void main(String[] args) {
		Duration duration = Duration.ofSeconds(60);
		ChatLanguageModel model = OpenAiChatModel.builder()
				.apiKey(ApiKeys.OPENAI_API_KEY)
				.timeout(duration)
				.build();
		
		///ComprehensiveFamilyPlan - DentalAndVisionInsurance - MaternityCoverage
		CreateMedicalInsurancePlan createMedicalInsurancePlan = new CreateMedicalInsurancePlan("BasicHealthCoverage");
		
		Prompt prompt = StructuredPromptProcessor.toPrompt(createMedicalInsurancePlan);
		String plan = model.generate(prompt.text());
		
		System.out.println(plan);

	}

}
