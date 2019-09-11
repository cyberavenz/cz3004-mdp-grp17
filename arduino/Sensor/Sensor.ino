#include "SharpIR.h"


#define SRmodel 1080
#define LRmodel 20150

#define s1 A0 // use
SharpIR sr1 =  SharpIR(s1, LRmodel);
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
