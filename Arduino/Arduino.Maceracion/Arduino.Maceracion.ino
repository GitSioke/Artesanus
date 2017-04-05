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
double temperature;
bool _started;
int id_process;

void setup() 
{
  Serial.begin(9600);
  // Connect via DHCP
  Serial.println("Connecting to network");

  temperature = 10.0;
  _started = false;
  
  // This could fail in case of no connection with router.
  client.dhcp();
  Serial.println(Ethernet.localIP());
  delay(500);
}

String response;
bool first = false;
void loop() 
{    
  // Check if the process is in progress or if is going to start
  if (_started || checkForStart())
  {
    Serial.println("Try send data to server");
    sendDataToServer();
  }

  // Delay 30 seconds, and restart cheking if new start command was launched.
  delay(60000);
}


///
/// This function check on server for the latest start event and returns true in case of new event. 
/// in other case returns false.
///
bool checkForStart()
{
  StaticJsonBuffer<20> jsonBuffer;
  Serial.println("checkForStart: creating variables");
  //Set content type to enable json content
  client.setContentType("application/json; charset=utf-8");
  response = "";
  char json[256];
  
  // create and format json object to send to server
  JsonObject& root = jsonBuffer.createObject();
  root["id_process"] = "1";
  root.printTo(json, sizeof(json));
  Serial.println(json); 
  int statusCode = client.post("/retrieve/last_mashing_event/", json, &response);
  
  StaticJsonBuffer<50> jsonBuffer1;
  JsonObject& root1 = jsonBuffer1.parseObject(response);
  id_process = root1["result"];
  
  Serial.println("Response body from server: ");
  Serial.println(response);
  Serial.println(id_process);

  _started = id_process == 0 ? false: true;
  //_started = true;
  delay(1000);

  return _started;
}

///
/// This funtion send data collected to server when there is a mashing process in progress
///
void sendDataToServer()
{
  //Set content type to enable json content
  client.setContentType("application/json; charset=utf-8");
  response = "";
  StaticJsonBuffer<200> jsonBuffer;
  char json[256];
  
  // create and format json object to send to server
  JsonObject& root = jsonBuffer.createObject();
  root["id_process"] = "1";
  root["value"] = temperature;
  root["data"] = "temperature";
  root["type"] = "mashing";
  root.printTo(json, sizeof(json));  
  Serial.println(json);


  //int statusCode = client.post("/insert/events/", json3, &response3);
  Serial.println("Status code from server: ");
  //Serial.println(statusCode);
  Serial.println("Response body from server: ");
  Serial.println(response);
  temperature = temperature + 0.5;
   
  
  // Sleep for 60 seconds and resend data
  delay(10000);
}