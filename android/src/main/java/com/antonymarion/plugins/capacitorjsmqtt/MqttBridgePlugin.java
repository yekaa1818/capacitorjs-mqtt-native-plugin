package com.antonymarion.plugins.capacitorjsmqtt;

import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.eclipse.paho.mqttv5.common.MqttException;


// Define a Capacitor plugin with the name "MqttBridge"
@CapacitorPlugin(name = "MqttBridge")
public class MqttBridgePlugin extends Plugin {

    // Create an instance of MqttBridge to handle MQTT functionality
    private MqttBridge mqttBridge;

    // This method is called when the plugin is loaded
    @Override
    public void load() {
        super.load();
        // Initialize the MqttBridge instance with the plugin's context and a reference to the plugin itself
        mqttBridge = new MqttBridge(getContext(), this);
    }

    // This method is called when the plugin is being destroyed
    @Override
    public void handleOnDestroy() {
        // Disconnect the MQTT client
        try {
            mqttBridge.disconnect(null);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        super.handleOnDestroy();
    }

    // This method is a Capacitor plugin method that connects the MQTT client
    @PluginMethod
    public void connect(PluginCall call) throws MqttException {
        mqttBridge.connect(call);
    }

    // This method is a Capacitor plugin method that disconnects the MQTT client
    @PluginMethod
    public void disconnect(PluginCall call) throws MqttException {
        mqttBridge.disconnect(call);
    }

    // This method is a Capacitor plugin method that subscribes the MQTT client to a topic
    @PluginMethod
    public void subscribe(PluginCall call) {
        mqttBridge.subscribe(call);
    }

    // This method is a Capacitor plugin method that unsubscribes the MQTT client from a topic
    @PluginMethod
    public void unsubscribe(PluginCall call) {
        //mqttBridge.unsubscribe(call);
    }

    // This method is a Capacitor plugin method that publishes a message to an MQTT topic
    @PluginMethod
    public void publish(PluginCall call) {
        mqttBridge.publish(call);
    }

    // This method is called by MqttBridge when an event occurs (e.g. a message is received)
    // and notifies the Capacitor plugin's listeners with the event name and value
    @PluginMethod
    public void handleCallback(String eventName, JSObject val) {
        try {
            notifyListeners(eventName, val);
        } catch (Exception e) {
            Log.e("MqttBridgePlugin", "Error in handleCallback", e);
        }
    }
}
