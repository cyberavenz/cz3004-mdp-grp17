#include "DualVNH5019MotorShield.h"
#include "PinChangeInt.h"

DualVNH5019MotorShield md;

#define motor_R_encoder 5  //Define pins for motor encoder input
#define motor_L_encoder 3

#define MAX_SPEED 400
#define SPEED 200

#define LF A0   //PS1
#define L  A1   //PS2
#define CF A2  //PS3
#define R  A3   //PS4
#define RF A4   //PS5
#define SR 0    //NOT PS6
#define LR 1    //NOT 

int right_encoder_val = 0, left_encoder_val = 0;
void RightEncoderInc(){right_encoder_val++;}
void LeftEncoderInc(){left_encoder_val++;}

void setup()
{
  Serial.begin(115200);
  md.init();
  PCintPort::attachInterrupt(motor_R_encoder, RightEncoderInc, CHANGE);
  PCintPort::attachInterrupt(motor_L_encoder, LeftEncoderInc, CHANGE);
}

void loop()
{
    moveFront(100, 100);
}

void readSensor(){
    int SRdistance;
    int SRreading = analogRead(SR);
    Serial.print("SR:");
    Serial.println(SRreading);
}

//n is right wheel, m is left wheel
void moveFront(float n, float m){
    m = m*0.983;
    md.setSpeeds(n, m);
}

void testMovement(){
    md.setM1Speed(100);
    md.setM2Speed(0);
    delay(2000);
    md.setM1Speed(0);
    md.setM2Speed(100);
    delay(2000);
    md.setSpeeds(100,100);
    delay(2000);
    md.setSpeeds(-100,-100);
    delay(2000);
}
