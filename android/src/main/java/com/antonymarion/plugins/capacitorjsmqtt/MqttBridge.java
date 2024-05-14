package com.antonymarion.plugins.capacitorjsmqtt;

import android.content.Context;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttCallback;


public class MqttBridge {

    // A constant string used as a tag for logging
    private static final String TAG = "MqttBridge";

    // An instance of the MqttBridgePlugin class
    private MqttBridgePlugin pluginInstance;

    // An instance of the MqttAndroidClient class, which represents an MQTT client for Android
    private MqttClient mqttClient;

    // A Context object representing the context in which the MQTT bridge is running
    private Context context;

    // A string representing the URI of the MQTT server to connect to
    private String serverURI;

    // A string representing the ID of the MQTT client
    private String clientId;

    // A string representing the port number to connect to on the MQTT server
    private String port;

    // A string representing the username to use for authentication when connecting to the MQTT server
    private String username;

    // A string representing the password to use for authentication when connecting to the MQTT server
    private String password;

    // A boolean flag indicating whether the MQTT client is currently connecting to the server
    private boolean isConnecting = false;

    /**
     * Constructor for the MqttBridge class
     *
     * @param context        the context in which the MQTT bridge is running
     * @param pluginInstance an instance of the MqttBridgePlugin class
     */
    public MqttBridge(Context context, MqttBridgePlugin pluginInstance) {
        this.context = context;
        this.pluginInstance = pluginInstance;
    }

    public void connect(final PluginCall call) throws MqttException {
        // Extract necessary information from the PluginCall data
        JSObject dataFromPluginCall = call.getData();
        String serverURI = dataFromPluginCall.getString("serverURI");
        String port = dataFromPluginCall.getString("port");
        String clientId = dataFromPluginCall.getString("clientId");
        String username = dataFromPluginCall.getString("username");
        String password = dataFromPluginCall.getString("password");

        // Extract optional information from the PluginCall data with default values
        boolean setCleanStart = dataFromPluginCall.getBoolean("setCleanStart", false);
        int connectionTimeout = dataFromPluginCall.getInteger("connectionTimeout", 30);
        int keepAliveInterval = dataFromPluginCall.getInteger("keepAliveInterval", 60);
        boolean setAutomaticReconnect = dataFromPluginCall.getBoolean("setAutomaticReconnect", true);

        // Validate the required fields
        if (serverURI == null || serverURI.isEmpty()) {
            call.reject("serverURI is required");
            return;
        }

        if (port == null || port.isEmpty()) {
            call.reject("port number is required");
            return;
        }

        if (connectionTimeout == 0) {
            call.reject(
                    "Invalid connection timeout value. Please provide a non-zero value, otherwise your MQTT client connection cannot be established."
            );
            return;
        }

        if (keepAliveInterval == 0) {
            call.reject(
                    "Invalid keep alive interval value. Please provide a non-zero value, otherwise your MQTT client connection may timeout or disconnect unexpectedly."
            );
            return;
        }

        // If clientId is null or empty, generate a random clientId
        if (clientId == null || clientId.isEmpty()) {
            clientId = UUID.randomUUID().toString();
        }

        // Construct the full server URI
        String fullURI = serverURI + ":" + port;

        // Create and configure the MqttConnectionOptions object
        MqttConnectionOptions MqttConnectionOptions = new MqttConnectionOptions();
        MqttConnectionOptions.setCleanStart(setCleanStart);
        MqttConnectionOptions.setConnectionTimeout(connectionTimeout);
        MqttConnectionOptions.setKeepAliveInterval(keepAliveInterval);
        MqttConnectionOptions.setAutomaticReconnect(setAutomaticReconnect);
        MqttConnectionOptions.setUserName(username);
        MqttConnectionOptions.setPassword(password.getBytes());

        // Set the last will message if it exists
        JSObject lastWillObj = dataFromPluginCall.getJSObject("setLastWill");
        if (lastWillObj != null) {
            String willTopic = lastWillObj.getString("willTopic");
            String willPayload = lastWillObj.getString("willPayload");
            int willQoS = lastWillObj.getInteger("willQoS");
            boolean setRetained = lastWillObj.getBoolean("setRetained", false);

            MqttMessage willMessage = new MqttMessage(willPayload.getBytes(), willQoS, setRetained, null);
            MqttConnectionOptions.setWill(willTopic, willMessage);
        }

        // Check if the client is not already connecting and the client is not already connected
        if (!isConnecting && (mqttClient == null || !mqttClient.isConnected())) {
            // Set isConnecting to true to avoid multiple connect requests
            isConnecting = true;

            //  if (!dataDir.canWrite()) {
            //      throw new MqttPersistenceException();
            //  }

            // Create the MqttAndroidClient object
            // https://github.com/eclipse/paho.mqtt.android/issues/272
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(fullURI, clientId, persistence);
            // Set this as the callback for the MQTT client
            mqttClient.setCallback(new MqttCallback() {
                public void disconnected(MqttDisconnectResponse disconnectResponse) {
                    // Create a JSObject to hold the connection lost data
                    JSObject data = new JSObject();
                    String message = "Client disconnected ";

                    message += disconnectResponse.getReasonString();
                    int reasonCode = disconnectResponse.getReturnCode();

                    data.put("connectionStatus", "disconnected");
                    data.put("reasonCode", reasonCode);
                    data.put("message", message);

                    // Call the handleCallback method of the pluginInstance with the connection lost data
                    pluginInstance.handleCallback(Constants.CONNECTION_LOST_EVENT_NAME, data);

                    // Print the message to the log for debugging purposes
                    Log.d("MQTT", message);
                }

                public void mqttErrorOccurred(MqttException exception) {
                    Log.d("MQTT", "Error: " + exception.getMessage());
                }

                public void messageArrived(String topic, MqttMessage message) {
                    // Create a JSObject to hold the message data
                    JSObject data = new JSObject();
                    data.put("topic", topic);
                    data.put("message", message.toString());
                    data.put("correlationData", message.getProperties().getCorrelationData().toString());
                    data.put("responseTopic", message.getProperties().getResponseTopic().toString());

                    // Call the handleCallback method of the pluginInstance with the message data
                    pluginInstance.handleCallback(Constants.MESSAGE_ARRIVED_EVENT_NAME, data);

                    // Print the message to the log for debugging purposes
                    Log.d("MQTT", "Message arrived on topic " + topic + ": " + message.toString());
                }

                public void deliveryComplete(IMqttToken token) {
                    Log.d("MQTT", "Delivery completed for token " + token.toString());
                }

                public void connectComplete(boolean reconnect, String serverURI) {
                    JSObject data = new JSObject();
                    data.put("reconnected", reconnect);
                    data.put("serverURI", serverURI);

                    pluginInstance.handleCallback(Constants.CONNECT_COMPLETE_EVENT_NAME, data);
                }

                public void authPacketArrived(int reasonCode, MqttProperties properties) {
                    Log.d("MQTT", "Auth. packet arrived");
                }
            });

            try {
                // Attempt to connect to the MQTT broker using the provided options
                mqttClient.connect(MqttConnectionOptions);
                // Set isConnecting to false to allow future connect requests
                isConnecting = false;
                // Resolve the PluginCall to signal successful connection
                call.resolve();
            } catch (MqttException exception) {
                // set isConnecting to false to indicate that the connection attempt has failed
                isConnecting = false;

                // Get the error message from the Throwable object
                String errorMessage = exception.getMessage();

                // Reject the plugin call with an error message containing the error message obtained from the Throwable object
                call.reject("Failed to connect to MQTT broker: " + errorMessage);
            }
        }
    }

