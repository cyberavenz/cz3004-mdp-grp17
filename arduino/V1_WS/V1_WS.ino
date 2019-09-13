#include "DualVNH5019MotorShield.h"
#include "PinChangeInt.h"
#include "SharpIR.h"

//////////////////////////////////
// Sensors

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

//////////////////////////////////////

DualVNH5019MotorShield md;

#define motor_R_encoder 11 //Define pins for motor encoder input
#define motor_L_encoder 3

#define MAX_SPEED 400
#define testSpeed 200

//For counting Encoder using interrupt
int right_encoder_val = 0, left_encoder_val = 0;
int fl = FL.distance();
void RightEncoderInc(){right_encoder_val++;}
void LeftEncoderInc(){left_encoder_val++;}
float piControlForward(float left, float right);
void moveForward(int distance,int left_speed,int right_speed);
void rotate(int degree);
void move_F(int left_speed, int right_speed);

bool s = true;
bool wall = false;

bool r = false;
bool g = true;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  md.init();
  PCintPort::attachInterrupt(motor_R_encoder, RightEncoderInc, FALLING);
  PCintPort::attachInterrupt(motor_L_encoder, LeftEncoderInc, FALLING);
}

void loop() {

fl = FL.distance();

if(s == true){
  move_F(222,200);
}

if (fl < 10){
  md.setSpeeds(0,0);
  s = false;
  wall = true;
}

if(wall == true){
  if(g == true){
      right_encoder_val = 0; 
      left_encoder_val = 0;
      g = false;
  }
  rotate(90);
  if(r == true){
    moveForward(3,222,200);
  }
}


//  rotate(270);
//  moveForward(3,222,200);
//  rotate(270);
//  moveForward(4,222,200);
//  rotate(90);



}


int pidControlForward(int left_encoder_val, int right_encoder_val){
  
  int error, prevError, pwmL = testSpeed, pwmR = testSpeed;
  
  float integral, derivative, output;
  float kp = 35;
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

// Distance - input "1" for 10 cm
// Left_Speed - 222
// Right_Speed - 200

void moveForward(int distance,int left_speed,int right_speed){
      int output;
      float actual_distance = (distance*323) - (20*distance); //323 is the tick required to move 10 cm
      output = pidControlForward(left_encoder_val, right_encoder_val);
      md.setSpeeds(left_speed+output,right_speed-output);
      if(right_encoder_val >= actual_distance) {
        md.setBrakes(375, 400);
        delay(2000);
        Serial.println("left_encoder_val = ");
        Serial.println(left_encoder_val);
      
        Serial.println("right_encoder_val = ");
        Serial.println(right_encoder_val);
        
        right_encoder_val = 0; 
        left_encoder_val = 0;
        r = false;
        wall = false;
        md.setSpeeds(0,0);
    }

}

void move_F(int left_speed, int right_speed){
      int output = pidControlForward(left_encoder_val, right_encoder_val);
      md.setSpeeds(left_speed+output,right_speed-output);
}

// Degree - Number of Degree u want to rotate/ It will rotate right
void rotate(int degree){
      int output;
      float dis = degree / 90.0;
      int left_speed = 222;
      int right_speed = 200;
      float actual_distance = (dis*405) - (5*dis);
      output = pidControlForward(left_encoder_val, right_encoder_val);
      md.setSpeeds(left_speed+output,-right_speed+output);
      if(right_encoder_val >= actual_distance){
        md.setBrakes(375, 400);
        right_encoder_val = 0; 
        left_encoder_val = 0;
        r = true;
      }
      
}
