#include <RestClient.h>
#include <Dhcp.h>
#include <Dns.h>
#include <Ethernet2.h>
#include <EthernetClient.h>
#include <EthernetServer.h>
#include <EthernetUdp2.h>
//#include <DHT.h>
#include <SPI.h>
#include <ArduinoJson.h>

#define IP "192.168.1.40"  // Server IP
#define PORT 5000         // Server Port

RestClient client = RestClient(IP, PORT);
double temperature = 10.0;

void setup() 
{
  Serial.begin(9600);
  // Connect via DHCP
  Serial.println("Connecting to network");
  
  // This could fail in case of no connection with router.
  client.dhcp();
  Serial.println(Ethernet.localIP());
  delay(500);
}

String response;
bool first = false;
void loop() 
{    
  client.setContentType("application/json; charset=utf-8");
  response = "";
  StaticJsonBuffer<200> jsonBuffer;
  char json[256];
  JsonObject& root = jsonBuffer.createObject();
  root["id_process"] = "1";
  root["value"] = temperature;
  root["data"] = "temperature";
  root["type"] = "mashing";
  root.printTo(json, sizeof(json));  
  Serial.println(json);
  int statusCode = client.post("/insert/events/", json, &response);
  Serial.println("Status code from server: ");
  Serial.println(statusCode);
  Serial.println("Response body from server: ");
  Serial.println(response);
  
  delay(1000000);
}