    public void disconnect(final PluginCall call) throws MqttException {
        if (mqttClient.isConnected()) {
            try {
                mqttClient.disconnect();
                call.resolve();
            } catch (MqttException exception) {
                call.reject(exception.getMessage());
            }
        }
    }

    public void subscribe(final PluginCall call) {
        // Check if MQTT client is connected
        if (mqttClient == null || !mqttClient.isConnected()) {
            call.reject("MQTT client is not connected");
            return;
        }

        // Get topic and qos from the plugin call
        final String topic = call.getString("topic");
        final int qos = call.getInt("qos", 0);

        // Subscribe to the MQTT topic with the given qos
        try {
            mqttClient.subscribe(topic,qos);
            // Create a JSObject to return the subscribed topic and qos
            JSObject data = new JSObject();
            data.put("topic", topic);
            data.put("qos", qos);

            // Resolve the plugin call with the data object
            call.resolve(data);
        } catch (MqttException exception) {
            // Reject the plugin call with an error message
            call.reject("Failed to subscribe to topic: " + topic);
        }
    }

    public void publish(final PluginCall call) {
        // Check if the MQTT client is not connected
        if (mqttClient == null || !mqttClient.isConnected()) {
            call.reject("MQTT client is not connected");
            return;
        }

        // Obtain the topic, qos, retained, and payload from the PluginCall object
        final String topic = call.getString("topic");
        final String correlationData = call.getString("correlationData");
        final int qos = call.getInt("qos", 0);
        final boolean retained = call.getBoolean("retained", false);
        final String payload = call.getString("payload");

        // Create an MqttMessage object with the payload
        MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        // Set the qos and retained flag of the message
        message.setQos(qos);
        message.setRetained(retained);

        MqttProperties properties = new MqttProperties();
        properties.setCorrelationData(correlationData.getBytes());
        message.setProperties(properties);

        try {
            // Publish the message to the topic using the mqttClient object
            mqttClient.publish(topic, message);
            // Construct a JSObject with the topic, qos, retained, payload, and messageId
            JSObject data = new JSObject();
            data.put("topic", topic);
            data.put("qos", qos);
            data.put("correlationData", correlationData);
            data.put("retained", retained);
            data.put("payload", payload);
            // Resolve the PluginCall with the JSObject
            call.resolve(data);
        } catch (MqttException exception){
            // Reject the PluginCall with an error message
            call.reject("Failed to publish message to topic: " + topic);
        }
    }




}
