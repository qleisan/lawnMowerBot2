#include <SPI.h>
#include <Adb.h>
#include <Servo.h>

/* 
 This is really simple example code to get you some basic
 functionality with the MonsterMoto Shield. The MonsterMote uses
 two VNH2SP30 high-current full-bridge motor drivers.
 
 Use the motorGo(uint8_t motor, uint8_t direct, uint8_t pwm) 
 function to get motors going in either CW, CCW, BRAKEVCC, or 
 BRAKEGND. Use motorOff(int motor) to turn a specific motor off.
 
 The motor variable in each function should be either a 0 or a 1.
 pwm in the motorGo function should be a value between 0 and 255.
 */
#define BRAKEVCC 0
#define CW   1
#define CCW  2
#define BRAKEGND 3
#define CS_THRESHOLD 100

/*  VNH2SP30 pin definitions
 xxx[0] controls '1' outputs
 xxx[1] controls '2' outputs */
int inApin[2] = {7, 4};  // INA: Clockwise input
int inBpin[2] = {8, 9}; // INB: Counter-clockwise input
int pwmpin[2] = {5, 6}; // PWM input
int cspin[2] = {2, 3}; // CS: Current sense ANALOG input
int enpin[2] = {0, 1}; // EN: Status of switches output (Analog pin)

//--------------------------------------------------

int ledPin = 14; //FIXME                // LED connected to digital pin 13
int statpin = 13;

Connection * connection;
Servo myservo;  // create servo object to control a servo
int rotDir[2] = {0,0};


enum colosrs_t {black, blue, green, cyan, red, purple, yellow, white, asdasd};

enum movementStateE  {rotCCW, rotCW, FWD0, FWD1, FWD2};
enum cutStatueE {CUTOFF, CUTON};

movementStateE movementState;
cutStatueE cutState;

// Event handler for shell connection; called whenever data sent from Android to Microcontroller
void adbEventHandler(Connection * connection, adb_eventType event, uint16_t length, uint8_t * data)
{
  // In this example Data packets contain three bytes: one for the state of each LED
  if (event == ADB_CONNECTION_RECEIVE)
  {
    const char * ch_p;

    data[length]=0;
    ch_p = (const char *) data;

    if (strcmp(ch_p, "ledon") == 0)
    {
      Serial.println("adb: ledon");
      digitalWrite(ledPin, HIGH);
    } 
    else if (strcmp(ch_p, "ledoff") == 0)
    {
      Serial.println("adb: ledoff");
      digitalWrite(ledPin, LOW);
    }
    else if (strcmp(ch_p, "cuton") == 0)
    {
      Serial.println("adb: cuton");
       myservo.write(180);
       cutState = CUTON;
    }
    else if (strcmp(ch_p, "cutoff") == 0)
    {
      if (cutState == CUTON)
      {
        Serial.println("adb: cutoff");
        myservo.write(50);
      } else {
        Serial.println("adb: cutoff-2");
        myservo.write(0);
      }
      cutState = CUTOFF;
    }
    else if (strcmp(ch_p, "fwd2") == 0)
    {
      Serial.println("adb: fwd2");
      motorGo(0, CCW, 90);
      motorGo(1, CW, 90);
      movementState = FWD2;
    }
    else if (strcmp(ch_p, "fwd1") == 0)
    {
      Serial.println("adb: fwd1");
      motorGo(0, CCW, 45);
      motorGo(1, CW, 45);
      movementState = FWD1;
    }
    else if (strcmp(ch_p, "fwd0") == 0)
    {
      Serial.println("adb: fwd0");
      motorGo(0, CCW, 0);
      motorGo(1, CW, 0);
      movementState = FWD0;
    }
    else if (strcmp(ch_p, "ccw") == 0)
    {
      if (movementState == FWD0) {
        Serial.println("adb: ccw");
        motorGo(0, CW, 30);
        motorGo(1, CW, 30);
        movementState = rotCCW;
      } else if (movementState == FWD1)
      {
        Serial.println("adb: ccw-2");
        motorGo(0, CW, 50);
      } else if (movementState == FWD1)
      {
        Serial.println("adb: ccw-3");
        motorGo(0, CW, 100);
      } else {
        Serial.println("adb: ccw-rejected");
      }
 }
    else if (strcmp(ch_p, "cw") == 0)
    {
      
      if (movementState == FWD0) {
        Serial.println("adb: cw");
        motorGo(0, CCW, 30);
        motorGo(1, CCW, 30);
        movementState = rotCW;
      } else if (movementState == FWD1)
      {
        Serial.println("adb: cw-2");
        motorGo(1, CW, 50);
      } else if (movementState == FWD1)
      {
        Serial.println("adb: cw-3");
        motorGo(1, CW, 100);
      } else {
        Serial.println("adb: cw-rejected");
      }   
    }
    else
    {
      Serial.print("adb: unrecognized command (");
      Serial.print(length);
      Serial.print(") ");
      Serial.println(ch_p);
    }  
  }
}

