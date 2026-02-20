package com.ivk23.hello.ai.agents.graph;

import org.bsc.langgraph4j.action.NodeAction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class ResearcherNode implements NodeAction<SimpleAgentState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResearcherNode.class);

    private static final String DDG_URL = "https://html.duckduckgo.com/html/?q=";

    private final ChatClient chatClient;

    public ResearcherNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(SimpleAgentState state) throws Exception {
        LOGGER.info("**********************************************************************************");
        LOGGER.info("RESEARCHER AGENT: Пошук інформації...");
        LOGGER.info("**********************************************************************************");

        final var topic = state.value(SimpleAgentState.TOPIC);
        if (topic.isEmpty()) {
            throw new IllegalAccessException("Відсутній запит для пошуку.");
        }

        final var searchResults = search((String) topic.get());

        final var systemMessage = """
                Ви - професійний дослідник AI в освіті.
                Проаналізуйте знайдену інформацію та виділіть 5 ключових фактів.
                """;

        final var userMessage = """
                Тема: {topic}
                
                Дані:
                {data}
                """;

        final var aiSummary = chatClient.prompt()
                .system(systemMessage)
                .user(u -> u.text(userMessage)
                        .param("topic", topic)
                        .param("data", searchResults))
                .call()
                .content();

        final var result = """
                %s
                
                AI Висновки:
                %s
                """.formatted(searchResults, aiSummary);

        return Map.of(SimpleAgentState.MESSAGES_KEY, "[OK] Researcher: Пошук завершено", SimpleAgentState.RESEARCH_RESULTS, result);
    }

    private String search(String query) throws IOException {
        LOGGER.info("Пошук інформації в інтернеті через DuckDuckGo...");

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = DDG_URL + encodedQuery;

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept-Language", "en-US,en;q=0.9")
                .timeout(10_000)
                .get();

        Elements results = doc.select(".result__body");

        int i = 0;

        final var searchResultsStr = new StringBuilder();

        for (Element result : results) {
            if (i++ > 3) break;

            String title = result.select(".result__a").text();
            String body = result.select(".result__snippet").text();

            if (!title.isBlank()) {
                searchResultsStr.append("""
                        - %s : %s
                        """.formatted(title, body));
            }
        }

        return searchResultsStr.toString();
    }
}
