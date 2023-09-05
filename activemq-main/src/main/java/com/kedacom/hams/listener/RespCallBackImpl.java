package com.kedacom.hams.listener;

import com.alibaba.fastjson.JSONObject;
import com.kedacom.hams.callback.IMessageCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


/**
 * @author : lzp
 * @date : 2023/9/5 15:14
 * @apiNote : 响应回调
 */
@Component
@Slf4j
public class RespCallBackImpl implements IMessageCallback {
    @Override
    public void messageArrived(String message) {

        log.info("RespListening message:" + message);
        if (StringUtils.isEmpty(message)) {
            return;
        }
        //解析数据
        JSONObject object = JSONObject.parseObject(message);
        String cmd = object.getString("cmd");
        String sn = object.getString("sn");
        String taskId = object.getString("taskId");
        JSONObject paramObject = object.getJSONObject("param");
        String failDes = paramObject.getString("failDes");
        String status = paramObject.getString("status");
        if (!"otaUpgradeResp".equals(cmd)) {
            return;
        }
        log.info("sn:{} taskid:{} failDes:{} status:{}",sn,taskId,failDes,status);
    }
}
