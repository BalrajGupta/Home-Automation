#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <WiFiManager.h> // https://github.com/tzapu/WiFiManager
#include <ArduinoJson.h>

ESP8266WebServer server(80);

// Secret key for basic API access protection
const String SECRET_KEY = "123456";

// Relay Pins
const int relayPins[] = {5, 4, 0, 2, 14, 12, 13, 15};
const int numRelays = sizeof(relayPins) / sizeof(relayPins[0]);

// Function to check if a pin is a valid relay pin
bool isValidPin(int pin) {
  for (int i = 0; i < numRelays; i++) {
    if (relayPins[i] == pin) return true;
  }
  return false;
}

// Handler for root path
void handleRoot() {
  server.send(200, "application/json", "{\"status\":\"online\",\"device\":\"SmartHome-NodeMCU\"}");
}

// Handler for getting pin status
void handleStatus() {
  if (!server.hasArg("pin")) {
    server.send(400, "application/json", "{\"error\":\"Missing pin parameter\"}");
    return;
  }
  
  int pin = server.arg("pin").toInt();
  if (!isValidPin(pin)) {
    server.send(400, "application/json", "{\"error\":\"Invalid pin\"}");
    return;
  }

  // Read current state
  int state = digitalRead(pin);
  String stateStr = (state == HIGH) ? "ON" : "OFF";
  
  String json = "{\"status\":\"ok\",\"pin\":" + String(pin) + ",\"state\":\"" + stateStr + "\"}";
  server.send(200, "application/json", json);
}

// Handler for toggling a pin
void handleToggle() {
  if (!server.hasArg("pin")) {
    server.send(400, "application/json", "{\"error\":\"Missing pin parameter\"}");
    return;
  }
  
  if (!server.hasArg("key") || server.arg("key") != SECRET_KEY) {
    server.send(401, "application/json", "{\"error\":\"Unauthorized\"}");
    return;
  }

  int pin = server.arg("pin").toInt();
  if (!isValidPin(pin)) {
    server.send(400, "application/json", "{\"error\":\"Invalid pin\"}");
    return;
  }

  // Toggle state
  int currentState = digitalRead(pin);
  int newState = (currentState == LOW) ? HIGH : LOW;
  digitalWrite(pin, newState);

  String stateStr = (newState == HIGH) ? "ON" : "OFF";
  String json = "{\"status\":\"ok\",\"pin\":" + String(pin) + ",\"state\":\"" + stateStr + "\"}";
  server.send(200, "application/json", json);
}

void handleNotFound() {
  server.send(404, "application/json", "{\"error\":\"Not found\"}");
}

void setup() {
  Serial.begin(9600);
  Serial.println("Starting up...");

  // Initialize pins to LOW to ensure safety on boot
  for (int i = 0; i < numRelays; i++) {
    pinMode(relayPins[i], OUTPUT);
    digitalWrite(relayPins[i], LOW);
  }

  // WiFiManager - Dynamically configure Wi-Fi credentials
  WiFiManager wifiManager;
  
  // Uncomment and run once, if you want to erase all the stored information
  // wifiManager.resetSettings();

  // Set timeout for AP config
  wifiManager.setConfigPortalTimeout(180);

  // Starts an access point "SmartHome-Setup" for configuration with no password
  if (!wifiManager.autoConnect("SmartHome-Setup")) {
    Serial.println("Failed to connect and hit timeout");
    delay(3000);
    //reset and try again, or maybe put it to deep sleep
    ESP.restart();
    delay(5000);
  }

  Serial.println("Connected to Wi-Fi!");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());

  // Set up mDNS responder: esp8266.local
  if (MDNS.begin("esp8266")) {
    Serial.println("mDNS responder started (esp8266.local)");
    
    // Announce HTTP service for Android auto-discovery
    MDNS.addService("http", "tcp", 80);
    // Alternatively, a custom service type helps specific filtering
    MDNS.addService("smarthome", "tcp", 80);
  } else {
    Serial.println("Error setting up MDNS responder!");
  }

  // Configure Web Server Routes
  server.on("/", HTTP_GET, handleRoot);
  server.on("/status", HTTP_GET, handleStatus);
  server.on("/toggle", HTTP_GET, handleToggle);
  server.onNotFound(handleNotFound);

  // Start the server
  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  // Handle client requests
  server.handleClient();
  
  // Allow MDNS to keep running
  MDNS.update();
}
