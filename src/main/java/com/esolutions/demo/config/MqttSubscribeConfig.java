package com.esolutions.demo.config;

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

    @Autowired
    public MqttSubscribeConfig(MqttClient mqttClient, Environment environment) {
        this.mqttClient = mqttClient;
        this.environment = environment;
    }

    @PostConstruct
    public void listen() throws MqttException {
        mqttClient.subscribe("/match/connect/" + environment.getProperty("clientId"), (topic, msg) -> {
            System.out.println(String.format("[%s] : I have connected with you, %s", msg, environment.getProperty("clientId")));
        });

        mqttClient.subscribe(String.format("/match/command/%s", environment.getProperty("clientId")), (topic, msg) -> {
            String content = new String(msg.getPayload());
            String action = content.split("#")[0];
            String sessionId = content.split("#")[1];
            System.out.println(String.format("[%s] : Execute the following command, %s", sessionId, action));
        });
    }
}
