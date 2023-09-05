package com.kedacom.hams.config;


import com.kedacom.hams.activemq.HamsActiveMqClient;
import com.kedacom.hams.activemq.HamsMqttClient;
import com.kedacom.hams.common.CommonContant;
import com.kedacom.hams.listener.ConnectImpl;
import com.kedacom.hams.listener.DeviceInfoCallBackImpl;
import com.kedacom.hams.listener.RespCallBackImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


/**
 * @author : lzp
 * @date : 2023/8/25 17:09
 * @apiNote : 根据配置的ip加载activemq
 */
@Slf4j
@Component
public class ReloadConfig implements ApplicationListener<ApplicationReadyEvent>  {
    @Autowired
    private HamsActiveMqProperties hamsActiveMqProperties;
    @Autowired
    private ConnectImpl connect;
    @Autowired
    private DeviceInfoCallBackImpl deviceInfoCallBack;
    @Autowired
    private RespCallBackImpl respCallBack;
    @Autowired
    private  ConfigurableApplicationContext configurableApplicationContext;
    @Autowired
    @Lazy
    HamsActiveMqClient mqClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("===>ReloadConfig start load activemq Event<===");
        String changeIp="172.16.137.184";
        String password="admin";
        hamsActiveMqProperties.setBrokerIp(changeIp);
        hamsActiveMqProperties.setPassword(password);
        HamsMqttClient hamsMqttClient = new HamsMqttClient(hamsActiveMqProperties);
        configurableApplicationContext.getBeanFactory().registerSingleton("hamsMqttClient",hamsMqttClient);

        HamsActiveMqClient hamsActiveMqClient = new HamsActiveMqClient(hamsActiveMqProperties);
        hamsActiveMqClient.setMqttClient(hamsMqttClient);
        configurableApplicationContext.getBeanFactory().registerSingleton("hamsActiveMqClient",hamsActiveMqClient);
        try {
            mqClient.subscribeConnection(connect);
            mqClient.subscribeMqtt(CommonContant.OTA_SUBSCRIBE_RESP,respCallBack);
            mqClient.subscribeMqtt(CommonContant.OTA_SUBSCRIBE_DEVICEINFO,deviceInfoCallBack);
        }catch (Exception e){
            log.error("subscribe failed:" , e);
        }
        log.info("===>ReloadConfig end load activemq Event<===");
    }
}
