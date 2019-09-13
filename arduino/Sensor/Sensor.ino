#include "SharpIR.h"


#define SRmodel 1080
#define LRmodel 20150

#define s1 A0 // 
#define s2 A1 //
#define s3 A2 //
#define s4 A3 //
#define s5 A4 //
#define s6 A5 // Long


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
  int fl = FL.distance();
  int fc = FC.distance();
  int fr = FR.distance();
  int r  = R.distance();
  int bs = BS.distance();
  int bl = BL.distance();
  
//  Serial.print("FL: ");  // returns it to the serial monitor
//  Serial.print(fl);
//    
//  Serial.print(" FC: ");  // returns it to the serial monitor
//  Serial.print(fc);
//    
//  Serial.print(" FR: ");  // returns it to the serial monitor
//  Serial.print(fr);
    
//  Serial.print(" R: ");  // returns it to the serial monitor
//  Serial.print(r);
//    
  Serial.print(" BS: ");  // returns it to the serial monitor
  Serial.print(bs);
    
  Serial.print(" BL: ");  // returns it to the serial monitor
  Serial.print(bl);

  Serial.println();
  
  delay(2000);
}
