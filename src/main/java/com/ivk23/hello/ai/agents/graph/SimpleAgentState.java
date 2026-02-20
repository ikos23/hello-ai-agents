package com.ivk23.hello.ai.agents.graph;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleAgentState extends AgentState {
    public static final String MESSAGES_KEY = "messages";
    public static final String TOPIC = "topic";
    public static final String RESEARCH_RESULTS = "research_results";
    public static final String ANALYSIS_RESULTS = "analysis_results";
    public static final String FINAL_REPORT = "final_report";

    // Define the schema for the state.
    // MESSAGES_KEY will hold a list of strings, and new messages will be appended.
    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            MESSAGES_KEY, Channels.appender(ArrayList::new)
    );

    public SimpleAgentState(Map<String, Object> initData) {
        super(initData);
    }

    public List<String> messages() {
        return this.<List<String>>value("messages").orElse(List.of());
    }
}