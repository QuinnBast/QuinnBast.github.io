---
title: "Reading Quaternion data from a 9DoF Sensor"
date: 2025-09-06 19:00
description: "Learn how to hook up a 9DoF sensor to an Arduino, get the sensor data, and visualize it."
path: "microelectronics"
tags:
- Electronics
- C++
- Arduino
---
# Getting Started

As a hobbyist, I like to tinker with electronics now and then, and I had an itch to try to use a motion sensor chip.
I shopped around and found The SparkFun 9DoF IMU Breakout (ICM-20948). This chip is a powerful sensor that integrates a 3-axis gyroscope, 3-axis
accelerometer, and 3-axis magnetometer. It’s great for projects that involve motion tracking, navigation, and object
orientation detection. I wanted to use this sensor to detect hand motions while in the air.
This sensor is created by InvenSense and has an integrated on-board Digital Motion Processor (DMP), which will automatically
report the quaternions among other data.

This blog post outlines the electronics setup, and code to get started.

## I2C vs. SPI

For my project, the idea was to use 5 of these sensors to collect motion data from a user's hand.
However, as we will see later, the Arduino cannot read from 5 sensors fast enough over I2C to get reliable data.
This can be mitigated by using SPI, but this guide will focus on I2C.

I2C is a communication protocol for electronic devices. It works by enabling a clock and a bus where each device has an address on the bus.
The master broadcasts the address of who it wants to talk to and only that device can talk. The [SparkFun Qwiic](https://www.sparkfun.com/qwiic)
connect system makes it extremely easy to setup I2C connections for prototyping and thus this guide focuses on I2C setup.

Before we get started, make sure you have the following parts:

- [SparkFun 9DoF IMU Breakout - ICM-20948 (Qwiic)](https://www.sparkfun.com/products/15335)
- An Arduino (I used an Arduino Mega because the DMP requires a lot of program space and a Nano wasn't enough.)
- [SparkFun Qwiic Cable](https://www.sparkfun.com/qwiic-cable-100mm.html)
- [SparkFun Qwiic Jumpers](https://www.sparkfun.com/qwiic-cable-breadboard-jumper-4-pin.html)
- Optional: [SparkFun Qwiic mMux breakout](https://www.sparkfun.com/sparkfun-qwiic-mux-breakout-8-channel-tca9548a.html)
- Breadboard and some basic tools (optional)

## Setting Up the ICM-20948

### Wiring Diagram

Here’s how to connect the breakout board to the Arduino via I2C:

![Hookup Diagram for Arduino Mega](./images/ArduinoHookupImuSensor.png)

The reason we need the Mux is because the 9DoF IMUs are only configurable to have 2 addresses, either 0x67 or 0x68.
However, because we have 5 sensors, we need to be able to talk to each sensor in another way.
Usually with I2C you can just connect all sensors in a row, but if two devices have the same address you cannot.
Using the Mux allows us to bypass this problem completely.

## Compiling to the Arduino

To interface with the ICM-20948, you’ll need to install the following libraries:

- [SparkFun_ICM-20948_ArduinoLibrary](https://github.com/sparkfun/SparkFun_ICM-20948_ArduinoLibrary)
- [SparkFun_I2C_Mux_Arduino_Library](https://github.com/sparkfun/SparkFun_I2C_Mux_Arduino_Library)

Both of these libraries are available to download directly from the Arduino IDE.

1. Open the Arduino IDE.
2. Open the library Manager
3. Search for the libraries listed above.
4. Click on the **Install** button for the library.

### Basic Arduino Sketch

Luckilly enouch, both of these libraries have some examples that we can use.
Specifically, for testing the ICM-20948 sensor, I made heavy use of [these exmaples](https://github.com/sparkfun/SparkFun_ICM-20948_ArduinoLibrary/tree/main/examples).

For our case, a simple sketch that reads the sensor data from a single sensor looks like this:

NOTE: [This was heavily inspired by this example](https://github.com/sparkfun/SparkFun_ICM-20948_ArduinoLibrary/blob/main/examples/Arduino/Example6_DMP_Quat9_Orientation/Example6_DMP_Quat9_Orientation.ino)

```cpp
#include "ICM_20948.h"
#include <SparkFun_I2C_Mux_Arduino_Library.h>

// The value of the last bit of the I2C address.
// On the SparkFun 9DoF IMU breakout the default is 1, and when the ADR jumper is closed the value becomes 0
#define AD0_VAL 1

// Create array of sensors. Just 2 for now.
ICM_20948_I2C *sensors[2];

// Init a Mux object
QWIICMUX sensorMux;

void setup()
{
  Serial.begin(500000); // Start the serial console
  delay(1000);
  
  sensors[0] = new ICM_20948_I2C();
  sensors[1] = new ICM_20948_I2C();
  
  Wire.begin();
  Wire.setClock(400000);

  // Enable our mux
  if (sensorMux.begin() == false)
  {
    Serial.println("Mux not detected. Freezing...");
    while (1);
  }
  
  // Set the Mux to read from port 0
  sensorMux.setPort(0);

  bool initialized = false;
  while (!initialized)
  {
    // Init the first sensor.
    sensors[0]->begin(WIRE_PORT, AD0_VAL);
  }

  bool success = true; // Use success to show if the DMP configuration was successful

  // Configure all of the sensor features that we want.
  // Enable the Digital Motion Processor
  success &= (sensors[0]->initializeDMP() == ICM_20948_Stat_Ok);
  // Collect quaternion data
  success &= (sensors[0]->enableDMPSensor(INV_ICM20948_SENSOR_ORIENTATION) == ICM_20948_Stat_Ok);
  // Configure the polling rate for the quaternion data to be the max (225Hz)
  success &= (sensors[0]->setDMPODRrate(DMP_ODR_Reg_Quat9, 0) == ICM_20948_Stat_Ok);
  // Enable the FIFO queue
  success &= (sensors[0]->enableFIFO() == ICM_20948_Stat_Ok);
  // Enable the Digital Motion Processor and reset the sensor.
  success &= (sensors[0]->enableDMP() == ICM_20948_Stat_Ok);
  success &= (sensors[0]->resetDMP() == ICM_20948_Stat_Ok);
  success &= (sensors[0]->resetFIFO() == ICM_20948_Stat_Ok);

  // Check success
  if (!success)
  {
    Serial.println(F("Enable DMP failed!"));
    Serial.println(F("Please check that you have uncommented line 29 (#define ICM_20948_USE_DMP) in ICM_20948_C.h..."));
    while (1); // Do nothing more
  }
}

void loop()
{
  sensorMux.setPort(0);
  
  // Read any DMP data waiting in the FIFO
  icm_20948_DMP_data_t data;
  sensors[0]->readDMPdataFromFIFO(&data);

  // Check the sensor status if we were able to read data from it.
  if ((sensors[0]->status == ICM_20948_Stat_Ok) || (sensors[0]->status == ICM_20948_Stat_FIFOMoreDataAvail)) // Was valid data available?
  {
    // Ensure the data includes quat9 data.
    if ((data.header & DMP_header_bitmap_Quat9) > 0)
    {
      // Q0 value is computed from this equation: Q0^2 + Q1^2 + Q2^2 + Q3^2 = 1.
      // In case of drift, the sum will not add to 1, therefore, quaternion data need to be corrected with right bias values.
      // The quaternion data is scaled by 2^30.

      // Scale to +/- 1
      double q1 = ((double)data.Quat9.Data.Q1) / 1073741824.0; // Convert to double. Divide by 2^30
      double q2 = ((double)data.Quat9.Data.Q2) / 1073741824.0; // Convert to double. Divide by 2^30
      double q3 = ((double)data.Quat9.Data.Q3) / 1073741824.0; // Convert to double. Divide by 2^30
      double q0 = sqrt(1.0 - ((q1 * q1) + (q2 * q2) + (q3 * q3)));

      // Output the Quaternion data
      Serial.print(F("{\"quat_w\":"));
      Serial.print(q0, 3);
      Serial.print(F(", \"quat_x\":"));
      Serial.print(q1, 3);
      Serial.print(F(", \"quat_y\":"));
      Serial.print(q2, 3);
      Serial.print(F(", \"quat_z\":"));
      Serial.print(q3, 3);
      Serial.println(F(", \"id\": 0 }"));
    }
  }
}
```

This sketch should set the Mux to read the sensor on port 0, enable that sensor, and start reading sensor data from it!

### Upload and Run the Code

1. Copy the above code into a new Arduino sketch.
2. Select your Arduino board and COM port from the **Tools** menu.
3. Click the upload button to flash the code to your board.
4. Open the Serial Monitor (set to 115200 baud rate) to see the quaternion data streaming in real time.

## Visualizing Quaternion Data

Now that we have the quaternion data coming in, we can start visualizing the data.
Luckilly, this has been done before! By reading through the example code, I found [this amazing tool](https://github.com/ZaneL/quaternion_sensor_3d_nodejs)!

This tool runs a Node.JS program which opens a serial monitor to the ardiuno.
The Node.js server then takes all message from the serial port and sends them on a websock to a locally hosted website.
The website processes the collected data and renders a rectangle on the screen that corellates to the represented quaternion!

Using the sketch above, the program should just work to be able to display our sensor on a website!
To do this, I cloned this repository and ran the following steps:

```bash
npm install
node index.js
```

Once running, I was able to open my web browser and visualize my sensor data on the screen!

## Conclusion

With the SparkFun 9DoF IMU Breakout and Arduino, you can accurately track motion in 3D space. This setup provides a
reliable way to work with orientation data for projects like robotics, drones, and custom controllers. Experiment with
the sensor’s capabilities, such as its accelerometer and gyroscope readings, to unlock more potential for your projects.

## Future Improvements

Future improvements for this project could include the following:

- **Support for Multiple Sensors**: Extend the example to incorporate data collection and visualization from multiple
  sensors connected through the Mux.
- **Data Filtering and Calibration**: Integrate advanced data filtering techniques such as complementary or Kalman
  filters to improve the accuracy of the orientation data.
- **Real-Time Visualization Enhancements**: Develop or use more interactive 3D visualization tools that allow dynamic
  adjustments, such as scaling, rotation, or zooming.
- **Save Data to File**: Add functionality to save the quaternion data to a local file for further analysis and
  debugging.
- **Wireless Communication**: Add support for wireless communication modules (e.g., Bluetooth or Wi-Fi) to transmit data
  to a visualization device without requiring a wired connection.
- **Explore More Features of the IMU**: Enable and visualize additional IMU features like accelerometer, gyroscope, and
  magnetometer data for more comprehensive motion tracking.
- **Gesture Detection**: The collected quaternion data will have definite 'tells' when a user is taking a specific action like pointing, waving, and more.
  An AI could be trained on this data to be able to detect and classify certain movements and motions.