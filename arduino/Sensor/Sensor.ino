#include "SharpIR.h"


#define SRmodel 1080
#define LRmodel 20150

#define s1 A0 // 
#define s2 A1 //
#define s3 A2 //
#define s4 A3 //
#define s5 A4 //
#define s6 A5 // Long

char cc;

SharpIR FL =  SharpIR(s1, SRmodel);
SharpIR FC =  SharpIR(s2, SRmodel);
SharpIR FR =  SharpIR(s3, SRmodel);
SharpIR R  =  SharpIR(s4, SRmodel);
SharpIR BS =  SharpIR(s5, SRmodel);
SharpIR BL =  SharpIR(s6, LRmodel);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
}

void loop() {
  // put your main code here, to run repeatedly:
  
  if (Serial.available() > 0) {
    cc = char(Serial.read());
  }
  
  switch (cc){
//    case 's':
//      Serial.print("Front Left: ");
//      Serial.println(getDistance(FL, 9));
//      
//      Serial.print("Front Center: ");
//      Serial.println(getDistance(FC, 9));
//      
//      Serial.print("Front Right: ");
//      Serial.println(getDistance(FR, 9));
//
//      Serial.print("Right: ");
//      Serial.println(getDistance(R, 6)); 
//      
//      Serial.print("Rear Left Short: ");
//      Serial.println(getDistance(BS, 9));
      
//      Serial.print("Rear Right Long: ");
//      int blDistance = getDistance();
//      Serial.println(blDistance);
//      if (blDistance <= 5) {
//        Serial.println("too far or too near"); // - 1
//      }
  
    
////// FOR CheckPoint
    case 's':
      if (BL.distance() > 25) {
        Serial.print("The object is at: (bl) ");
        Serial.print(getDistance());
      }
      else {
        Serial.print("The object is at: (bs) ");
        Serial.print(BS.distance()+1);
        
      }
      Serial.println();
      break;
/////////////////////
  }

}
int getDistance(SharpIR sensor, int offset) {
  double sum = 0;
  double average = 0; 
  
  for (int i = 0; i < 10; i++) {
    sum = sum + sensor.distance() - offset;
  }
  average = (sum / 10);
  
  return round(average/10);
//  return average;
}

double getDistance(){
  double sum = 0;
  double average = 0;
  for (int i = 0; i < 10; i++) {
    sum = sum + BL.distance();
  }

  if(average<60){
    average = (sum / 10) + 10;
  }
  
  return average;
}
