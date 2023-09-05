package com.kedacom.hams.listener;

import com.alibaba.fastjson.JSONObject;
import com.kedacom.hams.callback.IMessageCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author : lzp
 * @date : 2023/9/5 15:33
 * @apiNote : deviceInfo回调
 */
@Slf4j
@Component
public class DeviceInfoCallBackImpl implements IMessageCallback {
    @Override
    public void messageArrived(String message) {

        log.info("DevInfoListening message:" + message);
        if (StringUtils.isEmpty(message)) {
            log.info("DevInfoListening message is null!");
            return;
        }
        JSONObject object = JSONObject.parseObject(message);
        String cmd = object.getString("cmd");
        if (!"deviceInfo".equals(cmd)) {
            return;
        }
    }
}
