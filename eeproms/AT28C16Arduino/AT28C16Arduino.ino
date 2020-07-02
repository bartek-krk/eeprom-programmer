int address [11] = {22,24,26,28,30,32,34,36,38,40,42};
int data [8] = {23,25,27,29,31,33,35,37};
String state = "STANDBY";
String incomingData = "";

/*
 * @author: Bartosz Łukasik
 * @date:02-07-2020
 * 
 * This sketch is written for AtMEGA2560 for AtMEGA328 use shift registers
 * 
 * TRANSMISSION PROTOCOL:
 * 1. Send "TRANSMITTING" ro serial bus
 * 2. Send data in binary formatted: AAAAAAAAAAADDDDDDDD (A is address, D is data)
 * 3. Next set should be sent after "WRITEOK" acknowledge sequence is sent to the bus by Arduino
 * 4. After all data is sent, write "EOT" (end-of-transmission) sequence to the bus in order to terminate transmission
*/

void setup()
{
  Serial.begin(9600);
  for(int i=0; i<sizeof(address)/sizeof(int); i++) pinMode(address[i],OUTPUT);
  for(int i=0; i<sizeof(data)/sizeof(int); i++) pinMode(data[i],OUTPUT);


  for(int i=0; i<sizeof(address)/sizeof(int); i++) digitalWrite(i, LOW);
  for(int i=0; i<sizeof(data)/sizeof(int); i++) digitalWrite(i, LOW);
}

void loop()
{ 
  
  
  if(Serial.available() && state == "STANDBY") state = readIgnore();
  if(Serial.available() && state == "TRANSMITTING")
  {
   {
     String incoming = readIgnore();
     if(incoming == "EOT") state = "EOT";
     else
     {
      incomingData = incoming;
      state = "WRITING";
     }
    }
  }
  if(state == "WRITING")
  {
    char states[20];
    incomingData.toCharArray(states, 20);
    int statesIterator = 0;
    for(int i=0;i<sizeof(address)/sizeof(int); i++)
    {
      if(states[statesIterator] == '1') digitalWrite(address[i], HIGH);
      if(states[statesIterator] == '0') digitalWrite(address[i], LOW);
      statesIterator++;
    }
    for(int i=0;i<sizeof(data)/sizeof(int); i++)
    {
      if(states[statesIterator] == '1') digitalWrite(data[i], HIGH);
      if(states[statesIterator] == '0') digitalWrite(data[i], LOW);
      statesIterator++;
    }
    state = "TRANSMITTING";
    Serial.write("WRITEOK");
  }
  if(state == "EOT")
  {
    state = "STANDBY";
    incomingData = "";
  }
  //Serial.println(incomingData);
}

//FUNCTIONS

String readIgnore()
{
  String returnStr = "";
  if(Serial.available())
  {
    String incomingStr = Serial.readString();
    unsigned int s = incomingStr.length();
    char incomingChar[s];
    incomingStr.toCharArray(incomingChar, s);
    for(int i=0;i<sizeof(incomingChar)/sizeof(char);i++) if(incomingChar[i]!='\n') returnStr = returnStr + String(incomingChar[i]);
  }
  return returnStr;
}

/*---------------------------------------------------------------------
----------------------------END OF FILE--------------------------------
-----------------------------------------------------------------------*/
