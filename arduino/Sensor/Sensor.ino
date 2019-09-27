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
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  
  if (Serial.available() > 0) {
    cc = char(Serial.read());
  }
  
  switch (cc){
    
    //0 = wall
    //1 = 1 space in front
    
    case 's':
      Serial.print("Front Left: ");
      Serial.println(getDistance(FL, 11));
      
      Serial.print("Front Center: ");
      Serial.println(getDistance(FC, 9));
      
      Serial.print("Front Right: ");
      Serial.println(getDistance(FR, 10));

      Serial.print("Right: ");
      Serial.println(getDistance(R, 10)); 
      
      Serial.print("Rear Left Short: ");
      Serial.println(getDistance(BS, 8));
      
      Serial.print("Rear Right Long: ");
      Serial.println(getDistance(BL, 4)); // up to 4 accurately
  
    
//////// FOR CheckPoint
//    case 's':
//      if (BL.distance() > 25) {
//        Serial.print("The object is at: (bl) ");
//        Serial.print(getDistance());
//      }
//      else {
//        Serial.print("The object is at: (bs) ");
//        Serial.print(BS.distance()+1);
//        
//      }
//      Serial.println();
//      break;
///////////////////////
  }

}
int getDistance(SharpIR sensor, int offset) {
  double sum = 0;
  double average = 0; 
  
  for (int i = 0; i < 10; i++) {
    sum = sum + sensor.distance();
  }
  delay(100);
  for (int i = 0; i < 10; i++) {
    sum = sum + sensor.distance();
  }
  average = (sum / 20) - offset;
//  return average;
  return round(average/10);
}

//////// FOR CheckPoint
//double getDistance(){
//  double sum = 0;
//  double average = 0;
//  for (int i = 0; i < 10; i++) {
//    sum = sum + BL.distance();
//  }
//  delay(200);
//  for (int i = 0; i < 10; i++) {
//    sum = sum + BL.distance();
//  }
//
//  if(average<60){
//    average = (sum / 20);
//  }
//  
//  return average;
//}
///////////////////////
