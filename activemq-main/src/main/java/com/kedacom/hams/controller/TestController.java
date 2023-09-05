package com.kedacom.hams.controller;

import com.kedacom.hams.activemq.HamsActiveMqClient;
import com.kedacom.hams.dto.MsgDTO;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : lzp
 * @date : 2023/9/5 14:56
 * @apiNote : TODO
 */
@RestController
public class TestController {
    @Autowired
            @Lazy
    HamsActiveMqClient hamsActiveMqClient;
    @PostMapping("/sendMsg")
    public void sendMsg(@RequestBody MsgDTO msgDTO) throws MqttException {
        hamsActiveMqClient.publishMqtt(msgDTO.getTopicName(),msgDTO.getJson());
    }

}
