package mqttxes.lib;

import java.util.List;
import java.util.function.Consumer;

import org.json.simple.parser.ParseException;

import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

/**
 * This class provides the callback method to handle new events being received
 * 
 * @author Andrea Burattin
 */
public abstract class XesMqttEventCallback implements Consumer<Mqtt5Publish> {

	/**
	 * This method has to be implemented by subclasses to properly process the
	 * event received
	 * 
	 * @param event
	 */
	public abstract void accept(XesMqttEvent event);
	
	@Override
	public void accept(Mqtt5Publish t) {
		List<String> l = t.getTopic().getLevels();
		String activityName = l.get(l.size() - 1);
		String caseId = l.get(l.size() - 2);
		String processName = l.get(l.size() - 3);
		
		XesMqttEvent e = new XesMqttEvent(processName, caseId, activityName);
		try {
			e.setAttributes(new String(t.getPayloadAsBytes()));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		accept(e);
	}
}
