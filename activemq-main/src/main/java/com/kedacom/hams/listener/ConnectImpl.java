package com.kedacom.hams.listener;

import com.kedacom.hams.callback.IConnectionCallback;
import com.kedacom.hams.config.ClientInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : lzp
 * @date : 2023/9/5 15:36
 * @apiNote : 连接回调
 */
@Slf4j
@Component
public class ConnectImpl implements IConnectionCallback {
    @Override
    public void onLine(ClientInfo clientInfo) {
        log.info("上线:{}",clientInfo);
    }

    @Override
    public void offLine(ClientInfo clientInfo) {
        log.info("下线:{}",clientInfo);
    }
}
