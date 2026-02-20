package com.ivk23.hello.ai.agents.graph;

import org.bsc.langgraph4j.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ReporterNode implements NodeAction<SimpleAgentState> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReporterNode.class);

    private final ChatClient chatClient;

    public ReporterNode(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(SimpleAgentState state) throws Exception {
        LOGGER.info("**********************************************************************************");
        LOGGER.info("REPORTER AGENT: Створення звіту...");
        LOGGER.info("**********************************************************************************");

        final var systemMessage = """
                Ви - професійний технічний письменник.
                Створіть executive summary та рекомендації на основі дослідження та аналізу.
                """;

        final var userMessage = """
                Дослідження:
                {research}
                
                Аналіз:
                {analysis}
                """;

        final var research = state.value(SimpleAgentState.RESEARCH_RESULTS);
        final var analysis = state.value(SimpleAgentState.ANALYSIS_RESULTS);
        final var topic = state.value(SimpleAgentState.TOPIC);

        final var aiSummary = chatClient.prompt()
                .system(systemMessage)
                .user(u -> u.text(userMessage)
                        .param("research", research)
                        .param("analysis", analysis))
                .call()
                .content();

        final var report = """
                ╔══════════════════════════════════════════════════════════════╗
                ║         LANGGRAPH MULTI-AGENT RESEARCH REPORT                ║
                ╚══════════════════════════════════════════════════════════════╝
                
                Дата: %s
                Тема: %s
                Платформа: LangChain 1.0 + LangGraph
                
                ════════════════════════════════════════════════════════════════
                РЕЗУЛЬТАТИ ДОСЛІДЖЕННЯ (Researcher Agent)
                ════════════════════════════════════════════════════════════════
                
                %s
                
                ════════════════════════════════════════════════════════════════
                АНАЛІТИКА (Analyst Agent)
                ════════════════════════════════════════════════════════════════
                
                %s
                
                ════════════════════════════════════════════════════════════════
                EXECUTIVE SUMMARY (Reporter Agent)
                ════════════════════════════════════════════════════════════════
                
                %s
                """.formatted(LocalDateTime.now().toString(), topic, research, analysis, aiSummary);

        saveReport(report);

        return Map.of(SimpleAgentState.MESSAGES_KEY, "[OK] Reporter: Звіт створено та збережено");
    }

    private String saveReport(String content) {
        LOGGER.info("**********************************************************************************");
        LOGGER.info("Збереження звіту...\n\n{}", content);

        // Імітація збереження в базу даних або файл
        final var reportId = "report_" + System.currentTimeMillis();

        final var msg = "Звіт збережено з ID: " + reportId;

        LOGGER.info(msg);
        LOGGER.info("**********************************************************************************");

        return msg;
    }
}
