const byte numChars = 32;
char receivedChars[numChars];
boolean newData = false; 



void receiver();
void sendData();

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);

}

void loop() {
  // put your main code here, to run repeatedly:
  delay(1000);
//  receiver();
  Serial.println("@bHello!");
//  sendData();
}

void receiver() {
    static byte ndx = 0;
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
        String rec = String(receivedChars);
        Serial.println("@b"+rec+"!");
        newData = false;
    }
}
