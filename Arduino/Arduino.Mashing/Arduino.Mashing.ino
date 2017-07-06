#include <Dhcp.h>
#include <Dns.h>
#include <Ethernet2.h>
#include <EthernetClient.h>
#include <EthernetServer.h>
#include <EthernetUdp2.h>
#include <SPI.h>
#include <ArduinoJson.h>
#include <OneWire.h> 
#include <DallasTemperature.h>
#include <MemoryFree.h>

#define IP "192.168.1.46"  // Server IP
#define PORT 5000         // Server Port
#define TemperaturePin 4 // Temperature data pin

#define NOTSTARTED 0 
#define STARTED 1
#define OPEN 2
#define CLOSE 3
#define STOP 4
 

int currentStatus;

//#define HTTP_DEBUG // Uncomment for debugging purposes

//#ifdef HTTP_DEBUG
//#define Serial.print(string) (Serial.print(string))
//#endif

//#ifndef HTTP_DEBUG
//#define Serial.print(string)
//#endif

OneWire ourWire(TemperaturePin); // Establish temperature pin as bus for OneWire communication. 
DallasTemperature temperatureSensor(&ourWire); // Instantiate DallasTemperature library.
EthernetClient client;
//int readResponse(String*);
void write(const char*);
const char* host = IP;
int port = PORT;
int num_headers;
const char* headers[10];
const char* contentType = "application/json; charset=utf-8";

bool _started;
int id_process = -1;

const char* sourceCode = "mashing";

// 
byte sensorInterrupt = 0;  // 0 = digital pin 2
byte sensorPin       = 2;

// The hall-effect flow sensor outputs approximately 4.5 pulses per second per
// litre/minute of flow. Default: 4.5
float calibrationFactor = 5.8;

volatile byte pulseCount;  
float flowRate;
unsigned int flowMilliLitres;
unsigned long totalMilliLitres;
unsigned long oldTime;
unsigned int noFlowCounter;

void setup() 
{
  Serial.begin(9600);
  // Connect via DHCP
  Serial.println("Connecting to network");

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

  temperatureSensor.begin();

  pinMode(sensorPin, INPUT);
  digitalWrite(sensorPin, HIGH);

  pulseCount        = 0;
  flowRate          = 0.0;
  flowMilliLitres   = 0;
  totalMilliLitres  = 0;
  oldTime           = 0;
  noFlowCounter     = 0;
  currentStatus = NOTSTARTED;
}

bool first = false;
void loop() 
{    
  
  // Check if the process is in progress or if is going to start
  switch (currentStatus)
  {
    case NOTSTARTED:
      Serial.println("Asking for new starting commands");
      break;
      
    case STARTED:
      Serial.println("Try send temperature to server");
      retrieveTemperature();
      
      // Sleep for 30 seconds and resend data
      delay(30000);
      break;
    
    case OPEN:
      openValve();
      while(noFlowCounter < 9)
      {
        calculateFlow();
      }
      
      noFlowCounter = 0;
      saveTotalLitres();
      closeValve();
      break;

    case CLOSE:
      //stopProcess();

    case STOP:
      Serial.println("Stopping mashing process");
      currentStatus = NOTSTARTED;
      break;
  }

  checkStatus();
  delay(30000);

}

void saveTotalLitres()
{
  sendDataToServer("millilitres", totalMilliLitres, "/insert/millilitres/");
}

void retrieveTemperature()
{
  temperatureSensor.requestTemperatures(); // Get sensor ready
  double temperature = temperatureSensor.getTempCByIndex(0);
  Serial.print(temperature); // Read temperature in Celsius
  Serial.println(" ºC degrees");

  sendDataToServer("temperature", temperature, "/insert/temperature/");
}

void openValve()
{
   Serial.println("Opening valve");
}

void closeValve()
{
   Serial.println("Closing valve");
}

///
/// This function check on server for the latest start event and returns true in case of new event. 
/// in other case returns false.
///
void checkStatus()
{
  StaticJsonBuffer<50> jsonBuffer;
  
  char json[256];
  
  // create and format json object to send to server
  JsonObject& root = jsonBuffer.createObject();
  root["id"] = id_process;
  root["cmd"] = currentStatus;
  root["source"] = "mashing";
  root.printTo(json, sizeof(json));
  Serial.println(json); 

  String response = "";
  //String response = request("POST", "/check/status/", json);
  request("POST", "/check/status/", json, &response);
  delay(1000);
  Serial.println("Response body from server: ");
  Serial.println(response);
  
  StaticJsonBuffer<50> jsonBuffer1;
  JsonObject& root1 = jsonBuffer1.parseObject(response);
  id_process = root1["res"];
  currentStatus = root1["cmd"];

}

