package com.kedacom.hams.activemq;

import com.kedacom.hams.callback.HamsMqttCallback;
import com.kedacom.hams.callback.IMessageCallback;
import com.kedacom.hams.config.HamsActiveMqProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.util.StringUtils;

@Slf4j
public class HamsMqttClient {

    private final String clientId = "hams_57";
    private MqttClient mqttClient;
    private MqttConnectOptions options;
    private HamsMqttCallback hamsMqttCallback;

    private HamsActiveMqProperties hamsActiveMqProperties;
    public HamsMqttClient(HamsActiveMqProperties activeMqProperties){
        this.hamsActiveMqProperties = activeMqProperties;
        initMqtt();
        start();
    }

    private void start() {
        Thread thread = new Thread(() -> {
//            while (true){
                try {
                    mqttClient.connect(options);
                } catch (MqttException e) {
                    e.printStackTrace();
                    log.error("HamsMqttClient.start:e = " + e);
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }

        });
        thread.setName("Thread-HamsMqttClient");
        thread.start();
    }

    public void initMqtt(){
        try {
            options = mqttConnectOptions();
            hamsMqttCallback = new HamsMqttCallback();
            mqttClient.setCallback(hamsMqttCallback);
        } catch (MqttException e) {
            e.printStackTrace();
            log.error("HamsMqttClient.initMqtt:e = " + e);
        }
    }

    /**
     * MQTT连接参数设置
     */
    private MqttConnectOptions mqttConnectOptions() throws MqttException {
        String brokerUrl = "tcp://" + hamsActiveMqProperties.getBrokerIp() + ":" + hamsActiveMqProperties.getMqttPort();
        mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        if(!StringUtils.isEmpty(hamsActiveMqProperties.getUsername())){
            options.setUserName(hamsActiveMqProperties.getUsername());
        }
        if(!StringUtils.isEmpty(hamsActiveMqProperties.getPassword())){
            options.setPassword(hamsActiveMqProperties.getPassword().toCharArray());
        }
        options.setConnectionTimeout(10);
        options.setAutomaticReconnect(true);
        options.setCleanSession(false);
        return options;
    }

    /**
     * 向某个主题发布消息 默认qos：1
     *
     * @param topicName:发布的主题
     * @param message：发布的消息
     */
    public void publish(String topicName, String message) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        //mqttMessage.setQos(2);
        mqttMessage.setPayload(message.getBytes());
        MqttTopic mqttTopic = mqttClient.getTopic(topicName);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
    }

    /**
     * 向某个主题发布消息
     *
     * @param topicName: 发布的主题
     * @param msg:   发布的消息
     * @param qos:   消息质量    Qos：0、1、2
     */
    public void publish(String topicName, String msg, int qos) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(qos);
        mqttMessage.setPayload(msg.getBytes());
        MqttTopic mqttTopic = mqttClient.getTopic(topicName);
        MqttDeliveryToken token = mqttTopic.publish(mqttMessage);
        token.waitForCompletion();
    }

    /**
     * 订阅主题
     * @param topicName
     * @throws MqttException
     */
    public void subscribe(String topicName) throws MqttException {
        mqttClient.subscribe(topicName);
    }

    public void setMessageCallBack(String topicName, IMessageCallback messageCallback){
        if(hamsMqttCallback != null){
            hamsMqttCallback.setMessageCallback(topicName, messageCallback);
        }
    }
}
