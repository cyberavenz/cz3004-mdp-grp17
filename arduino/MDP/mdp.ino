#include "DualVNH5019MotorShield.h"
#include "PinChangeInt.h"
#include "SharpIR.h"

DualVNH5019MotorShield md;

#define motor_R_encoder 11 //Define pins for motor encoder input
#define motor_L_encoder 3


#define SRmodel 1080
#define LRmodel 20150

#define s1 A0 // use
#define s2 A1 //
#define s3 A2 //
#define s4 A3 //
#define s5 A4 //
#define s6 A5 //long_range

#define MAX_SPEED 400
#define testSpeed 200
#define timer = 0;

SharpIR sr1 =  SharpIR(s1, SRmodel);
SharpIR sr2 =  SharpIR(s2, SRmodel);
SharpIR sr3 =  SharpIR(s3, SRmodel);
SharpIR sr4 =  SharpIR(s4, SRmodel);
SharpIR sr5 =  SharpIR(s5, SRmodel);
SharpIR sr6 =  SharpIR(s6, LRmodel);

//For counting Encoder using interrupt
int right_encoder_val = 0, left_encoder_val = 0;
void RightEncoderInc(){right_encoder_val++;}
void LeftEncoderInc(){left_encoder_val++;}
float piControlForward(float left, float right);
void setup()
{
  Serial.begin(115200);
  md.init();
  PCintPort::attachInterrupt(motor_R_encoder, RightEncoderInc, FALLING);
  PCintPort::attachInterrupt(motor_L_encoder, LeftEncoderInc, FALLING);
}

void loop() {
  
//paste in loop to work
    int output;
    output = pidControlForward(left_encoder_val, right_encoder_val);

    md.setSpeeds(230+output,testSpeed-output);
   
//    unsigned long dt = millis() - timer;
//      if (dt >= 1000) // True for every 2 sec. 
//      {
//        timer = millis();
//        md.setSpeeds(testSpeed+output,testSpeed-output); 
//      }
//        

//  333 = 10cm
    if(left_encoder_val >= 333) {
      md.setBrakes(200, 268);
      delay(2000);
      Serial.println("left_encoder_val = ");
      Serial.println(left_encoder_val);
    
      Serial.println("right_encoder_val = ");
      Serial.println(right_encoder_val);
      
      right_encoder_val = 0; 
      left_encoder_val = 0;
    }

//  md.setSpeeds(100,100);

//  WHEEL PIN CHECK
//  delay(1000);
//  Serial.println("left_encoder_val = ");
//  Serial.println(left_encoder_val);
//
//  Serial.println("right_encoder_val = ");
//  Serial.println(right_encoder_val);
  
}

//This function is used to implement the PID control
int pidControlForward(int left_encoder_val, int right_encoder_val){
  int error, prevError, pwmL = testSpeed, pwmR = testSpeed;
  float integral, derivative, output;
  float kp = 30;
  float ki = 1;
  float kd = 1;

  error = right_encoder_val - left_encoder_val - 1 ;
  integral += error;
  derivative = error - prevError;
  output = kp*error + ki*integral + kd*derivative;
  prevError = error;

  pwmR = output;
  return pwmR;
  
}

//int goStraightOneGrid(long value)
//{
//  while (1) {
//    if (totalDis >= value) //125000)
//    {
//      totalDis = 0;
//      md.setBrakes(355, 350);
//      break;
//    }
//    else {
//      goStraightOneTime();
//      totalDis = totalDis + Input_ML + Input_MR;
//    }
//  }
//}
