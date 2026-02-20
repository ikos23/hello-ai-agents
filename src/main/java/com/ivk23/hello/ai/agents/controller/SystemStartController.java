package com.ivk23.hello.ai.agents.controller;

import com.ivk23.hello.ai.agents.service.SimpleMultiAgentSystem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemStartController {

    private final SimpleMultiAgentSystem system;

    public SystemStartController(SimpleMultiAgentSystem system) {
        this.system = system;
    }

    @GetMapping(value = "/api/start", produces = "text/plain")
    public String start() {

        system.start("Штучний інтелект в освіті України 2025: можливості та виклики");

        return "Готово!";

    }

}
