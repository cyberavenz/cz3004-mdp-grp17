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

// For Global Variable
int right_encoder_val = 0, left_encoder_val = 0;
const byte numChars = 32;
char commands[numChars];
char command;
boolean newData = false; 
int error, prevError;
float integral, derivative, output;
String inputCommand = "";
String value = "";
bool stringComplete = false;

//Function Decleration
void RightEncoderInc(){right_encoder_val++;}
void LeftEncoderInc(){left_encoder_val++;}
int pidControlForward(int left_encoder_val, int right_encoder_val);
int pidControlTurn(int left_encoder_val, int right_encoder_val);
int getDistance(SharpIR sensor, int offset);
void rotateR(int degree);
void rotateL(int degree);

void setup() {
    Serial.begin(9600);
    md.init();
    PCintPort::attachInterrupt(motor_R_encoder, RightEncoderInc, FALLING);
    PCintPort::attachInterrupt(motor_L_encoder, LeftEncoderInc, FALLING);
    inputCommand.reserve(200);
}

void loop() {

  if(stringComplete){
    switch(inputCommand[0]){
      case 'F': moveForward(value.toInt(),222,200); break;
      case 'L': rotateL(value.toInt()); break;
      case 'R': rotateR(value.toInt()); break;
      default: inputCommand = "";
    }
    inputCommand = "";
    stringComplete = false;
    value = "";
  }
}

int pidControlForward(int left_encoder_val, int right_encoder_val){
  int pwmL = testSpeed, pwmR = testSpeed;
  float kp = 38;
  float ki = 1;
  float kd = 1;
  integral += error;
  derivative = error - prevError;
  
  output = kp*error + ki*integral + kd*derivative;
  prevError = error;

  pwmR = output;
  return pwmR;
}

int pidControlTurn(int left_encoder_val, int right_encoder_val){
  int pwmL = testSpeed, pwmR = testSpeed;
  float kp = 50;
  float ki = 2;
  float kd = 2;
  integral += error;
  derivative = error - prevError;
  
  output = kp*error + ki*integral + kd*derivative;
  prevError = error;

  pwmR = output;
  return pwmR;
}

void moveForward(int distance,int left_speed,int right_speed){
      int output;
      float actual_distance = (distance*323) - (30*distance); //323 is the tick required to move 10 cm
      while(right_encoder_val < actual_distance) {
              output = pidControlForward(left_encoder_val, right_encoder_val);
              md.setSpeeds(left_speed+output,right_speed-output);
      }
      md.setBrakes(375, 400);
      delay(1000);
      right_encoder_val = 0;
      left_encoder_val = 0;
      sendSensors();
}

void sendSensors() {
  int fl,fc,fr,r,bs,bl;
  fl = getDistance(FL, 9);
  fc = getDistance(FC, 4);
  fr = getDistance(FR, 7);
   r = getDistance(R, 6);
  bs = getDistance(BS, 5);
  bl = getDistance();
  Serial.print("@a");
  Serial.print(fl);
  Serial.print("|");
  Serial.print(fc);
  Serial.print("|");
  Serial.print(fr);
  Serial.print("|");
  Serial.print(r);
  Serial.print("|");
  Serial.print(bs);
  Serial.print("|");
  Serial.print(bl);
  Serial.println("!");
}

int getDistance(SharpIR sensor, int offset) {
  double sum = 0;
  double average = 0; 
  
  for (int i = 0; i < 10; i++) {
    sum = sum + sensor.distance() - offset;
  }
  delay(200);
  for (int i = 0; i < 10; i++) {
    sum = sum + sensor.distance() - offset;
  }
  average = (sum / 20);
  return round(average/10);
}

double getDistance(){
  double sum = 0;
  double average = 0;
  
  for (int i = 0; i < 10; i++) {
    sum = sum + BL.distance();
  }
  delay(200);
  for (int i = 0; i < 10; i++) {
    sum = sum + BL.distance();
  }
  average = (sum / 20);
  
  return average + 4;
}

void rotateR(int degree){
      int output;
      float dis = degree / 90.0;
      int left_speed = 222;
      int right_speed = 200;
      float actual_distance = (dis*390) - (5*dis);
      while (right_encoder_val < actual_distance){
        output = pidControlForward(left_encoder_val, right_encoder_val);
        md.setSpeeds(left_speed+output,-right_speed+output);
      }
        md.setBrakes(375, 400);
        delay(2000);     
        left_encoder_val = 0;
        right_encoder_val = 0;
        sendSensors();
}

void rotateL(int degree){
      int output;
      float dis = degree / 90.0;
      int left_speed = 220;
      int right_speed = 190;
      float actual_distance = (dis*400)-(10*dis);
      while(left_encoder_val < actual_distance){
          output = pidControlTurn(left_encoder_val, right_encoder_val);
          md.setSpeeds(-(left_speed+output),right_speed-output);
      }
      md.setBrakes(375, 400);
      delay(2000);
      left_encoder_val = 0;
      right_encoder_val = 0;
      sendSensors();
}

void serialEvent(){
  while(Serial.available()){
    char inChar = (char)Serial.read();
    inputCommand += inChar;
    if(inChar == '\n'){
      char a = inputCommand[1];
      char b = inputCommand[2];
      value += a;
      value += b;
      stringComplete = true;
    }
  }
}
