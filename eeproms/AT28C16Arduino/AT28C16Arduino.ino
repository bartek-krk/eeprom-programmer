#define WE 53

int address [11] = {22,24,26,28,30,32,34,36,38,40,42};   //LSB to MSB
int data [8] = {23,25,27,29,31,33,35,37};   //LSB to MSB


/*
 * @author: Bartosz Łukasik
 * @date:02-07-2020
 * 
 * This sketch is written for AtMEGA2560 for AtMEGA328 use shift registers
 * 
*/

void setup()
{
  Serial.begin(9600);
  Serial.setTimeout(50);
  for(int i=0; i<sizeof(address)/sizeof(int); i++) pinMode(address[i],OUTPUT);
  for(int i=0; i<sizeof(data)/sizeof(int); i++) pinMode(data[i],OUTPUT);

  for(int i=0; i<sizeof(address)/sizeof(int); i++) digitalWrite(i, LOW);
  for(int i=0; i<sizeof(data)/sizeof(int); i++) digitalWrite(i, LOW);

  pinMode(WE, OUTPUT);
  digitalWrite(WE, HIGH);
}

void loop()
{ 
  String incoming = "";
  if(Serial.available()) incoming = Serial.readStringUntil('\n');

  if(incoming != "")
  {
    char chars[20];
    incoming.toCharArray(chars,20);
  
    int addressIterator = 0;
    for(int i=0; i<11; i++)
    {
      char buffer = chars[i];
      if(buffer == '0') digitalWrite(address[addressIterator], LOW);
      if(buffer == '1') digitalWrite(address[addressIterator], HIGH);
      addressIterator++;
    }

    int dataIterator = 0;
    for(int i=11; i<20; i++)
    {
      char buffer = chars[i];
      if(buffer == '0') digitalWrite(data[dataIterator], LOW);
      if(buffer == '1') digitalWrite(data[dataIterator], HIGH);
      dataIterator++;
    }
    digitalWrite(WE, LOW);
    delayMicroseconds(1);
    digitalWrite(WE, HIGH);
  }
}

/*---------------------------------------------------------------------
----------------------------END OF FILE--------------------------------
-----------------------------------------------------------------------*/
