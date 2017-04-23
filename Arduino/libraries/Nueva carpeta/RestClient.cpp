#include <EthernetClient.h>


class RestClient
{
	
	EthernetClient client;
	
	String RestClient::request(const char* method, const char* path,
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

	String RestClient::readResponse() {
	  
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
}