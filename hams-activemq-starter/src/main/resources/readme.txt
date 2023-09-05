===============================================
@Autowired
    HamsActiveMqClient hamsActiveMqClient;

    @Autowired
    HaPersonService haPersonService;

    @PostConstruct
    public void test() {

        try {
            hamsActiveMqClient.subscribeConnection(new IConnectionCallback() {
                @Override
                public void onLine(ClientInfo clientInfo) {
                    log.info("online: " + clientInfo.toString());
                }

                @Override
                public void offLine(ClientInfo clientInfo) {
                    log.info("offline: " + clientInfo.toString());
                }
            });
        } catch (com.kedacom.hams.utils.ServiceException e) {
            e.printStackTrace();
        }
        try {
            hamsActiveMqClient.subscribeMqtt("/e/default/rp/d/plat", new IMessageCallback() {

                @Override
                public void messageArrived(String message) {
                    log.info("HaPersonController.messageArrived:message = " + message);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //config
    hams.activemq.brokerIp=172.16.249.140
    hams.activemq.brokerPort=61616
    hams.activemq.mqttPort=1883
    hams.activemq.username=admin
    hams.activemq.password=123456
===============================================