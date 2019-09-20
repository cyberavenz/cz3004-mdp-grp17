#include "SharpIR.h"


#define SRmodel 1080
#define LRmodel 20150

#define s1 A0 // 
#define s2 A1 //
#define s3 A2 //
#define s4 A3 //
#define s5 A4 //
#define s6 A5 // Long

//int stack[10];
//int pointer = 0;
int test = 1;
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
    case 'a':
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
  }

//  // FOR PROJECT
//  if(bs <= 28 && bl <= 30){
//    Serial.print("The object is at: (bs) ");
//    Serial.print(round((bs - 5)/10)*10);
//  }
//  else if (bl > 64 && bl < 79){
//    Serial.print("The object is at: (bl) 70");
//  }
//  else if (bl <= 64) {
//    Serial.print("The object is at: (bl) ");
//    Serial.print(round(bl/10)*10); 
//  }
//  Serial.println();
//  delay(2000);
//  //////

}

double getDistance(){
  double sum = 0;
  double average = 0;
  
  for (int i = 0; i < 10; i++) {
    sum = sum + BL.distance();
  }
  if(average<60)
  average = (sum / 10) + 10;
  
  return average;
}
