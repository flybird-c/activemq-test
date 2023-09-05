package com.kedacom.hams.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "hams.activemq")
@Component
public class HamsActiveMqProperties {

    private String brokerIp;

    private String brokerPort;

    private String mqttPort;

    private String username;

    private String password;

}
