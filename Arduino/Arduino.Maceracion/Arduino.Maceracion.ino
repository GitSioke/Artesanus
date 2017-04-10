//#include <RestClient.h>
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

//#define HTTP_DEBUG // Uncomment for debugging purposes

//#ifdef HTTP_DEBUG
//#define Serial.print(string) (Serial.print(string))
//#endif

//#ifndef HTTP_DEBUG
//#define Serial.print(string)
//#endif


EthernetClient client;
int readResponse(String*);
void write(const char*);
const char* host = IP;
int port = PORT;
int num_headers;
const char* headers[10];
const char* contentType = "application/json; charset=utf-8";

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

  byte mac[] = { 0x90, 0xA2, 0xDA, 0x10, 0x13, 0x8A };
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    return false;
  }
  
  // This could fail in case of no connection with router.
  //client.dhcp();
  Serial.println(Ethernet.localIP());
  delay(500);
}

bool first = false;
void loop() 
{    
  // Check if the process is in progress or if is going to start
  if (_started || checkForStart())
  {
    Serial.println("Try send data to server");
    sendDataToServer();
  }

  Serial.println("Going to sleep");
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
  
  char json[256];
  
  // create and format json object to send to server
  JsonObject& root = jsonBuffer.createObject();
  root["id_process"] = "1";
  root.printTo(json, sizeof(json));
  Serial.println(json); 
  
  String response = request("POST", "/retrieve/last_mashing_event/", json);
  delay(1000);
  Serial.println("Response body from server: ");
  Serial.println(response);
  
  StaticJsonBuffer<50> jsonBuffer1;
  JsonObject& root1 = jsonBuffer1.parseObject(response);
  id_process = root1["result"];

  Serial.println("Id process: ");
  Serial.println(id_process);

  bool startCommandReceived = id_process == 0 ? false: true;
  _started = startCommandReceived;

  return startCommandReceived;
}

///
/// This funtion send data collected to server when there is a mashing process in progress
///
void sendDataToServer()
{
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

  String response = request("POST", "/insert/events/", json);
  Serial.println("SENDDATA:: Response body from server: ");
  Serial.println(response);
  temperature = temperature + 0.5;
   
  
  // Sleep for 60 seconds and resend data
  delay(10000);
}

String request(const char* method, const char* path,
                  const char* body)
{
  
  if(client.connect(host, port)== 1){
    Serial.print("Connected to server\n");
    //Serial.print("REQUEST: \n");
    // Make a HTTP request line:
    client.print(method);
    client.print(" ");
    client.print(path);
    client.print(" HTTP/1.1\r\n");
    for(int i=0; i<num_headers; i++)
    {
      client.print(headers[i]);
      client.print("\r\n");
    }
    
    client.print("Host: ");
    client.print(host);
    client.print("\r\n");
    client.print("Connection: keep-alive\r\n");


    if(body != NULL){
      char contentLength[5];
      sprintf(contentLength, "%d", strlen(body));
      client.print("Content-Length: ");
      client.print(contentLength);
      client.print("\r\n");

      delay(50);
      client.print("Content-Type: ");
      client.print("application/json; charset=utf-8");
      client.print("\r\n");
      client.print("\r\n");
      
      client.print(body);
      client.print("\r\n");
      client.print("\r\n");
    }
    
    //make sure you write all those bytes.
    delay(500);

    String statusCode = readResponse();
  
    //cleanup
    num_headers = 0;
    client.stop();
    delay(100);
    
    return statusCode;
  }else{
    Serial.print("RestClient Connection failed\n");
    return "Connection failed";
  }
}

String readResponse() {
  
  boolean currentLineIsBlank = true;
  boolean httpBody = false;
  boolean inStatus = false;

  char statusCode[4];
  String jsonResponse = "";
  int i = 0;
  int code = 0;

  //Serial.print("RestClient: RESPONSE: \n");
  while (client.available()) {
    
    char c = client.read();
    //Serial.print(c);
  
    if(c == ' ' && !inStatus)
    {
       inStatus = true;
    }
  
    if(inStatus && i < 3 && c != ' '){
      statusCode[i] = c;
      i++;
    }
    if(i == 3){
      statusCode[i] = '\0';
      code = atoi(statusCode);
    }
  
    if(httpBody && code== 200)
    {
      jsonResponse += c;
      //Serial.print(c);
    }
    else
    {
      if (c == '\n' && currentLineIsBlank) {
        httpBody = true;
      }

      if (c == '\n') {
        // you're starting a new line
        currentLineIsBlank = true;
      }
      else if (c != '\r') {
        // Character on the current line
        currentLineIsBlank = false;
      }
    }
  }
  
  jsonResponse += '\0';
  delay(1500);
  
  return jsonResponse;
}
