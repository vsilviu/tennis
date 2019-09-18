package com.esolutions.demo.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MqttClientConfig {

    @Bean
    public MqttClient mqttClientFactory(Environment environment) throws MqttException {
        MqttClient mqttClient = new MqttClient("tcp://localhost:1883", environment.getProperty("clientId"), new MemoryPersistence());
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setMqttVersion(4);
        mqttConnectOptions.setKeepAliveInterval(15000);
        mqttClient.connectWithResult(mqttConnectOptions);
        return mqttClient;
    }

}
