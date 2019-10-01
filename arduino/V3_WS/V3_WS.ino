#include "DualVNH5019MotorShield.h"
#include "PinChangeInt.h"
#include "SharpIR.h"

//////////////////////////////////
// Sensors

#define SRmodel 1080
#define LRmodel 20150

#define FL A0 // 
#define FC A1 //
#define FR A2 //
#define R A3 //
#define BS A4 //
#define BL A5 // Long


//////////////////////////////////////

DualVNH5019MotorShield md;

#define motor_R_encoder 11 //Define pins for motor encoder input
#define motor_L_encoder 3

#define MAX_SPEED 400
#define RUN_SPEED 300

// For Global Variable
int right_encoder_val = 0, left_encoder_val = 0;
const byte numChars = 32;
char commands[numChars];
char command;
boolean newData = false; 
int error, prevError;
float integral, derivative, output;
String inputCommand = "";
bool stringComplete = false;
String value = "";

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
      case 'S': sendSensors(); break;
      case 'F': moveForward(value.toInt(),RUN_SPEED,RUN_SPEED); break;
      case 'L': rotateL(value.toInt()); break;
      case 'R': rotateR(value.toInt()); break;
      case 'C': checkAlignmentOne(); break;
      default: inputCommand = ""; 
    }
      inputCommand = "";
      stringComplete = false;
      value = "";
  }
}


int pidControlForward(int left_encoder_val, int right_encoder_val){
  int pwmL = RUN_SPEED, pwmR = RUN_SPEED;
  float kp = 40;
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
  int pwmL = RUN_SPEED, pwmR = RUN_SPEED;
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
      float actual_distance = (distance*300) - (30*distance); //323 is the tick required to move 10 cm
      while(right_encoder_val < actual_distance) {
              output = pidControlForward(left_encoder_val, right_encoder_val);
              md.setSpeeds(left_speed+output,right_speed-output);
      }
      md.setBrakes(337, 400);
      delay(1000);
      right_encoder_val = 0;
      left_encoder_val = 0;
//      sendSensors();
}

void sendSensors() {
  int fl,fc,fr,r,bs,bl;
  fl = getDistance(sensorRead(20, FL), FL, 0);
  fc = getDistance(sensorRead(20, FC), FC, 0);
  fr = getDistance(sensorRead(20, FR), FR, 0);
   r = getDistance(sensorRead(20, R), R, 0);
  bs = getDistance(sensorRead(20, BS), BS, 0);
  bl = getDistance(sensorRead(20, BL), BL, 0);
  Serial.print("@t");
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


//=========================Calibrate Codes=====================================
double getError(){

  double error = 0;
  double L = getDistance(sensorRead(20, FL), FL, 1);
  double R = getDistance(sensorRead(20, FR), FR, 1);
  
  Serial.print("L: ");
  Serial.println(L);
  
  Serial.print("R: ");
  Serial.println(R);
  error = L-R;
  Serial.println(error);
  return error;
}

void calibrate(double error){
  if(error > 0) {
    moveRight(error);
  }
  else if(error < 0) {
    moveLeft(error);
  }
  else {
    md.setBrakes(375, 400);
  }
}

//=======================Calibrate Right========================================
void moveRight(double error){
  md.setSpeeds(400,-400);
  delay(abs(error*30));
  md.setBrakes(375, 400);
  delay(1000);
}

//=======================Calibrate Left=========================================
void moveLeft(double error){
  md.setSpeeds(-400,400);
  delay(abs(error*30));
  md.setBrakes(375, 400);
  delay(1000);
}

void checkAlignmentOne(){
  double error = getError();
    while (error > 0 || error < 0) {
      error = getError();
      calibrate(error);
    }
}

void insertionsort(int array[], int length) {
  int i, j;
  int temp;
  for (i = 1; i < length; i++) {
    for (j = i; j > 0; j--) {
      if (array[j] < array[j - 1]) {
        temp = array[j];
        array[j] = array[j - 1];
        array[j - 1] = temp;
      }
      else
        break;
    }
  }
}

int sensorRead(int n, int sensor) {
  int x[n];
  int i;
  int sum = 0;
  for (i = 0; i < n; i++) {
    x[i] = analogRead(sensor);
  }
  insertionsort(x, n);
  return x[n / 2];        //Return Median
}


int getDistance(int reading, int sensor, bool cali){
  float cm;

  switch (sensor) {
    case FL:
      cm = 6088 / (reading  + 7) - 2;
      break;
    case FC: 
      cm = 6088 / (reading  + 7) - 1;
      break;
    case FR:
      cm = 6088 / (reading  + 7) - 2;
      break;
    case R:
      cm = 6088 / (reading  + 7) - 1; //15500.0 / (reading + 29) - 5
      break;
    case BS:
      cm = 6088 / (reading  + 7);
      break;
    case BL:
      cm = 15500.0 / (reading + 29) - 4;
      break;
    default:
      return -1;
  }
  if (!cali) {
    return round(cm/10) - 1;
  }
  else {
    return cm - 10;
  }
}


void rotateR(int degree){
      int output;
      float dis = degree / 90.0;
      int left_speed = 222;
      int right_speed = 200;
      float actual_distance = (dis*380) - (5*dis);
      while (right_encoder_val < actual_distance){
        output = pidControlForward(left_encoder_val, right_encoder_val);
        md.setSpeeds(left_speed+output,-right_speed+output);
      }
        md.setBrakes(375, 400);
        delay(2000);     
        left_encoder_val = 0;
        right_encoder_val = 0;
//        sendSensors();
}

void rotateL(int degree){
      int output;
      float dis = degree / 90.0;
      int left_speed = 220;
      int right_speed = 190;
      float actual_distance = (dis*425)-(10*dis);
      while(left_encoder_val < actual_distance){
          output = pidControlTurn(left_encoder_val, right_encoder_val);
          md.setSpeeds(-(left_speed+output),right_speed-output);
      }
      md.setBrakes(375, 400);
      delay(2000);
      left_encoder_val = 0;
      right_encoder_val = 0;
//      sendSensors();
}

void serialEvent(){
  while(Serial.available()){
    char inChar = (char)Serial.read();
    inputCommand += inChar;
    if(inChar == '\n'){
      value += inputCommand[1];
      value += inputCommand[2];
      stringComplete = true;
    }
  }
}
