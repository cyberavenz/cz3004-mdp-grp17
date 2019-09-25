const byte numChars = 32;
char receivedChars[numChars];
boolean newData = false; 



void receiver();
void sendData();

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

}

void loop() {
  // put your main code here, to run repeatedly:
//  Serial.println("@bHello!");
  delay(100);
  receiver();
  delay(100);
  sendData();
}

void receiver() {
    int ndx = 0;
    char endMarker = '!';
    char rc;
    while (Serial.available() > 0 && newData == false) {
        rc = Serial.read();
        if (rc != endMarker) {
            receivedChars[ndx] = rc;
            ndx++;
            if (ndx >= numChars) {
                ndx = numChars - 1;
            }
        }
        else {
            ndx = 0;
            newData = true;
        }
    }
}

void sendData() {
    if (newData == true) {
        String initial = String(receivedChars[0]);
        String second = String(char(int(receivedChars[1])+1));
        Serial.println("@"+initial+second+"!");
        newData = false;
    } 
}
