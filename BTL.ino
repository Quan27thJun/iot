#include <Wire.h>
#include "MAX30100_PulseOximeter.h"
#include <WiFi.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

#define DHTPIN 18
#define DHTTYPE DHT11
#define DS18B20 5
#define REPORTING_PERIOD_MS 1000

float bpm, sp, body, temperature, humidity;
const char* ssid = "Quan";
const char* password = "12345678";

DHT dht(DHTPIN, DHTTYPE);
PulseOximeter pox;
uint32_t tsLastReport = 0;
OneWire oneWire(DS18B20);
DallasTemperature sensors(&oneWire);

const char* mqtt_server = "192.168.199.174";
const int mqtt_port = 1883;

WiFiClient espClient;
PubSubClient client(espClient);

void onBeatDetected() {
  Serial.println("Beat Detected!");
}

void callback(char* topic, byte* payload, unsigned int length) {
  String message = "";
  String topicStr = String(topic);
  Serial.println(topic);

  if (strcmp(topic, "postData") == 0) {
    DynamicJsonDocument doc(256);
    deserializeJson(doc, payload, length);
    String jsonString;
    serializeJson(doc, jsonString);
    Serial.println(jsonString);
    bpm = doc["nhiptim"];
    sp = doc["oxy"];
    body = doc["cothe"];
    temperature = doc["nhietdo"];
    humidity = doc["doam"];
  }
}

void setup() {
  Serial.begin(115200);
  pinMode(19, OUTPUT);
  delay(100);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Kết nối đến mạng WiFi...");
  }
  Serial.println("Đã kết nối đến mạng WiFi");

  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
  Serial.print("Initializing pulse oximeter..");

  if (!pox.begin()) {
    Serial.println("FAILED");
    for (;;);
  } else {
    Serial.println("SUCCESS");
    pox.setOnBeatDetectedCallback(onBeatDetected);
  }

  pox.setIRLedCurrent(MAX30100_LED_CURR_7_6MA);
  dht.begin();
  sensors.begin();
  reconnect();
}

unsigned long timer = 0;

void loop() {
  client.loop();
  pox.update();
  bpm = pox.getHeartRate();
  sp = pox.getSpO2();
  sensors.requestTemperatures();
  body = sensors.getTempCByIndex(0);
  temperature = dht.readTemperature();
  humidity = dht.readHumidity();

  if (millis() - tsLastReport > REPORTING_PERIOD_MS) {
    if (isnan(bpm) || isnan(sp)) {
      Serial.println("Failed to read from Pulse Oximeter!");
    } else {
      float nhietdo = round(temperature * 10) / 10.0;
      float doam = round(humidity * 10) / 10.0;
      float cothe = round(body * 10) / 10.0;
      float nhiptim = round(bpm * 10) / 10;
      float oxy = round(sp * 10) / 10;

      Serial.print("Nhịp tim: ");
      Serial.println(bpm);
      Serial.print("Nồng độ Oxy trong máu: ");
      Serial.println(sp);
      Serial.print("Nhiệt độ cơ thể: ");
      Serial.println(body);
      Serial.print("Nhiệt độ: ");
      Serial.println(temperature);
      Serial.print("Độ ẩm: ");
      Serial.println(humidity);
      Serial.println("\n");

      StaticJsonDocument<200> jsonDoc;
      jsonDoc["bpm"] = nhiptim;
      jsonDoc["sp"] = oxy;
      jsonDoc["body"] = cothe;
      jsonDoc["temp"] = nhietdo;
      jsonDoc["humi"] = doam;

      char jsonBuffer[200];
      serializeJson(jsonDoc, jsonBuffer);

      client.publish("topic", jsonBuffer);

      if (!client.connected()) {
        reconnect();
      }
      tsLastReport = millis();
    }
  }
}

void reconnect() {
  while (!client.connected()) {
    Serial.println("Kết nối đến máy chủ MQTT...");
    if (client.connect("ESP32Client")) {
      Serial.println("Đã kết nối đến máy chủ MQTT");
      client.subscribe("postData");
    } else {
      Serial.println("Kết nối lại sau 5 giây...");
      delay(5000);
    }
  }
}
