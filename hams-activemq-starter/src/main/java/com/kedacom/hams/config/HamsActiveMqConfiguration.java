package com.kedacom.hams.config;

import com.kedacom.hams.activemq.HamsActiveMqClient;
import com.kedacom.hams.activemq.HamsMqttClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@Configuration
@EnableConfigurationProperties({HamsActiveMqProperties.class})
@ConditionalOnProperty(prefix = "hams.activemq",
        name = {"enable"},
        havingValue = "true",
        matchIfMissing = true)
@Slf4j
public class HamsActiveMqConfiguration {

    @Autowired
    private HamsActiveMqProperties hamsActiveMqProperties;

    @Bean
    public HamsMqttClient hamsMqttClient(){
        log.info("===>starter init bean hamsMqttClient<===");
        return new HamsMqttClient(hamsActiveMqProperties);
    }

    @Bean
    public HamsActiveMqClient activeMqClient(HamsMqttClient hamsMqttClient){
        HamsActiveMqClient hamsActiveMqClient = new HamsActiveMqClient(hamsActiveMqProperties);
        hamsActiveMqClient.setMqttClient(hamsMqttClient);
        log.info("===>starter init bean activeMqClient<===");
        return hamsActiveMqClient;
    }


}
