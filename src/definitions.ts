import type { PluginListenerHandle } from '@capacitor/core';

// Define type for the "onConnectionLost" event listener
export type onConnectionLostListener = (x: {
  connectionStatus: string;
  reasonCode: number;
  message: string;
}) => void;

// Define type for the "onConnectComplete" event listener
export type onConnectCompleteListener = (x: {
  reconnected: boolean;
  serverURI: string;
}) => void;

// Define type for the "onMessageArrived" event listener
export type onMessageArrivedListener = (x: {
  topic: string;
  message: string;
  correlationData?: string;
}) => void;

// Define the interface for the MqttBridgePlugin
export interface MqttBridgePlugin {
  // Method to connect to an MQTT broker
  connect(options: {
    serverURI: string;
    port: number;
    clientId: string;
    username: string;
    password: string;
    setCleanSession: boolean;
    connectionTimeout: number;
    keepAliveInterval: number;
    setAutomaticReconnect: boolean;
    setLastWill?: {
      willTopic: string;
      willPayload: string;
      willQoS: number;
      setRetained: boolean;
    };
  }): Promise<void>;

  // Method to disconnect from the MQTT broker
  disconnect(): Promise<void>;

  // Method to subscribe to an MQTT topic
  subscribe(options: {
    topic: string;
    qos: number;
  }): Promise<{ topic: string; qos: number }>;

  // Method to publish an MQTT message to a topic
  publish(options: {
    topic: string;
    payload: string;
    qos: number;
    retained: boolean;
    correlationData?: string;
  }): Promise<{
    topic: string;
    payload: string;
    qos: number;
    retained: boolean;
    messageId: string;
  }>;

  // Method to add an event listener for the "onConnectionLost" event
  addListener(
    eventName: 'onConnectionLost',
    listener: onConnectionLostListener,
  ): Promise<PluginListenerHandle> & Partial<PluginListenerHandle>;

  // Method to add an event listener for the "onConnectComplete" event
  addListener(
    eventName: 'onConnectComplete',
    listener: onConnectCompleteListener,
  ): Promise<PluginListenerHandle> & Partial<PluginListenerHandle>;

  // Method to add an event listener for the "onMessageArrived" event
  addListener(
    eventName: 'onMessageArrived',
    listener: onMessageArrivedListener,
  ): Promise<PluginListenerHandle> & Partial<PluginListenerHandle>;
}
