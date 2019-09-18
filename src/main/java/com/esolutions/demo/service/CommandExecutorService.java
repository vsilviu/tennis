package com.esolutions.demo.service;

import com.esolutions.demo.model.DeviceAction;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class CommandExecutorService {

    private Thread game;
    private GameAction gameAction;

    @Autowired
    public CommandExecutorService(MqttClient mqttClient, Environment environment) {
        gameAction = new GameAction(mqttClient, environment);
        game = new Thread(gameAction);
    }

    public void execute(DeviceAction action) throws InterruptedException {
        switch (action) {
            case START:
                startGame();
                break;
            case PAUSE:
                pauseGame();
                break;
            case RESUME:
                resumeGame();
                break;
            case STOP:
                stopGame();
                break;
            case RESTART:
                stopGame();
                startGame();
                break;
            default:
                throw new IllegalStateException("Should not reach this point!");
        }
    }

    private void startGame() {
        System.out.println("Starting game!");
        game.start();
    }

    private void pauseGame() throws InterruptedException {
        System.out.println("Pausing game!");
        game.wait();
    }

    private void resumeGame() {
        System.out.println("Resuming game!");
        game.notify();
    }


    private void stopGame() {
        System.out.println("Terminating game!");
        gameAction.terminate();
    }

}
