package com.esolutions.demo.config;

import com.esolutions.demo.model.DeviceAction;
import com.esolutions.demo.service.CommandExecutorService;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MqttSubscribeConfig {

    private final MqttClient mqttClient;
    private final Environment environment;
    private final CommandExecutorService commandExecutorService;

    @Autowired
    public MqttSubscribeConfig(MqttClient mqttClient, Environment environment, CommandExecutorService commandExecutorService) {
        this.mqttClient = mqttClient;
        this.environment = environment;
        this.commandExecutorService = commandExecutorService;
    }

    @PostConstruct
    public void listen() throws MqttException {
        mqttClient.subscribe("/match/connect/" + environment.getProperty("clientId"), (topic, msg) -> {
            try {
                System.out.println(String.format("[%s] : I have connected with you, %s", msg, environment.getProperty("clientId")));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });

        mqttClient.subscribe(String.format("/match/command/%s", environment.getProperty("clientId")), (topic, msg) -> {
            try {
                String content = new String(msg.getPayload());
                DeviceAction action = DeviceAction.valueOf(content.split("#")[0]);
                String sessionId = content.split("#")[1];
                System.out.println(String.format("[%s] : Execute the following command, %s", sessionId, action));
                commandExecutorService.execute(action);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
