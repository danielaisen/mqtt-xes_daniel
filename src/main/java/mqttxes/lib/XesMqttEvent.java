package mqttxes.lib;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.model.XAttributeMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import Helpers.DateHelper;

/**
 * Class that represents an event. Objects of this class can be used both to
 * send events to the broker (using the {@link XesMqttProducer}) and also when
 * events are received (using the {@link XesMqttConsumer} and the
 * {@link XesMqttEventCallback}).
 * 
 * @author Andrea Burattin
 */
public class XesMqttEvent {

	private static JSONParser parser = new JSONParser();

	private String processName;
	private String caseId;
	private String activityName;
	private Map<String, HashMap<String, String>> attributes = new HashMap<String, HashMap<String, String>>();

	public XesMqttEvent(String processName, String caseId, String activityName) {
		this.processName = processName;
		this.caseId = caseId;
		this.activityName = activityName;
	}

	public String getProcessName() {
		return processName;
	}

	public String getCaseId() {
		return caseId;
	}

	public String getActivityName() {
		return activityName;
	}

	public String getAttributes() {
		return new JSONObject(attributes).toJSONString();
	}

	public void setAttributes(String json) throws ParseException {
		attributes.clear();
		if (!json.isEmpty()) {
			JSONObject atts = (JSONObject) parser.parse(json);
			JSONObject traceAtts = (JSONObject) atts.get("trace");
			if (traceAtts != null) {
				for (Object key : traceAtts.keySet()) {
					addTraceAttribute(key.toString(), traceAtts.get(key).toString());
				}
			}
			JSONObject eventAtts = (JSONObject) atts.get("event");
			if (eventAtts != null) {
				for (Object key : eventAtts.keySet()) {
					addEventAttribute(key.toString(), eventAtts.get(key).toString());
				}
			}
		}
	}

	public XesMqttEvent addEventAttribute(String name, String value) {
		return addAttribute(name, value, "event");
	}

	public XesMqttEvent addTraceAttribute(String name, String value) {
		return addAttribute(name, value, "trace");
	}

	public XesMqttEvent addAttribute(String name, String value, String type) {
		if (!attributes.containsKey(type)) {
			attributes.put(type, new HashMap<String, String>());
		}
		attributes.get(type).put(name, value);
		return this;
	}

	public XesMqttEvent addAllEventAttributes(XAttributeMap map) {
		return addAllAttributes(map, "event");
	}

	public XesMqttEvent addAllTraceAttributes(XAttributeMap map) {
		return addAllAttributes(map, "trace");
	}

	public XesMqttEvent addAllAttributes(XAttributeMap map, String type) {
		for (String key : map.keySet()) {
			addAttribute(key, map.get(key).toString(), type);
		}
		return this;
	}

	public XesMqttEvent removeTraceAttribute(String name) {
		return removeAttribute(name, "trace");
	}

	public XesMqttEvent removeEventAttribute(String name) {
		return removeAttribute(name, "event");
	}

	public XesMqttEvent removeAttribute(String name, String type) {
		attributes.get(type).remove(name);
		return this;
	}

	public Date getTime() throws java.text.ParseException { //todo add a check that it is the right format
		HashMap<String, String> event = attributes.get("event");
		String time = event.get("time:timestamp");


		Date myDate = DateHelper.getDate(time);
//		if (time.length() == 29) {
//			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//			dateFormat.parse(time);
//			myDate = dateFormat.parse(time);
//		}
//		else{
//			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//			dateFormat.parse(time);
//			myDate = dateFormat.parse(time);
//		}
		return myDate;

	}

}
