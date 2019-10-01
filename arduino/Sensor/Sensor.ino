#define SRmodel 1080
#define LRmodel 20150

#define FL A0 // 
#define FC A1 //
#define FR A2 //
#define  R A3 //
#define BS A4 //
#define BL A5 // Long

#define SR 0
#define LR 1


void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  int fl, fc, fr, r, bs, bl;
  
  fl = distanceInCM(sensorRead(20, FL), FL);
  fc = distanceInCM(sensorRead(20, FC), FC);
  fr = distanceInCM(sensorRead(20, FR), FR);
   r = distanceInCM(sensorRead(20, R), R);
  bs = distanceInCM(sensorRead(20, BS), BS);
  bl = distanceInCM(sensorRead(20, BL), BL);

  

  delay(1000);
  
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
  Serial.println("");
  
  
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

int distanceInCM(int reading, int sensor) {
  float cm;

  switch (sensor) {
    case FL:
      cm = 6088 / (reading  + 7) - 4;
      break;
    case FC: 
      cm = 6088 / (reading  + 7) - 3;
      break;
    case FR:
      cm = 6088 / (reading  + 7) - 2;
      break;
    case R:
      cm = 6088 / (reading  + 7) - 3; //15500.0 / (reading + 29) - 5
      break;
    case BS:
      cm = 6088 / (reading  + 7) - 3;
      break;
    case BL:
      cm = 15500.0 / (reading + 29) - 7;
      break;
    default:
      return -1;
  }
return round(cm/10) - 1;
}

//int distanceInGrids(int dis, int sensorType) {
//  int grids;
//  if (sensorType == SR) {
//    if (dis > 28) grids = 3;
//    else if (dis >= 10 && dis <= 19) grids = 1;
//    else if (dis >= 20 && dis <= 28) grids = 2;
//    else grids = -1;
//  }
//  else if (sensorType == LR) {
//    if (dis > 58) grids = 6;
//    else if (dis >= 12 && dis <= 22) grids = 1;
//    else if (dis >  22 && dis <= 27) grids = 2;
//    else if (dis >= 30 && dis <= 37) grids = 3;
//    else if (dis >= 39 && dis <= 48) grids = 4;
//    else if (dis >= 49 && dis <= 58) grids = 5;
//    else grids = -1;
//  }
//
//  return grids;
//}

//
//
//void sendSensors() {
//  int fl, fc, fr, r, bs, bl;
//  
//  fl = distanceInCM(sensorRead(20, FL), FL);
//  fc = distanceInCM(sensorRead(20, FC), FC);
//  fr = distanceInCM(sensorRead(20, FR), FR);
//   r = distanceInCM(sensorRead(20, R), R);
//  bs = distanceInCM(sensorRead(20, BS), BS);
//  bl = distanceInCM(sensorRead(20, BL),BL); 
//
//
//  Serial.print(distanceInGrids(fl, SR));
//  Serial.print("|");
//  Serial.print(distanceInGrids(fc, SR));
//  Serial.print("|");
//  Serial.print(distanceInGrids(fr, SR));
//  Serial.print("|");
//  Serial.print(distanceInGrids(r, SR));
//  Serial.print("|");
//  Serial.print(distanceInGrids(bs, SR));
//  Serial.print("|");
//  Serial.print(distanceInGrids(bl, LR));
//}



//int getDistance(SharpIR sensor, int offset) {
//  double sum = 0;
//  double average = 0; 
//  
//  for (int i = 0; i < 10; i++) {
//    sum = sum + sensor.distance();
//  }
//  delay(100);
//  for (int i = 0; i < 10; i++) {
//    sum = sum + sensor.distance();
//  }
//  average = (sum / 20) - offset;
////  return average;
//  return round(average/10);
//}


///////////////////////
