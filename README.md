# "Hello AI World" Agentic System Example

> **This is an educational project** — built to demonstrate and explore the concepts of Java library for building stateful, multi-agent applications with Large Language Models (LLMs). It is not production-ready software.

Very simple Sprint Boot project with REST Controller that allows to trigger a simple Agentic system.

[LangGraph4J](https://github.com/langgraph4j/langgraph4j) is used to define a simple workflow of 3 agents. Spring Boot and Spring AI - the rest.

## How It Works

```
START
  └─> [Agent 1: Researcher] - searches data in Internet and does initial analysis
        └─> [Agent 2: Analyst] — receives the state from Researcher, performs deeper analysis
              └─> [Agent 3: Reporter] — generates dummy report based on Analyst's findings
```

### Agent 1: Researcher
- Searches requested topic in DuckDuckGo
- Asks LLM to do some analysis of the search results (e.g. extract key points, summarize, find trends)

### Agent 2: Analyst
- Receives the initial data and analysis from Researcher
- Uses LLM to perform deeper analysis

### Agent 3: Reporter
- Receives the data and insights from Analyst
- Asks LLM to generate some suggestions
- Finally generates a dummy report based on all the findings

## Usage

```
### API Test
GET http://localhost:8080/api/start
```

## Project Structure

```
hello-ai-agents /
├── config                     # Simple Spring @Configuration class setup ChatClient bean
├── controller                 # Simple REST Endpoint to trigger the workflow
├── graph                      # Graph components (nodes)
├── service                    # Simple service where the actual graph is configured and executed
```

## Requirements

- Java 21, 
- Gradle
- OpenAI API key
