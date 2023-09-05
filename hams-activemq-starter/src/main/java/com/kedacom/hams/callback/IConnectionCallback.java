package com.kedacom.hams.callback;

import com.kedacom.hams.config.ClientInfo;

public interface IConnectionCallback {

    public void onLine(ClientInfo clientInfo);

    public void offLine(ClientInfo clientInfo);
}
