package com.kedacom.hams.callback;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HamsMqttCallback implements MqttCallback {

    private Map<String,IMessageCallback> messageCallbackMap = new HashMap<>();

    public void setMessageCallback(String topic, IMessageCallback messageCallback){
        messageCallbackMap.put(topic, messageCallback);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        log.error("disconnect mqtt: " + throwable);
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.info("arrived message, topic: " + topic + " message: " + new String(mqttMessage.getPayload()));
        IMessageCallback messageCallback = messageCallbackMap.get(topic);
        if(messageCallback != null){
            messageCallback.messageArrived(new String(mqttMessage.getPayload()));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info("publish success!" + Arrays.toString(iMqttDeliveryToken.getTopics()));
    }
}
