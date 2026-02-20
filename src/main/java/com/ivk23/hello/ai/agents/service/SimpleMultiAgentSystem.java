package com.ivk23.hello.ai.agents.service;

import com.ivk23.hello.ai.agents.graph.AnalystNode;
import com.ivk23.hello.ai.agents.graph.ReporterNode;
import com.ivk23.hello.ai.agents.graph.ResearcherNode;
import com.ivk23.hello.ai.agents.graph.SimpleAgentState;
import org.bsc.langgraph4j.GraphDefinition;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.bsc.langgraph4j.GraphDefinition.END;

@Service
public class SimpleMultiAgentSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMultiAgentSystem.class);

    private final ResearcherNode researcherNode;
    private final AnalystNode analystNode;
    private final ReporterNode reporterNode;

    public SimpleMultiAgentSystem(ResearcherNode researcherNode, AnalystNode analystNode, ReporterNode reporterNode) {
        this.researcherNode = researcherNode;
        this.analystNode = analystNode;
        this.reporterNode = reporterNode;
    }

    public void start(String query) {
        StateGraph<SimpleAgentState> stateGraph;
        try {
            stateGraph = new StateGraph<>(SimpleAgentState.SCHEMA, SimpleAgentState::new)
                    .addNode("researcher", AsyncNodeAction.node_async(researcherNode))
                    .addNode("analyst", AsyncNodeAction.node_async(analystNode))
                    .addNode("reporter", AsyncNodeAction.node_async(reporterNode))
                    .addEdge(GraphDefinition.START, "researcher")
                    .addEdge("researcher", "analyst")
                    .addEdge("analyst", "reporter")
                    .addEdge("reporter", END);

            final var compiledGraph = stateGraph.compile();

            compiledGraph.invoke(Map.of(SimpleAgentState.TOPIC, query));

        } catch (GraphStateException e) {
            throw new RuntimeException(e);
        }
    }

}
