package com.example.demo;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public class AiServicesExamples {
	static Duration duration = Duration.ofSeconds(60);
	static ChatLanguageModel model = OpenAiChatModel.builder().apiKey(ApiKeys.OPENAI_API_KEY).timeout(duration).build();

	////////////////// SIMPLE EXAMPLE //////////////////////

	static class Simple_AI_Service_Example {

		interface Assistant {

			String chat(String message);
		}

		public static void main(String[] args) {

			Assistant assistant = AiServices.create(Assistant.class, model);

			String userMessage = "Translate 'Plus-Values des cessions de valeurs mobilières, de droits sociaux et gains assimilés'";

			String answer = assistant.chat(userMessage);

			System.out.println(answer);
		}
	}

	////////////////// WITH MESSAGE AND VARIABLES //////////////////////

	static class AI_Service_with_System_and_User_Messages_Example {

		interface TextUtils {

			@SystemMessage("You are a professional translator into {{language}}")
			@UserMessage("Translate the following text: {{text}}")
			String translate(@V("text") String text, @V("language") String language);

			@SystemMessage("Summarize every message from user in {{n}} bullet points. Provide only bullet points.")
			List<String> summarize(@UserMessage String text, @V("n") int n);
		}

		public static void main(String[] args) {

			TextUtils utils = AiServices.create(TextUtils.class, model);

			String translation = utils.translate("Hello, how are you?", "italian");
			System.out.println(translation); // Ciao, come stai?

			String text = "AI, or artificial intelligence, is a branch of computer science that aims to create "
					+ "machines that mimic human intelligence. This can range from simple tasks such as recognizing "
					+ "patterns or speech to more complex tasks like making decisions or predictions.";

			List<String> bulletPoints = utils.summarize(text, 3);
			System.out.println(bulletPoints);
		}
	}
////////////////////EXTRACTING DIFFERENT DATA TYPES ////////////////////

	static class Sentiment_Extracting_AI_Service_Example {

		enum Sentiment {
			POSITIVE, NEUTRAL, NEGATIVE;
		}

		interface SentimentAnalyzer {

			@UserMessage("Analyze sentiment of {{it}}")
			Sentiment analyzeSentimentOf(String text);

			@UserMessage("Does {{it}} have a positive sentiment?")
			boolean isPositive(String text);
		}

		public static void main(String[] args) {

			SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, model);

			Sentiment sentiment = sentimentAnalyzer.analyzeSentimentOf("It is amazing!");
			System.out.println(sentiment); // POSITIVE

			boolean positive = sentimentAnalyzer.isPositive("It is bad!");
			System.out.println(positive); // false
		}
	}

	static class POJO_Extracting_AI_Service_Example {

		static class Person {

			private String firstName;
			private String lastName;
			private LocalDate birthDate;

			@Override
			public String toString() {
				return "Person {" + " firstName = \"" + firstName + "\"" + ", lastName = \"" + lastName + "\""
						+ ", birthDate = " + birthDate + " }";
			}
		}

		interface PersonExtractor {

			@UserMessage("Extract information about a person from {{it}}")
			Person extractPersonFrom(String text);
		}

		public static void main(String[] args) {

			PersonExtractor extractor = AiServices.create(PersonExtractor.class, model);

			String text = "In 1968, amidst the fading echoes of Independence Day, "
					+ "a child named John arrived under the calm evening sky. "
					+ "This newborn, bearing the surname Doe, marked the start of a new journey.";

			Person person = extractor.extractPersonFrom(text);

			System.out.println(person); // Person { firstName = "John", lastName = "Doe", birthDate = 1968-07-04 }
		}
	}
	
	 ////////////////////// DESCRIPTIONS ////////////////////////

    static class POJO_With_Descriptions_Extracting_AI_Service_Example {

        static class Recipe {

            @Description("short title, 3 words maximum")
            private String title;

            @Description("short description, 2 sentences maximum")
            private String description;

            @Description("each step should be described in 6 to 8 words, steps should rhyme with each other")
            private List<String> steps;

            private Integer preparationTimeMinutes;

            @Override
            public String toString() {
                return "Recipe {" +
                        " title = \"" + title + "\"" +
                        ", description = \"" + description + "\"" +
                        ", steps = " + steps +
                        ", preparationTimeMinutes = " + preparationTimeMinutes +
                        " }";
            }
        }

        @StructuredPrompt("Create a recipe of a {{dish}} that can be prepared using only {{ingredients}}")
        static class CreateRecipePrompt {

            private String dish;
            private List<String> ingredients;
        }

        interface Chef {

            Recipe createRecipeFrom(String... ingredients);

            Recipe createRecipe(CreateRecipePrompt prompt);
        }

        public static void main(String[] args) {

            Chef chef = AiServices.create(Chef.class, model);

            Recipe recipe = chef.createRecipeFrom("cucumber", "tomato", "feta", "onion", "olives", "lemon");

            System.out.println(recipe);
            

            CreateRecipePrompt prompt = new CreateRecipePrompt();
            prompt.dish = "oven dish";
            prompt.ingredients = Arrays.asList("cucumber", "tomato", "feta", "onion", "olives", "potatoes");

            Recipe anotherRecipe = chef.createRecipe(prompt);
            System.out.println(anotherRecipe);
        }
    }
    
    ////////////////////////// WITH MEMORY /////////////////////////

    static class ServiceWithMemoryExample {

        interface Assistant {

            String chat(String message);
        }

        public static void main(String[] args) {

            ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

            Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .chatMemory(chatMemory)
                    .build();

            String answer = assistant.chat("Hello! My name is Klaus.");
            System.out.println(answer); // Hello Klaus! How can I assist you today?

            String answerWithName = assistant.chat("What is my name?");
            System.out.println(answerWithName); // Your name is Klaus.
        }
    }

    static class ServiceWithMemoryForEachUserExample {

        interface Assistant {

            String chat(@MemoryId int memoryId, @UserMessage String userMessage);
        }

        public static void main(String[] args) {

            Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                    .build();

            System.out.println(assistant.chat(1, "Hello, my name is Klaus"));
            // Hi Klaus! How can I assist you today?

            System.out.println(assistant.chat(2, "Hello, my name is Francine"));
            // Hello Francine! How can I assist you today?

            System.out.println(assistant.chat(1, "What is my name?"));
            // Your name is Klaus.

            System.out.println(assistant.chat(2, "What is my name?"));
            // Your name is Francine.
        }
    }
}
