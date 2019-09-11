#include "DualVNH5019MotorShield.h"
#include "PinChangeInt.h"

DualVNH5019MotorShield md;

#define motor_R_encoder 11 //Define pins for motor encoder input
#define motor_L_encoder 3

#define MAX_SPEED 400
#define testSpeed 200

//For counting Encoder using interrupt
int right_encoder_val = 0, left_encoder_val = 0;
void RightEncoderInc(){right_encoder_val++;}
void LeftEncoderInc(){left_encoder_val++;}
float piControlForward(float left, float right);
void moveForward(int distance,int left_speed,int right_speed);
void rotate(int degree);

void setup() {
  Serial.begin(115200);
  md.init();
  PCintPort::attachInterrupt(motor_R_encoder, RightEncoderInc, FALLING);
  PCintPort::attachInterrupt(motor_L_encoder, LeftEncoderInc, FALLING);
}

// 200 for right is 222 for left
void loop() {
  rotate(850);
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
    }
}

// Degree - Number of Degree u want to rotate/ It will rotate right
void rotate(int degree){
      int output;
      int dis = degree / 90;
      int left_speed = 222;
      int right_speed = 200;
      float actual_distance = (dis*405) - (5*dis);
      output = pidControlForward(left_encoder_val, right_encoder_val);
      md.setSpeeds(left_speed+output,-right_speed+output);
      if(right_encoder_val >= actual_distance){
        md.setBrakes(375, 400);
        delay(2000);
        Serial.println("left_encoder_val = ");
        Serial.println(left_encoder_val);
      
        Serial.println("right_encoder_val = ");
        Serial.println(right_encoder_val);
        
        right_encoder_val = 0; 
        left_encoder_val = 0;
      }
}
