package com.esolutions.demo.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.core.env.Environment;

import java.util.concurrent.TimeUnit;

public class GameAction implements Runnable {

    private int p1, p2;
    private MqttClient mqttClient;
    private Environment environment;

    private volatile boolean running = true;

    public GameAction(MqttClient mqttClient, Environment environment) {
        this.mqttClient = mqttClient;
        this.environment = environment;
    }

    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            while (p1 < 4 && p2 < 4) {
                int serveWinner = (int) (Math.random() * 2);

                if (serveWinner == 0) p1++;
                else p2++;

                String gameScore = String.format("Game score : [%s - %s]", format(p1), format(p2));
                publish(gameScore);

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String winMessage = "";

            if (p1 == p2) {
                winMessage = "It's a draw!";
            } else if (p1 > p2) {
                winMessage = "Game ended! Player 1 wins!";
            } else {
                winMessage = "Game ended! Player 2 wins!";
            }

            publish(winMessage);
            terminate();
            break;
        }
    }

    private void publish(String message) {
        System.out.println(message);
        try {
            mqttClient.publish("/stats", new MqttMessage((environment.getProperty("clientId") + "#" + message).getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private String format(int player) {
        return String.valueOf(player * 15);
    }

}
