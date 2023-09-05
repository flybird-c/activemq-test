package com.kedacom.hams.activemq;

import com.kedacom.hams.callback.IConnectionCallback;
import com.kedacom.hams.callback.IMessageCallback;
import com.kedacom.hams.common.ErrorDef;
import com.kedacom.hams.config.ClientInfo;
import com.kedacom.hams.config.HamsActiveMqProperties;
import com.kedacom.hams.utils.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.DataStructure;
import org.apache.activemq.command.RemoveInfo;
import org.eclipse.paho.client.mqttv3.MqttException;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HamsActiveMqClient {

    private Connection connection = null;

    private HamsActiveMqProperties hamsActiveMqProperties;

    private Map<String, ClientInfo> clientInfoMap = new HashMap<>();

    private HamsMqttClient hamsMqttClient;

    public HamsActiveMqClient(HamsActiveMqProperties activeMqProperties) {
        this.hamsActiveMqProperties = activeMqProperties;
        init();
    }

    public void init() {
        try {
            //1.创建连接工厂
            String brokerURL = "tcp://" + hamsActiveMqProperties.getBrokerIp() + ":" + "61616";
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(hamsActiveMqProperties.getUsername(),
                    hamsActiveMqProperties.getPassword(), brokerURL);
            //2.获取连接
            connection = connectionFactory.createConnection();
            //3.启动连接
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
            log.error("HamsActiveMqClient.HamsActiveMqClient:init() e = " + e);
        }
    }


    public void setMqttClient(HamsMqttClient hamsMqttClient) {
        this.hamsMqttClient = hamsMqttClient;
    }

    /**
     * 根据clientId, 获取所有在线的终端
     * @param clientId
     * @return
     */
    public ClientInfo getOnlineClient(String clientId) {
        for (String key : clientInfoMap.keySet()) {
            if (key.equals(clientId)) {
                return clientInfoMap.get(key);
            }
        }
        return null;
    }


    /**
     * 订阅上下线事件
     * @param connectionCallback
     * @throws ServiceException
     */
    public void subscribeConnection(IConnectionCallback connectionCallback) throws ServiceException {
        subscribe("ActiveMQ.Advisory.Connection", message -> {
            log.info("msg: " + message.toString());
            if (message instanceof ActiveMQMessage) {
                ActiveMQMessage msg = (ActiveMQMessage) message;
                DataStructure dataStructure = msg.getDataStructure();
                if (dataStructure instanceof ConnectionInfo) {
                    ConnectionInfo connectionInfo = (ConnectionInfo) dataStructure;
                    log.info("connectionInfo: " + connectionInfo.toString());
                    ClientInfo clientInfo = new ClientInfo();
                    clientInfo.setClientIp(connectionInfo.getClientIp());
                    clientInfo.setClientId(connectionInfo.getClientId());
                    clientInfo.setUserName(connectionInfo.getUserName());
                    clientInfo.setConnectionId(connectionInfo.getConnectionId().toString());
                    clientInfoMap.put(clientInfo.getConnectionId(), clientInfo);
                    connectionCallback.onLine(clientInfo);
                } else if (dataStructure instanceof RemoveInfo) {
                    RemoveInfo removeInfo = (RemoveInfo) dataStructure;
                    log.info("removeInfo: " + removeInfo.toString());
                    ClientInfo clientInfo = clientInfoMap.get(removeInfo.getObjectId().toString());
                    if (clientInfo != null) {
                        connectionCallback.offLine(clientInfo);
                        clientInfoMap.remove(clientInfo);
                    }
                }
            }
        });

    }

    /**
     * 订阅activeMq 消息主题
     *
     * @param topicName
     * @param messageListener
     * @throws ServiceException
     */
    public void subscribe(String topicName, MessageListener messageListener) throws ServiceException {
        if (connection == null) {
            init();
        }

        try {
            // 1.获取session (参数1：是否启动事务,参数2：消息确认模式)
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // 2.创建主题
            Topic topic = session.createTopic(topicName);
            // 3.创建消息消费
            MessageConsumer consumer = session.createConsumer(topic);
            // 4.监听消息
            consumer.setMessageListener(messageListener);

        } catch (JMSException e) {
            e.printStackTrace();
            log.error("HamsActiveMqClient.consume:e = " + e);
            throw new ServiceException(ErrorDef.ERROR_MQ_INNER_EXCEPTION.getCode(), ErrorDef.ERROR_MQ_INNER_EXCEPTION.getMsg());
        }

    }

    /**
     * 发布activemq 主题
     *
     * @param topicName
     * @param jsonData
     * @throws ServiceException
     */
    public void produce(String topicName, String jsonData) throws ServiceException {
        if (connection == null) {
            init();
        }

        try {
            //1.获取session  (参数1：是否启动事务,参数2：消息确认模式)
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //2.创建主题对象
            Topic topic = session.createTopic(topicName);
            //3.创建消息生产者
            MessageProducer producer = session.createProducer(topic);
            //4.创建消息
            TextMessage textMessage = session.createTextMessage(jsonData);
            //5.发送消息
            producer.send(textMessage);
            //6.关闭资源
            producer.close();
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
            log.error("HamsActiveMqClient.produce:e = " + e);
            throw new ServiceException(ErrorDef.ERROR_MQ_INNER_EXCEPTION.getCode(), ErrorDef.ERROR_MQ_INNER_EXCEPTION.getMsg());
        }

    }

    /**
     * 发布mqtt消息
     *
     * @param topicName:发布的主题
     * @param message：发布的消息
     */
    public void publishMqtt(String topicName, String message) throws MqttException {
        hamsMqttClient.publish(topicName, message);
    }

    /**
     * 订阅mqtt主题
     *
     * @param topicName
     * @throws MqttException
     */
    public void subscribeMqtt(String topicName, IMessageCallback messageCallback) throws MqttException {
        hamsMqttClient.setMessageCallBack(topicName, messageCallback);
        hamsMqttClient.subscribe(topicName);
    }


}
