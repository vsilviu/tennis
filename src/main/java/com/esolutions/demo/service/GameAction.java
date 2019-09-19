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

    private volatile boolean running;
    private volatile boolean paused;

    public static final Object lock = new Object();

    public GameAction(MqttClient mqttClient, Environment environment) {
        this.mqttClient = mqttClient;
        this.environment = environment;
    }

    public void markAsStarted() {
        publish("Starting a game of tennis!");
        p1 = 0; p2 = 0;
        running = true;
    }

    public void pause() {
        publish("Pausing the game!");
        paused = true;
    }

    public void resume() {
        publish("Resuming the game!");
        synchronized (lock) {
            paused = false;
            lock.notify();
        }
    }

    public void terminate() {
        synchronized (lock) {
            if (paused) {
                paused = false;
                lock.notify();
            }
            running = false;
        }
        publish("Stopping the game!");
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        synchronized (lock) {
            try {
                while (p1 < 4 && p2 < 4) {

                    if (!running) {
                        //I have terminated the game
                        publish("The game was stopped!");
                        p1 = 0;
                        p2 = 0;
                        return;
                    }

                    if (paused) {
                        publish("The game was paused!");
                        lock.wait();
                    }

                    int serveWinner = (int) (Math.random() * 2);

                    if (serveWinner == 0) p1++;
                    else p2++;

                    String gameScore = String.format("Game score : [%s - %s]", format(p1), format(p2));
                    publish(gameScore);

                    TimeUnit.SECONDS.sleep(3);
                }

                publish(calculateWinMessage());
                terminate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        System.out.println("Runnable ended its work!");
    }

    private String calculateWinMessage() {
        if (p1 == p2) {
            return "It's a draw!";
        } else if (p1 > p2) {
            return "Game ended! Player 1 wins!";
        } else {
            return "Game ended! Player 2 wins!";
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
