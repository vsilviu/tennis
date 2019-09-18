package com.esolutions.demo.controller;

import com.esolutions.demo.model.DeviceAction;
import com.esolutions.demo.service.CommandExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/command/execute")
public class CommandExecutionController {

    private final CommandExecutorService commandExecutorService;

    @Autowired
    public CommandExecutionController(CommandExecutorService commandExecutorService) {
        this.commandExecutorService = commandExecutorService;
    }

    @GetMapping("/{action}")
    public void execute(@PathVariable("action") DeviceAction action) throws InterruptedException {
        commandExecutorService.execute(action);
    }

}
