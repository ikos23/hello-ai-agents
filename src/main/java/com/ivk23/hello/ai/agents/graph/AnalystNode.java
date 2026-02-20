package com.ivk23.hello.ai.agents.graph;

import org.bsc.langgraph4j.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AnalystNode implements NodeAction<SimpleAgentState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalystNode.class);

    private final ChatClient chatClient;

    public AnalystNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(SimpleAgentState state) throws Exception {
        LOGGER.info("**********************************************************************************");
        LOGGER.info("ANALYST AGENT: Аналіз даних...");
        LOGGER.info("**********************************************************************************");

        final var data = analyzeData((String) state.value(SimpleAgentState.RESEARCH_RESULTS).get());

        final var systemMessage = """
                Ви - експерт з data science та аналізу трендів.
                Проаналізуйте дані та виявіть ключові інсайти, тренди та закономірності.
                """;

        final var userMessage = """
                Дані для аналізу:
                {data}
                """;

        // Create and invoke the chain
        String aiSummary = chatClient.prompt()
                .system(systemMessage)
                .user(u -> u.text(userMessage).param("data", data))
                .call()
                .content();

        final var deepAnalysis = """
                %s
                
                Глибокий аналіз:
                %s
                """.formatted(data, aiSummary);

        LOGGER.info(deepAnalysis);

        return Map.of(SimpleAgentState.MESSAGES_KEY, "[OK] Analyst: Аналіз завершено", SimpleAgentState.ANALYSIS_RESULTS, deepAnalysis);
    }

    public String analyzeData(String input) {
        LOGGER.info("Аналіз тексту та витягування ключової інформації...");

        final var words = input.split("\\s+");
        final var sentences = count(input);

        final var keywords = Map.ofEntries(
                Map.entry("технології", List.of("AI", "штучний інтелект", "machine learning", "ML", "технологія")),
                Map.entry("освіта", List.of("навчання", "студенти", "університет", "освіта", "викладач")),
                Map.entry("тренди", List.of("тренд", "майбутнє", "2025", "2024", "інновація"))
        );

        final var categoryCounts = new HashMap<String, Integer>();

        keywords.forEach((category, categoryWords) -> {
            int count = 0;
            for (String word : categoryWords) {
                if (input.toLowerCase().contains(word)) {
                    count++;
                }
            }
            categoryCounts.put(category, count);
        });

        final var themes = categoryCounts
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));

        final var details = categoryCounts
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> "%s (%d)".formatted(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(", "));

        return """
                Аналіз тексту:
                - Слів: %d
                - Речень: %d
                - Ключові теми: %s
                - Деталі: %s
                """.formatted(words.length, sentences, themes.isBlank() ? "не виявлено" : themes, details);
    }

    private int count(String input) {
        int count = 0;
        for (char c : input.toCharArray()) {
            if (c == '.' || c == '!' || c == '?') {
                count++;
            }
        }
        return count;
    }
}
