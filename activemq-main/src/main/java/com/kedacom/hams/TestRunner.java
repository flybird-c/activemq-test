package com.kedacom.hams;

import com.kedacom.hams.activemq.HamsActiveMqClient;
import com.kedacom.hams.activemq.HamsMqttClient;
import com.kedacom.hams.common.Common;
import com.kedacom.hams.config.HamsActiveMqProperties;
import com.kedacom.hams.listener.ConnectImpl;
import com.kedacom.hams.listener.DeviceInfoCallBackImpl;
import com.kedacom.hams.listener.RespCallBackImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author : lzp
 * @date : 2023/9/5 15:07
 * @apiNote : TODO
 */
@Component
@Slf4j
public class TestRunner implements CommandLineRunner {
    private static int count=0;
    @Autowired
    private HamsActiveMqProperties hamsActiveMqProperties;
    @Autowired
    private ConnectImpl connectionListening;
    @Autowired
    private DeviceInfoCallBackImpl devInfoListening;
    @Autowired
    private RespCallBackImpl respListening;

    @Autowired
    ConfigurableApplicationContext configurableApplicationContext;
    @Override
    public void run(String... args) throws Exception {

        log.info("===>TestRunner run<===");
        // 循环等待直到Feign Client注册到Consul中
        int maxAttempts = 60; // 最大等待次数
        int currentAttempt = 0;
        boolean feignClientRegistered = false;
        while (!feignClientRegistered && currentAttempt < maxAttempts) {
            log.info("尝试获取upms初始化activemq的ip配置...");
            // 检查Feign Client是否已经注册到Consul
            feignClientRegistered = checkFeignClientRegistration();
            if (!feignClientRegistered) {
                try {
                    // 等待一段时间再进行下一次检查
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.warn("线程异常:", e);
                    Thread.currentThread().interrupt();
                }
            }
            currentAttempt++;
        }
        if (feignClientRegistered) {
            // Feign Client已经注册到Consul，执行相应的操作
            reloadConfig();
        } else {
            // 达到最大等待次数仍未注册成功，可以选择抛出异常或者记录日志等处理方式
            log.warn("consul尝试获取upms达到最大重试次数,未发现upms注册成功");
            log.info("尝试使用默认配置连接");
        }
        subscribe();
        log.info("===>TestRunner end<===");
    }
    private boolean checkFeignClientRegistration() {
        // 返回true表示已经注册，返回false表示尚未注册
        count++;
        log.info("this count:{}",count);
        if (count>=50){
            return true;
        }
        return false;
    }

    private void reloadConfig() {
        log.info("start ReloadConfig hamsActiveMqProperties:{}", hamsActiveMqProperties);
        hamsActiveMqProperties.setBrokerIp("127.0.0.1");
        log.info("end ReloadConfig hamsActiveMqProperties:{}", hamsActiveMqProperties);
    }

    private void subscribe() {
        log.info("subscribe start");
        HamsMqttClient hamsMqttClient = new HamsMqttClient(hamsActiveMqProperties);
        configurableApplicationContext.getBeanFactory().registerSingleton("hamsMqttClient", hamsMqttClient);
        HamsActiveMqClient hamsActiveMqClient = new HamsActiveMqClient(hamsActiveMqProperties);
        hamsActiveMqClient.setMqttClient(hamsMqttClient);
        configurableApplicationContext.getBeanFactory().registerSingleton("hamsActiveMqClient", hamsActiveMqClient);
        log.info("SvmsDoormsRun properties:{}", hamsActiveMqProperties);
        //订阅mqtt消息
        try {
            hamsActiveMqClient.subscribeConnection(connectionListening);
            hamsActiveMqClient.subscribeMqtt(Common.OTA_SUBSCRIBE_DEVICEINFO, devInfoListening);
            hamsActiveMqClient.subscribeMqtt(Common.OTA_SUBSCRIBE_RESP, respListening);
        } catch (Exception e) {
            log.error("subscribe failed:", e);
        }
        log.info("subscribe end");
    }
}