void setup()
{
  myservo.attach(22);  // attaches the servo on pin 22 to the servo object 
  
  pinMode(statpin, OUTPUT);
  
  // Initialize digital pins as outputs
  for (int i=0; i<2; i++)
  {
    pinMode(inApin[i], OUTPUT);
    pinMode(inBpin[i], OUTPUT);
    pinMode(pwmpin[i], OUTPUT);
  }
  // Initialize braked
  motorOff(0);
  motorOff(1);
  movementState = FWD0;
  
  pinMode(ledPin, OUTPUT);      // sets the digital pin as output

  // Init serial port for debugging
  Serial.begin(57600);  
  Serial.print("Arming cut motor (15 sec)\n");

  myservo.write(90);                  // sets the servo position according to the scaled value 
  delay(15000);                           // waits for the servo to get there 
  myservo.write(50);  
  delay(5000);
  cutState = CUTOFF;  
  Serial.print("Init ADB subsystem\n");

  // Init the ADB subsystem.  
  ADB::init();

  // Open an ADB stream to the phone's shell. Auto-reconnect. Use any unused port number eg:4568
  connection = ADB::addConnection("tcp:4568", true, adbEventHandler);  
}

void myPause()
{
  unsigned int j;

  for (j=0;j<30000;j++) {
    ADB::poll();
  }
  Serial.print("myPause - exit\n");  
}

void loop()
{
  // Poll the ADB subsystem.
  ADB::poll();

  /*
  motorGo(0, CCW, 90);
  motorGo(1, CW, 90);
  delay(10000);
  motorGo(0, CCW, 0);
  motorGo(1, CCW, 0);
  delay(10000);
  */
  
  if ((analogRead(cspin[0]) < CS_THRESHOLD) && (analogRead(cspin[1]) < CS_THRESHOLD))
  digitalWrite(statpin, HIGH);
  
}

void motorOff(int motor)
{
  // Initialize braked
  for (int i=0; i<2; i++)
  {
    digitalWrite(inApin[i], LOW);
    digitalWrite(inBpin[i], LOW);
  }
  analogWrite(pwmpin[motor], 0);
}

/* motorGo() will set a motor going in a specific direction
 the motor will continue going in that direction, at that speed
 until told to do otherwise.
 
 motor: this should be either 0 or 1, will selet which of the two
 motors to be controlled
 
 direct: Should be between 0 and 3, with the following result
 0: Brake to VCC
 1: Clockwise
 2: CounterClockwise
 3: Brake to GND
 
 pwm: should be a value between ? and 1023, higher the number, the faster
 it'll go
 */
void motorGo(uint8_t motor, uint8_t direct, uint8_t pwm)
{
  if (motor <= 1)
  {
    if (direct <=4)
    {
      // Set inA[motor]
      if (direct <=1)
        digitalWrite(inApin[motor], HIGH);
      else
        digitalWrite(inApin[motor], LOW);

      // Set inB[motor]
      if ((direct==0)||(direct==2))
        digitalWrite(inBpin[motor], HIGH);
      else
        digitalWrite(inBpin[motor], LOW);

      analogWrite(pwmpin[motor], pwm);
    }
  }
}


