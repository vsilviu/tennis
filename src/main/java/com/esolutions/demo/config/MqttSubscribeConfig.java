package com.esolutions.demo.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MqttSubscribeConfig {

    private final MqttClient mqttClient;

    @Autowired
    public MqttSubscribeConfig(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    @PostConstruct
    public void listen() throws MqttException {
        mqttClient.subscribe("/match/connect", (topic, msg) -> {
            System.out.println("A connection attempt was received from WS session id " + new String(msg.getPayload()));
        });
    }
}
