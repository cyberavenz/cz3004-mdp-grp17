#include "DualVNH5019MotorShield.h"
#include "PinChangeInt.h"
#include "SharpIR.h"
#include <Queue.h>

//////////////////////////////////////
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
boolean newData = false; 
int error, prevError;
float integral, derivative, output;
String inputCommand = "";
bool stringComplete = false;
struct Command {
  char command;
  String value = "";
};

DataQueue<Command> commandQueue(20);

//==============================Function Decleration============================
void RightEncoderInc(){right_encoder_val++;}
void LeftEncoderInc(){left_encoder_val++;}
int pidControlForward(int left_encoder_val, int right_encoder_val);
int pidControlTurn(int left_encoder_val, int right_encoder_val);
int getDistance(SharpIR sensor, int offset);
void rotateR(int degree);
void rotateL(int degree);

//=======================End of Function Decleration============================

//=================================Setup========================================
void setup() {
    Serial.begin(9600);
    md.init();
    PCintPort::attachInterrupt(motor_R_encoder, RightEncoderInc, FALLING);
    PCintPort::attachInterrupt(motor_L_encoder, LeftEncoderInc, FALLING);
    inputCommand.reserve(200);
}
//=================================End of Setup=================================

//====================================Loop======================================
void loop() {

  if(stringComplete){
    while(!commandQueue.isEmpty()){
      struct Command com;
      com = commandQueue.dequeue();
      switch(com.command){
        case 'S': sendSensors(); break;
        case 'F': moveForward(com.value.toInt(),RUN_SPEED,RUN_SPEED); break;
        case 'L': rotateL(com.value.toInt()); break;
        case 'R': rotateR(com.value.toInt()); break;
//        case 'C': checkAlignmentOne(); break;
//        case 'V': checkAlignmentTwo(); break;
        default: inputCommand = ""; 
      }
    }
    stringComplete = false;
  }
}
//=========================End of Loop==========================================

//===================================Turn PID===================================
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
//=========================End of Turn PID======================================

//=========================Move Forward Codes===================================
void moveForward(int distance,int left_speed,int right_speed){
      int output;
      float actual_distance = (distance*300) - (30*distance); //323 is the tick required to move 10 cm
      while(right_encoder_val < actual_distance) {
              output = pidControlForward(left_encoder_val, right_encoder_val);
              md.setSpeeds(left_speed+output,right_speed-output);
      }
      md.setBrakes(337, 400);
      delay(20);
      right_encoder_val = 0;
      left_encoder_val = 0;
      
      int fl = getDistanceinGrids(getDistance(sensorRead(20, FL), FL, 0), FL);
      int fr = getDistanceinGrids(getDistance(sensorRead(20, FR), FR, 0), FR);
      
      if (fl == 0 && fr == 0) {
//      if ((fl == 0 && fr == 0) || (fl == 1 && fr == 1)) {
        checkAlignmentOne();
        delay(20);
        checkAlignmentTwo();
        return;
      }
//      sendSensors();
}
//=========================End of Move Forward Codes============================

//=========================Rotate Right Codes===================================
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
      delay(20);     
      left_encoder_val = 0;
      right_encoder_val = 0;
      int fl = getDistanceinGrids(getDistance(sensorRead(20, FL), FL, 0), FL);
      int fr = getDistanceinGrids(getDistance(sensorRead(20, FR), FR, 0), FR);
      
      if (fl == 0 && fr == 0) {
//      if ((fl == 0 && fr == 0) || (fl == 1 && fr == 1)) {
        checkAlignmentOne();
        delay(20);
        checkAlignmentTwo();
        return;
      }
//        sendSensors();
}
//=========================End of Rotate Right Codes============================

