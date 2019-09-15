#include "DualVNH5019MotorShield.h"
#include "PinChangeInt.h"
#include "SharpIR.h"

//////////////////////////////////
// Sensors

#define SRmodel 1080
#define LRmodel 20150

#define se1 A0 // 
#define se2 A1 //
#define se3 A2 //
#define se4 A3 //
#define se5 A4 //
#define se6 A5 // Long


SharpIR FL =  SharpIR(se1, SRmodel);
SharpIR FC =  SharpIR(se2, SRmodel);
SharpIR FR =  SharpIR(se3, SRmodel);
SharpIR R  =  SharpIR(se4, SRmodel);
SharpIR BS =  SharpIR(se5, SRmodel);
SharpIR BL =  SharpIR(se6, LRmodel);

//////////////////////////////////////

DualVNH5019MotorShield md;

#define motor_R_encoder 11 //Define pins for motor encoder input
#define motor_L_encoder 3

#define MAX_SPEED 400
#define testSpeed 200

//For counting Encoder using interrupt
int right_encoder_val = 0, left_encoder_val = 0;
int error, prevError;
float integral, derivative, output;
int fl = FL.distance();

void RightEncoderInc(){right_encoder_val++;}
void LeftEncoderInc(){left_encoder_val++;}
void resetEncoder();
float piControlForward(float left, float right);
void moveForward(int distance);
void right(int degree);

bool forward = false;
bool wall = false;
bool t1 = false, t2 = false, t3 = false, t4 = false, s1 = false, s2 = false, s3 = false;
  
void setup() {
  Serial.begin(115200);
  md.init();
  PCintPort::attachInterrupt(motor_R_encoder, RightEncoderInc, FALLING);
  PCintPort::attachInterrupt(motor_L_encoder, LeftEncoderInc, FALLING);
}

// 200 for right is 222 for left
void loop() {

  
  char cc;
  if (Serial.available() > 0) {
    cc = char(Serial.read());
    if (cc == 's') {
      forward = true;
    }
  }
  
//  right(90);
//  right(90);
  
  if(forward) {
    moveForward(1);
  }
  
  fl = FL.distance();
  
  if (fl < 10 && forward == true){    
    forward = false;
    wall = true;
    t1 = true;
  }

//  if (wall == true) {
//    if (t1) {
//      right(90);
//      t1 = false;
//      s1 = true;
//    }
//    else if (s1) {
//      moveForward(2);
//      s1 = false;
//      t2 = true;
//    }
//    else if (t2) {
//      
//      right(270);
//      t2 = false;
//      s2 = true;
//       
//    }
//    else if (s2) {
//
//      s2 = false;
//      t3 = true;
//    }
//    else if (t3) {
//
//      t3 = false;
//      s3 = true;
//    }
//    else if (s3) {
//
//      s3 = false;
//      t4 = true;
//    }
//    else if (t4) {
//
//      t4 = false;  
//      wall = false;
//    }
//  }  
}

//void avoid_left_block() {
//  resetEncoder();
//  delay(2000);
//  right(90);
//  md.setBrakes(375, 400);
//  
//  resetEncoder();
//  delay(2000);
//  moveForward(2);
//  md.setBrakes(375, 400);
//
//  resetEncoder();
//  delay(2000);
//  right(270);
//  md.setBrakes(375, 400);
//}

void resetEncoder() {
  right_encoder_val = 0;
  left_encoder_val = 0;
}


int pidControlForward(int left_encoder_val, int right_encoder_val){
  
  int pwmL = testSpeed, pwmR = testSpeed;
  
  float kp = 35;
  float ki = 1;
  float kd = 1;

  integral += error;
  derivative = error - prevError;
  
  output = kp*error + ki*integral + kd*derivative;
  prevError = error;

  pwmR = output;
//  Serial.print("error = ");
//  Serial.print(error);
//    
//  Serial.print(" pre error = ");
//  Serial.print(prevError);
//  
//  Serial.print("output = ");
//  Serial.print(output);
  
  return pwmR;
  
}

// Distance - input "1" for 10 cm
// Left_Speed - 222
// Right_Speed - 200

void moveForward(int distance){
//      Serial.print("left_encoder_val = ");
//      Serial.print(left_encoder_val);
//    
//      Serial.print(" right_encoder_val = ");
//      Serial.println(right_encoder_val);
      int output;
      int left_speed = 222;
      int right_speed = 200;
      float actual_distance = (distance*323) - (20*distance); //323 is the tick required to move 10 cm
      output = pidControlForward(left_encoder_val, right_encoder_val);
      md.setSpeeds(left_speed+output,right_speed-output);
      if(right_encoder_val >= actual_distance) {
        md.setBrakes(375, 400);
        delay(2000);
        
//        right_encoder_val = 0; 
//        left_encoder_val = 0;
    }
}

// Degree - Number of Degree u want to rotate/ It will rotate right
void right(int degree){
//      Serial.print("left_encoder_val = ");
//      Serial.print(left_encoder_val);
//    
//      Serial.print(" right_encoder_val = ");
//      Serial.println(right_encoder_val);
      int output;
      float dis = degree / 90.0;
      int left_speed = 222;
      int right_speed = 200;
      float actual_distance = (dis*405) - (5*dis);
      output = pidControlForward(left_encoder_val, right_encoder_val);
      md.setSpeeds(left_speed+output,-right_speed+output);
      if(right_encoder_val >= actual_distance){
        md.setBrakes(375, 400);
        delay(2000);
        
      }
}
