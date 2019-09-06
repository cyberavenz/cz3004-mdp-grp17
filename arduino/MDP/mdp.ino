#include "DualVNH5019MotorShield.h"
#include "PinChangeInt.h"

DualVNH5019MotorShield md;

#define motor_R_encoder 11 //Define pins for motor encoder input
#define motor_L_encoder 3

#define MAX_SPEED 400
#define testSpeed 200
#define timer = 0;

//For counting Encoder using interrupt
int right_encoder_val = 0, left_encoder_val = 0;
void RightEncoderInc(){right_encoder_val++;}
void LeftEncoderInc(){left_encoder_val++;}

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
    output = pidControlForward(left_encoder_val,right_encoder_val);
    md.setSpeeds(testSpeed+output,testSpeed-output);
//    unsigned long dt = millis() - timer;
//      if (dt >= 1000) // True for every 2 sec. 
//      {
//        timer = millis();
//        md.setSpeeds(testSpeed+output,testSpeed-output); 
//      }
//        
    if((right_encoder_val >= 249) || (right_encoder_val >= 249)) {
      md.setBrakes(355, 340);
      delay(2000);
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
  float kp = 1;
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
