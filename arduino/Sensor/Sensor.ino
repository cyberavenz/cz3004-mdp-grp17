#include "SharpIR.h"


#define SRmodel 1080
#define LRmodel 20150

#define s1 A0 // use#define s1 A0 // use
#define s2 A1 //
#define s3 A2 //
#define s4 A3 //
#define s5 A4 //
#define s6 A5 //long_range


SharpIR sr1 =  SharpIR(s1, SRmodel);
SharpIR sr2 =  SharpIR(s2, SRmodel);
SharpIR sr3 =  SharpIR(s3, SRmodel);
SharpIR sr4 =  SharpIR(s4, SRmodel);
SharpIR sr5 =  SharpIR(s5, SRmodel);
SharpIR sr6 =  SharpIR(s6, LRmodel);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);

}

void loop() {
  // put your main code here, to run repeatedly:
  int dis= 0;
  int garbage = 0;
  int total_dis = 0;
  int mean_dis = 0;
  for(int i = 0; i < 10; i++){
    dis = sr1.distance();
    garbage = dis;
  }
  for(int j = 0; j < 10; j++){
   
    total_dis += dis;
  }
  mean_dis = total_dis / 10;
  Serial.print("Mean distance: ");  // returns it to the serial monitor
  Serial.println(mean_dis);
  delay(2000);
}