///
/// This funtion send data collected to server when there is a mashing process in progress
///
void sendDataToServer(String data, double value, const char* url)
{
  StaticJsonBuffer<200> jsonBuffer;
  char json[256]; 
    
  // create and format json object to send to server
  JsonObject& root = jsonBuffer.createObject();
  root["id_process"] = id_process;
  root["value"] = value;
  root["data"] = data;
  root["source"] = "mashing";
  root["type"] = "data";
  root.printTo(json, sizeof(json));  
  Serial.println(json);

  String response;
  //String response = request("POST", "/insert/temperature/", json);
  request("POST", url, json, &response);   
}

// Calculate flow and evaluate if flow has been stopped. Returns true when liquid is flowing, false when conditions met to consider that flow has been stopped.
void calculateFlow()
{

  if((millis() - oldTime) > 1000)    // Only process counters once per second
  { 
    // Disable the interrupt while calculating flow rate and sending the value to
    // the host
    detachInterrupt(sensorInterrupt);
        
    // Because this loop may not complete in exactly 1 second intervals we calculate
    // the number of milliseconds that have passed since the last execution and use
    // that to scale the output. We also apply the calibrationFactor to scale the output
    // based on the number of pulses per second per units of measure (litres/minute in
    // this case) coming from the sensor.
    flowRate = ((1000.0 / (millis() - oldTime)) * pulseCount) / calibrationFactor;
    
    // Note the time this processing pass was executed. Note that because we've
    // disabled interrupts the millis() function won't actually be incrementing right
    // at this point, but it will still return the value it was set to just before
    // interrupts went away.
    oldTime = millis();
    
    // Divide the flow rate in litres/minute by 60 to determine how many litres have
    // passed through the sensor in this 1 second interval, then multiply by 1000 to
    // convert to millilitres.
    flowMilliLitres = (flowRate / 60) * 1000;

    if (flowMilliLitres == 0)
    {
      // There is no increase, could be a stopping flow
      noFlowCounter++;      
    }
    else
    {
      // Liquid is flowing
      noFlowCounter = 0;  
    }
    
    // Add the millilitres passed in this second to the cumulative total
    totalMilliLitres += flowMilliLitres;
      
    unsigned int frac;
    
    // Print the flow rate for this second in litres / minute
    Serial.print("Flow rate: ");
    Serial.print(int(flowRate));  // Print the integer part of the variable
    Serial.print(".");             // Print the decimal point
    // Determine the fractional part. The 10 multiplier gives us 1 decimal place.
    frac = (flowRate - int(flowRate)) * 10;
    Serial.print(frac, DEC) ;      // Print the fractional part of the variable
    Serial.print("L/min");
    // Print the number of litres flowed in this second
    Serial.print("  Current Liquid Flowing: ");             // Output separator
    Serial.print(flowMilliLitres);
    Serial.print("mL/Sec");

    // Print the cumulative total of litres flowed since starting
    Serial.print("  Output Liquid Quantity: ");             // Output separator
    Serial.print(totalMilliLitres);
    Serial.println("mL"); 

    // Reset the pulse counter so we can start incrementing again
    pulseCount = 0;
    
    // Enable the interrupt again now that we've finished sending output
    attachInterrupt(sensorInterrupt, pulseCounter, FALLING);
  }
}



int request(const char* method, const char* path,
                  const char* body, String* response)
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

    int statusCode = readResponse(response);
  
    //cleanup
    num_headers = 0;
    client.stop();
    delay(100);
    
    return statusCode;
  }
  else
  {
    Serial.print("RestClient Connection failed\n");
    return "Connection failed";
  }
}

int readResponse(String* response) 
{ 
  boolean currentLineIsBlank = true;
  boolean httpBody = false;
  boolean inStatus = false;

  char statusCode[4];
  //String jsonResponse = "";
  int i = 0;
  int code = 0;

  //Serial.print("RestClient: RESPONSE: \n");
  while (client.available()) {
    
    char c = client.read();
    Serial.print(c);
    delay(10);
  
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
      if (c != '\n') 
      {
        //jsonResponse += c;
        response->concat(c);
        //Serial.print(*response);
      }
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

  //Serial.println(jsonResponse);
  //jsonResponse += '\0';
  //Serial.println(jsonResponse);
  delay(1500);
  //Serial.println(jsonResponse);
  //return jsonResponse;
  return code;
}

/*
Interrupt Service Routine
 */
void pulseCounter()
{
  // Increment the pulse counter
  pulseCount++;
}
