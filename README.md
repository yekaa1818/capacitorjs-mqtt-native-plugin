# CapacitorJS MQTT Native Plugin

⚡️ This plugin enabzles CapacitorJS-powered Android mobile apps to connect to an MQTT broker and send/receive messages natively using TCP protocol.

#### ⚠️ Note: Supports only for android for now.

## Installation

To install the plugin, run:

```bash
npm install capacitor-mqtt-native-plugin
npx cap sync
```

## Examples

Here are some examples of how to use the plugin in your capacitorJS project using Typescript:

- [Connect to an MQTT Broker](#connect-to-broker)
- [Disconnecting from the MQTT Broker](#disconnect-from-broker)
- [Subscribing to an MQTT Topic](#subscribe-to-topic)
- [Publishing a Message to an MQTT Topic](#publish-to-topic)
- [Listen to Incoming Messages](#message-arrived-event)
- [Listen to ConnectComplete Event](#connect-complete-event)
- [Listen to ConnectionLost Event](#connection-lost-event)

### Connect to an MQTT Broker : <a name="connect-to-broker"></a>

To connect to an MQTT broker, you can use the `connect()` method provided by the plugin. The following code demonstrates how to connect to an MQTT broker:

```typescript
import { MqttBridge } from 'capacitor-mqtt-native-plugin';

// Set the MQTT server connection options
const connectionOptions = {
  serverURI: 'tcp://', // MQTT broker URI
  port: 1883, // MQTT broker default port
  clientId: 'your_mqtt_clientId', // client ID for connection
  username: 'your_mqtt_broker_username', // MQTT broker username
  password: 'your_mqtt_broker_password', // MQTT broker password
  setCleanSession: true, // clean session option
  connectionTimeout: 30, // connection timeout in seconds
  keepAliveInterval: 60, // keep alive interval in seconds
  setAutomaticReconnect: true, // automatic reconnect option
};

// connect to MQTT broker with options
MqttBridge.connect(connectionOptions)
  .then(() => {
    // connection successful
    console.log('Connect Success');
  })
  .catch((error: any) => {
    // connection failed with error message
    console.log('Connect Failed:', error);
  });
```

you can also add optional connect options parameter: `lastWill` to the `connectOptions`:

```typescript
  setLastWill: {
    willTopic: "your_last_will_topic",
    willPayload: "your_last_will_message",
    willQoS: "your_last_will_QoS",
    setRetained: true,
  }
```

### Disconnecting from the MQTT Broker : <a name="disconnect-from-broker"></a>

To disconnect from the MQTT broker, you can use the `disconnect()` method provided by the plugin. The following code demonstrates how to disconnect from an MQTT broker:

```typescript
import { MqttBridge } from 'capacitor-mqtt-native-plugin';

// Disconnect from the MQTT broker
MqttBridge.disconnect()
  .then(() => {
    // The disconnection is successful
    console.log('Successfully disconnected from the MQTT broker');
  })
  .catch((errorMessage: any) => {
    // The disconnection fails
    console.log(
      'Failed to disconnect from the MQTT broker. Error:',
      errorMessage,
    );
  });
```

### Subscribing to an MQTT Topic : <a name="#subscribe-to-topic"></a>

To subscribe to an MQTT topic, you can use the `subscribe()` method provided by the plugin. The following code demonstrates how to subscribe to an MQTT topic:

```typescript
import { MqttBridge } from 'capacitor-mqtt-native-plugin';

// Define the topic, qos
const topic = 'your_mqtt_topic';
const qos = 0;

// Subscribe to an MQTT topic
MqttBridge.subscribe({ topic, qos })
  // The subscription is successful
  .then((result: any) => {
    console.log('Successfully subscribed to topic:');
    console.log('Topic:', result.topic);
    console.log('QoS:', result.qos);
  })
  // The subscription fails
  .catch((errorMessage: any) => {
    console.log('Failed to subscribe to topic. Error:', errorMessage);
  });
```

### Publishing a Message to an MQTT Topic : <a name="publish-to-topic"></a>

To publish a message to an MQTT topic, you can use the `publish()` method provided by the plugin. The following code demonstrates how to publish a message to an MQTT topic:

```typescript
import { MqttBridge } from 'capacitor-mqtt-native-plugin';

// Define the topic, payload, qos, and retained properties for the message
const topic = 'your_mqtt_topic';
const payload = 'your_mqtt_message';
const qos = 0;
const retained = false;

// Publish the message
MqttBridge.publish({ topic, payload, qos, retained })
  .then((result: any) => {
    // The message is published successfully
    console.log('Successfully published message:');
    console.log('Topic:', result.topic);
    console.log('QoS:', result.qos);
    console.log('Payload:', result.payload);
    console.log('Retained:', result.retained);
    console.log('Message ID:', result.messageId);
  })
  .catch((errorMessage: any) => {
    // The message fails to publish
    console.log('Failed to publish message. Error:', errorMessage);
  });
```

### Listen to Incoming Messages : <a name="message-arrived-event"></a>

To listen to incoming messages, you can add a CapacitorJS listener with this event name : `onMessageArrived`. The following code demonstrates how to publish a message to an MQTT topic:

```typescript
import { MqttBridge } from 'capacitor-mqtt-native-plugin';

// Listen to incoming MQTT messages
MqttBridge.addListener('onMessageArrived', (result: any) => {
  console.log('Received a new message:');
  console.log('Topic:', result.topic);
  console.log('Message:', result.message);
});
```

When a message arrives, the listener will be triggered and you can access the message topic and payload in the result parameter. You can modify the code to suit your use case and do something more interesting with the incoming messages.

### Listen to ConnectComplete Event : <a name="connect-complete-event"></a>

This event is triggered only when the connection to the MQTT broker is successfully completed. It also triggers when the client was reconnected after a connection loss. To implement this, you can add a CapacitorJS listener with the event name : `onConnectComplete`. The following code demonstrates how to listen to the ConnectComplete event:

```typescript
import { MqttBridge } from 'capacitor-mqtt-native-plugin';

// Listen for the 'onConnectComplete' event
MqttBridge.addListener('onConnectComplete', (result: any) => {
  console.log('Successfully connected to MQTT broker:');
  console.log('Reconnected:', result.reconnected);
  console.log('Server URI:', result.serverURI);
});
```

### Listen to ConnectionLost Event : <a name="connection-lost-event"></a>

This event is triggered only when the client loses the connection to the MQTT broker. To handle this event, you can add a CapacitorJS listener with the event name : `onConnectionLost`. The following code demonstrates how to listen to ConnectionLost event:

```typescript
import { MqttBridge } from 'capacitor-mqtt-native-plugin';

// Add a listener for when the connection is lost
MqttBridge.addListener('onConnectionLost', (result: any) => {
  console.log('Connection lost:');
  console.log('Connection status:', result.connectionStatus);
  console.log('Reason code:', result.reasonCode);
  console.log('Message:', result.message);
});
```

The event listener function receives an object result as an argument with the following properties:

- **connectionStatus:** The status of the connection at the time the event was triggered.
- **reasonCode:** The MQTT reason code for the connection loss.
- **message:** Additional information about the connection loss.

#

## API

<docgen-index>

* [`connect(...)`](#connect)
* [`disconnect()`](#disconnect)
* [`subscribe(...)`](#subscribe)
* [`publish(...)`](#publish)
* [`addListener('onConnectionLost', ...)`](#addlisteneronconnectionlost-)
* [`addListener('onConnectComplete', ...)`](#addlisteneronconnectcomplete-)
* [`addListener('onMessageArrived', ...)`](#addlisteneronmessagearrived-)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### connect(...)

```typescript
connect(options: { serverURI: string; port: number; clientId: string; username: string; password: string; setCleanSession: boolean; connectionTimeout: number; keepAliveInterval: number; setAutomaticReconnect: boolean; setLastWill?: { willTopic: string; willPayload: string; willQoS: number; setRetained: boolean; }; }) => Promise<void>
```

| Param         | Type                                                                                                                                                                                                                                                                                                                      |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ serverURI: string; port: number; clientId: string; username: string; password: string; setCleanSession: boolean; connectionTimeout: number; keepAliveInterval: number; setAutomaticReconnect: boolean; setLastWill?: { willTopic: string; willPayload: string; willQoS: number; setRetained: boolean; }; }</code> |

--------------------


### disconnect()

```typescript
disconnect() => Promise<void>
```

--------------------


### subscribe(...)

```typescript
subscribe(options: { topic: string; qos: number; }) => Promise<{ topic: string; qos: number; }>
```

| Param         | Type                                         |
| ------------- | -------------------------------------------- |
| **`options`** | <code>{ topic: string; qos: number; }</code> |

**Returns:** <code>Promise&lt;{ topic: string; qos: number; }&gt;</code>

--------------------


### publish(...)

```typescript
publish(options: { topic: string; payload: string; qos: number; retained: boolean; correlationData?: string; }) => Promise<{ topic: string; payload: string; qos: number; retained: boolean; messageId: string; }>
```

| Param         | Type                                                                                                       |
| ------------- | ---------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ topic: string; payload: string; qos: number; retained: boolean; correlationData?: string; }</code> |

**Returns:** <code>Promise&lt;{ topic: string; payload: string; qos: number; retained: boolean; messageId: string; }&gt;</code>

--------------------


### addListener('onConnectionLost', ...)

```typescript
addListener(eventName: 'onConnectionLost', listener: onConnectionLostListener) => Promise<PluginListenerHandle> & Partial<PluginListenerHandle>
```

| Param           | Type                                                                          |
| --------------- | ----------------------------------------------------------------------------- |
| **`eventName`** | <code>'onConnectionLost'</code>                                               |
| **`listener`**  | <code><a href="#onconnectionlostlistener">onConnectionLostListener</a></code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#partial">Partial</a>&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('onConnectComplete', ...)

```typescript
addListener(eventName: 'onConnectComplete', listener: onConnectCompleteListener) => Promise<PluginListenerHandle> & Partial<PluginListenerHandle>
```

| Param           | Type                                                                            |
| --------------- | ------------------------------------------------------------------------------- |
| **`eventName`** | <code>'onConnectComplete'</code>                                                |
| **`listener`**  | <code><a href="#onconnectcompletelistener">onConnectCompleteListener</a></code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#partial">Partial</a>&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('onMessageArrived', ...)

```typescript
addListener(eventName: 'onMessageArrived', listener: onMessageArrivedListener) => Promise<PluginListenerHandle> & Partial<PluginListenerHandle>
```

| Param           | Type                                                                          |
| --------------- | ----------------------------------------------------------------------------- |
| **`eventName`** | <code>'onMessageArrived'</code>                                               |
| **`listener`**  | <code><a href="#onmessagearrivedlistener">onMessageArrivedListener</a></code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt; & <a href="#partial">Partial</a>&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


### Type Aliases


#### Partial

Make all properties in T optional

<code>{ [P in keyof T]?: T[P]; }</code>


#### onConnectionLostListener

<code>(x: { connectionStatus: string; reasonCode: number; message: string; }): void</code>


#### onConnectCompleteListener

<code>(x: { reconnected: boolean; serverURI: string; }): void</code>


#### onMessageArrivedListener

<code>(x: { topic: string; message: string; correlationData?: string; }): void</code>

</docgen-api>
