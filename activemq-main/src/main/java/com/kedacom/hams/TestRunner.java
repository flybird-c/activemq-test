package com.kedacom.hams;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author : lzp
 * @date : 2023/9/5 15:07
 * @apiNote : TODO
 */
@Component
@Slf4j
public class TestRunner implements CommandLineRunner {
    //@Autowired
    //@Lazy
    //private HamsActiveMqClient activeMqClient;
    //@Autowired
    //private ConnectImpl connect;
    //@Autowired
    //private DeviceInfoCallBackImpl deviceInfoCallBack;
    //@Autowired
    //private RespCallBackImpl respCallBack;
    @Override
    public void run(String... args) throws Exception {
        log.info("===>TestRunner run<===");
        //try {
        //    activeMqClient.subscribeConnection(connect);
        //    activeMqClient.subscribeMqtt(CommonContant.OTA_SUBSCRIBE_RESP,respCallBack);
        //    activeMqClient.subscribeMqtt(CommonContant.OTA_SUBSCRIBE_DEVICEINFO,deviceInfoCallBack);
        //}catch (Exception e){
        //    log.error("subscribe failed:" , e);
        //}
        log.info("===>TestRunner end<===");
    }
}
