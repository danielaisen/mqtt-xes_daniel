/**
 * @author Daniel Max Aisen (s171206)
 **/

package mqttxes.lib;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientBuilder;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

public class UpdatedPublisher {

    private final Mqtt5AsyncClient client;
    private String topicBase;


    public UpdatedPublisher(String topic) { //TODO change server host to the String MAYBE also topic
        topicBase = topic;
        MqttClientBuilder clientBuilder = MqttClient.builder()
                .identifier("Bachelor_project")
                .serverHost("broker.hivemq.com")
//                .serverHost("127.0.0.1")

                .serverPort(1883)
                ;

        Mqtt5Client client = clientBuilder.useMqttVersion5().build();
        this.client= client.toAsync();

    }

    public static String checkForWildCards(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '+' ||
                    string.charAt(i) == '#' ||
                    string.charAt(i) == '/' ||
                    string.charAt(i) == '$') {
                System.out.println();
                System.out.println("found wild Cards!!! ");
                System.out.println();
            }
        }
        string = string.replace("+"," PLUS ");
        string = string.replace("/"," DASH ");
        string = string.replace("#"," NUMBER-SIGN ");
        string = string.replace("$"," DOLLAR-SIGN ");
        return string;
    }

//    public UpdatedPublisher(String topic, String bro)


    public void send(String message){ //todo check if the connection in on prior sending. check if the broker has disconnected
        //todo how do i specify which metaData is sent? send it
//        client.publishWith()
//                .topic("test/topic")
//                .qos(MqttQos.AT_LEAST_ONCE)
//                .payload("payload".getBytes())
//                .contentType("text/plain")
//                .send();
        client.connect();
        client.publishWith()
                .topic(topicBase)
                .contentType("text/plain")
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(("hello mqtt start with 1, now " + message ).getBytes())
                .send();
//        client.disconnect();
    }

    public void connect() { //todo set a session expiry interval
        //todo have a print/check for Unsuccessful reason code / reason Strings
//        this.client.connectWith().cleanStart(false)
//                .willPublish()
//                .topic("test/topic")
//                .qos(MqttQos.AT_LEAST_ONCE)
//                .payload("payload".getBytes())
//                .retain(true)
//                .applyWillPublish();

        client.connect(); //todo set the client to connect first time with a clean start
    }
    public void disconnect(){
        client.disconnectWith(); //todo the result is Warning:(74, 16) Result of 'Mqtt5AsyncClient.disconnectWith()' is ignored
    }

    public void sendConnect() {
        client.publishWith().topic("test/topic").send();
    }

    public void send(XesMqttEvent event) {
//        CompletableFuture<Mqtt5ConnAck> connAckFuture = client.connect();
        String caseID = checkForWildCards(event.getCaseId());

        String processName = checkForWildCards(event.getProcessName());
        String activityName = checkForWildCards(event.getActivityName());
        client.publishWith().
                topic(
                topicBase + "/" +
                        processName + "/" +
                        caseID + "/" +
                        activityName)
                .contentType("text/plain")
                .qos(MqttQos.EXACTLY_ONCE)
                .payload(event.getAttributes().getBytes())
                .send();

    }
}