//=========================Rotate Left Codes====================================
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
      delay(20);
      left_encoder_val = 0;
      right_encoder_val = 0;
      int fl = getDistanceinGrids(getDistance(sensorRead(20, FL), FL, 0), FL);
      int fr = getDistanceinGrids(getDistance(sensorRead(20, FR), FR, 0), FR);
      
      if (fl == 0 && fr == 0) {
//      if ((fl == 0 && fr == 0) || (fl == 1 && fr == 1))
        checkAlignmentOne();
        delay(20);
        checkAlignmentTwo();
        return;
      }
}
//=========================End of Rotate Left Codes=============================

//=========================Send Sensor String Codes=============================
void sendSensors() {
  int fl,fc,fr,r,bs,bl;
  fl = getDistanceinGrids(getDistance(sensorRead(40, FL), FL, 0), FL);
  fc = getDistanceinGrids(getDistance(sensorRead(40, FC), FC, 0), FC);
  fr = getDistanceinGrids(getDistance(sensorRead(40, FR), FR, 0), FR);
   r = getDistanceinGrids(getDistance(sensorRead(40, R), R, 0), R);
  bs = getDistanceinGrids(getDistance(sensorRead(40, BS), BS, 0), BS);
  bl = getDistanceinGrids(getDistance(sensorRead(40, BL), BL, 0), BL);
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
//=========================End of Send Sensor String Codes======================

//=========================Alignment for Rotation Codes=========================
void checkAlignmentOne(){
  double error = getRotError();
  while (error != 0) {
//  while (!(error > -0.1 && error < 0.1)) {
    calibrateRot(error);
    error = getRotError();
  }
  Serial.print("DONE");
  delay(20);
  right_encoder_val = 0;
  left_encoder_val = 0;
}

double getRotError(){

  double error = 0;
  double L = getDistance(sensorRead(20, FL), FL, 1);
  double R = getDistance(sensorRead(20, FR), FR, 1) + 1;
  
  error = L-R;
  if(error < -7) {
    error = -7;
  }
  else if(error > 7) {
    error = 7;
  }
//  Serial.println(error);
  return error;
}

void calibrateRot(double error){
  if(error > 0) {
    moveLeft(error);
  }
  else if(error < 0) {
    moveRight(error);
  }
  else {
    md.setBrakes(375, 400);
  }
}

//======Calibrate Right======
void moveRight(double error){
  md.setSpeeds(-225, 250);
  delay(abs(error*20));
  md.setBrakes(375, 400);
  delay(20);
}

//======Calibrate Left=======
void moveLeft(double error){
  md.setSpeeds(225, -250);
  delay(abs(error*20));
  md.setBrakes(375, 400);
  delay(20);
}
//=========================End of Alignment for Rotation Codes==================

//=========================Alignment for Wall Distance Codes====================
void checkAlignmentTwo(){
  bool error = getDistError();
  while (error) {
    error = getDistError();
  }
//  Serial.print("DONE");
  delay(20);
  right_encoder_val = 0;
  left_encoder_val = 0;
}

bool getDistError(){

  double errorL = 0;
  double errorR = 0;
  double L = getDistance(sensorRead(20, FL), FL, 1);
  double R = getDistance(sensorRead(20, FR), FR, 1);

  errorL = 3 - L;
  errorR = 3 - R;

  if(errorL < -3) {
    errorL = -3;
  }
  else if(errorL > 3) {
    errorL = 3;
  }

  if(errorR < -3) {
    errorR = -3;
  }
  else if(errorR > 3) {
    errorR = 3;
  }
  
  if (errorL == 0 && errorR == 0) {
    return 0;
  }
  else {
      calibrateDistR(errorR);
      calibrateDistL(errorL);
  }
  return 1;
}

void calibrateDistR(double errorR){
  if(errorR > 0) {
    spinRightB(errorR);
  }
  else if(errorR < 0) {
    spinRightF(errorR);
  }
  else {
    md.setBrakes(375, 400);
  }
}

void calibrateDistL(double errorL){
  if(errorL > 0) {
    spinLeftB(errorL);
  }
  else if(errorL < 0) {
    spinLeftF(errorL);
  }
  else {
    md.setBrakes(375, 400);
  }
}

//======Spin Right======
void spinRightF(double error){
  md.setSpeeds(0, 100);
  delay(abs(50/error));
  md.setBrakes(375, 400);
}

void spinRightB(double error){
  md.setSpeeds(0, -100);
  delay(abs(50/error));
  md.setBrakes(375, 400);
}

//======Spin Left=======
void spinLeftF(double error){
  md.setSpeeds(150, 0);
  delay(abs(50/error));
  md.setBrakes(375, 400);
}

void spinLeftB(double error){
  md.setSpeeds(-100, 0);
  delay(abs(50/error));
  md.setBrakes(375, 400);
}
//=========================End of Alignment for Wall Distance Codes=============

//==============================Get Sensor Codes================================
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

int getDistance(int reading, int sensor, bool cali){
  float cm;

  switch (sensor) {
    case FL:
      cm = 6088 / (reading  + 7); // 21-26=2  11-17=1  -6=0
      break;
    case FC: 
      cm = 6088 / (reading  + 7); // 18-21=2  10-14=1  -5=0
      break;
    case FR:
      cm = 6088 / (reading  + 7); // 19-23=2  10-14=1  -5=0
      break;
    case R:
      cm = 6088 / (reading  + 7); // 17-22=2   8-13=1  -2=0
      break;
    case BS:
      cm = 6088 / (reading  + 7); // 17-27=2   8-13=1  -2=0
      break;
    case BL:
      cm = 15500.0 / (reading + 29) - 4; // 46-=5 36-40=4  26-31=3  17-22=2
      break;
    default:
      return -1;
  }
  
  return cm-10;
}
//=========================End of Get Sensor Codes==============================

//===============================Get Grid Codes=================================
int getDistanceinGrids(int reading, int sensor){
  int grid;
  switch(sensor){
    case FL: 
      if(reading >= 21){
        grid = 2;
      }else if(reading >= 11){
        grid = 1;
      }else if(reading <= 6){
        grid = 0;
      } break;
    case FC: 
      if(reading >= 14){
        grid = 2;
      }else if(reading >= 9){
        grid = 1;
      }else if(reading <= 5){
        grid = 0;
      }
      break;
    case FR: 
      if(reading >= 19){
        grid = 2;
      }else if(reading >= 10){
        grid = 1;
      }else if(reading <= 5){
        grid = 0;
      } break;
    case  R: 
      if(reading >= 17){
        grid = 2;
      }else if(reading >= 8){
        grid = 1;
      }else if(reading <= 2){
        grid = 0;
      } break;
    case BS: 
      if(reading >= 14){
        grid = 2;
      }else if(reading >= 8){
        grid = 1;
      }else if(reading <= 2){
        grid = 0;
      } break;
    case BL:  
      if (reading >= 41 ){
        grid = 5;
      }else if(reading >= 36){
        grid = 4;
      }else if(reading >= 26){
        grid = 3;
      }else if(reading >= 17){
        grid = 2;
      }else if(reading <= 16){
        grid = 2;
      } break;
    default: return -1;
  }
  return grid;
}
//=========================End of Get Grid Codes================================

//====================Serial Communication Codes================================
void serialEvent(){
  while(Serial.available()){
    char inChar = (char)Serial.read();
    if(inChar == '|'){
      struct Command c;
      c.command = inputCommand[0];
      c.value += inputCommand[1];
      c.value += inputCommand[2];
      delay(50);
      inputCommand = "";
      commandQueue.enqueue(c);
    }else{
      inputCommand += inChar;
    }
    
    
    if(inChar == '\n'){
      struct Command c;
      c.command = inputCommand[0];
      c.value += inputCommand[1];
      c.value += inputCommand[2];
      delay(20);
      inputCommand = "";
      commandQueue.enqueue(c);
      stringComplete = true;
    }
  }
}
//==================End of Serial Communication Codes===========================
