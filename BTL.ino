#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <WiFi.h>
#include <Wire.h>
#include "MAX30100_PulseOximeter.h"
#include <OneWire.h>
#include <DallasTemperature.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

#define DHTPIN 18
#define DHTTYPE DHT11
#define DS18B20 5
#define REPORTING_PERIOD_MS 1000

float temperature, humidity, bpm, sp, body;

int cb = A0;
int doc_cb, TBcb;

const char* ssid = "Quan";
const char* password = "12345678";

DHT dht(DHTPIN, DHTTYPE);
PulseOximeter pox;
OneWire oneWire(DS18B20);
DallasTemperature sensors(&oneWire);

const char* mqtt_server = "192.168.199.174";
const int mqtt_port = 1883;

WiFiClient espClient;
PubSubClient client(espClient);

void onBeatDetected() {
  Serial.println("Beat!");
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

    temperature = doc["nhietdo"];
    humidity = doc["doam"];
    bpm = doc["nhiptim"];
    sp = doc["oxy"];
    body = doc["cothe"];
  }
}

void setup() {
  Serial.begin(115200);
  delay(10);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Kết nối đến mạng WiFi...");
  }
  Serial.println("Đã kết nối đến mạng WiFi");

  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
  Wire.begin();
  pox.begin();
  dht.begin();
  sensors.begin(); // Initialize DS18B20 sensor
  reconnect();
}

unsigned long DHTdelay = 3000;
unsigned long timer = 0;

void loop() {
  client.loop();

  unsigned long currentTime = millis();
  if (currentTime - timer >= DHTdelay) {
    timer = currentTime;

    TBcb = analogRead(cb);
    sensors_event_t event;

    event.temperature = dht.readTemperature();
    temperature = event.temperature;

    event.relative_humidity = dht.readHumidity();
    humidity = event.relative_humidity;

    bpm = pox.getHeartRate();
    sp = pox.getSpO2();

    // Retrieve body temperature from the Dallas Temperature library
    body = sensors.getTempCByIndex(0);

    // ... (rest of your code)
    float phantramao = map(TBcb, 0, 1023, 0, 100);    //Chuyển giá trị Analog thành giá trị %
    float phantramthuc = 100 - phantramao; 

    if (isnan(temperature) || isnan(humidity)) {
        Serial.println("Failed to read from DHT sensor!");
        // return;
    }
    if (isnan(bpm) || isnan(sp)) {
        Serial.println("Failed to read from DS sensor!");
        // return;
    }
    if (isnan(body)) {
        Serial.println("Failed to read from onewire sensor!");
        // return;
    }
    float nhietdo = round(temperature * 10) / 10.0; // Làm tròn temperature với 1 chữ số thập phân
    float doam = round(humidity * 10) / 10.0; // Làm tròn humidity với 1 chữ số thập phân
    float nhiptim = round(bpm * 10) / 10.0;
    float oxy = round(sp * 10) / 10.0;
    float cothe = round(body * 10) / 10.0;
    // float doamdat = round(phantramthuc * 10) / 10.0; // Làm tròn soil với 1 chữ số thập phân
    delay (1000);
    Serial.print("Nhiệt độ: ");
    Serial.println(temperature);
    Serial.print("Độ ẩm: ");
    Serial.println(humidity);
    Serial.print("Nhịp tim: ");
    Serial.println(bpm);
    Serial.print("Oxy: ");
    Serial.println(sp);
    Serial.print("cothe: ");
    Serial.println(body);
    Serial.println("\n");

    StaticJsonDocument<200> jsonDoc;
    jsonDoc["temp"] = nhietdo;
    jsonDoc["humi"] = doam;
    jsonDoc["bpm"] = nhiptim;
    jsonDoc["sp"] = oxy;
    jsonDoc["body"] = cothe;

    char jsonBuffer[200];
    serializeJson(jsonDoc, jsonBuffer);

    client.publish("topic", jsonBuffer);

    if (!client.connected()) {
      reconnect();
    }

    doc_cb = 0;
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
