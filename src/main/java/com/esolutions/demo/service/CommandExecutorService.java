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
        game = new Thread(gameAction, "Game-Thread");
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
        if (gameAction.isRunning()) {
            throw new IllegalStateException("A game is already running! Send a STOP command!");
        }
        System.out.println("Starting game!");
        gameAction.markAsStarted();
        game = new Thread(gameAction);
        game.start();
    }

    private void pauseGame() throws InterruptedException {
        System.out.println("Pausing game!");
        gameAction.pause();
    }

    private void resumeGame() {
        System.out.println("Resuming game!");
        gameAction.resume();
    }


    private void stopGame() throws InterruptedException {
        gameAction.terminate();
        game.join();
    }

}
