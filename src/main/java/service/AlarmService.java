package service;

import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import utils.Constants;

public class AlarmService implements MqttCallbackExtended{
	
	MqttClient client;
	MqttConnectOptions mqOptions;
	static ArrayList<Double> listTwoLastTemperatureAverage;
	int qos = 1;
	
	public static void main(String[] args) throws MqttException, InterruptedException {	
		listTwoLastTemperatureAverage = new ArrayList<Double>();
		
		AlarmService alarmService = new AlarmService();
		alarmService.run();
		
		
	}
	
	public void run() throws MqttException {
		client = new MqttClient(Constants.broker,"alarm");
	    client.setCallback(this);
	    
	    mqOptions=new MqttConnectOptions();
	    mqOptions.setCleanSession(true);
	    
	    client.connect(mqOptions);  
	    System.out.println("Alarm Service is ON!!!");
	    client.subscribe(Constants.topicCatAverageTemperature); 		
	}

	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		listTwoLastTemperatureAverage.add(Double.parseDouble(message.toString()));
		
		
		if(listTwoLastTemperatureAverage.size()==2) {
			Double differenceTemperatureAverage = listTwoLastTemperatureAverage.get(0)-listTwoLastTemperatureAverage.get(1);
			if(differenceTemperatureAverage==5.0) {
			
				MqttTopic topic1 = client.getTopic(Constants.topicName);   			    
				
			    MqttMessage message1 = new MqttMessage("Aumento de temperatura repentina".getBytes());
				message1.setQos(Constants.qos);   
				message1.setRetained(true); 
				topic1.publish(message1);    
				message1.clearPayload();
				
			}
			listTwoLastTemperatureAverage.clear();			
			
		}
		
		if(Double.parseDouble(message.toString())>200) {					

			MqttTopic topic1 = client.getTopic(Constants.topicName);   
		    
			
		    MqttMessage message1 = new MqttMessage("Temperatura alta".getBytes());
			message1.setQos(Constants.qos);    
			message1.setRetained(true); 
			topic1.publish(message1);    
			message1.clearPayload();
		}
		
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

	public void connectComplete(boolean reconnect, String serverURI) {
		// TODO Auto-generated method stub
		
	}

}
