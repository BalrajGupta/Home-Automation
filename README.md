# Home Automation

A low-cost Home Automation project developed as part of B.Tech coursework.  
This system demonstrates how common electrical appliances can be controlled over Wi-Fi using a smartphone app and IoT hardware.

The goal is to build a **minimal, affordable, and practical automation setup** that can be used in homes, small offices, and labs.

---

## Table of Contents
- [Project Overview](#project-overview)
- [System Design](#system-design)
- [Hardware Used](#hardware-used)
- [How It Works](#how-it-works)
- [Software Requirements](#software-requirements)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [Applications](#applications)
- [Limitations](#limitations)
- [Future Improvements](#future-improvements)
- [Safety Notes](#safety-notes)
- [Contributors](#contributors)

---

## Project Overview

Traditional switches require manual operation. This project automates that process by enabling users to control appliances remotely through an Android application.  
At the core of the system is the **NodeMCU ESP8266**, which receives commands through Wi-Fi and switches relays ON/OFF accordingly.

### Key Features
- Remote switching of appliances through Android app
- Low-cost implementation using readily available components
- Supports up to 8 appliances using an 8-channel relay module
- Easy to scale for additional appliances and rooms

---

## System Design

The system uses a Wi-Fi router as a local communication bridge between the Android app and the NodeMCU.

![Block Diagram](https://github.com/BalrajGupta/Home-Automation/blob/master/block%20diagram.png)

### Basic Architecture
1. User sends command from Android app.
2. Command is transmitted over Wi-Fi.
3. NodeMCU ESP8266 receives the command.
4. Relay channel is triggered.
5. Connected AC appliance turns ON/OFF.

---

## Hardware Used

The following hardware was used to test and implement the project:

- **Wi-Fi Router**  
  Connects all devices in the local network.

- **NodeMCU (ESP8266)**  
  A low-cost IoT microcontroller with built-in Wi-Fi for communication and control.

- **8-Channel Relay Module (5V)**  
  Allows the microcontroller to control up to 8 AC electrical appliances.

- **Android Smartphone (Android 7.1 and above)**  
  Used to run the control app and send commands.

---

## How It Works

The NodeMCU is programmed to listen for incoming control commands from the app.  
Each command maps to one relay channel. When a relay is activated, it completes the circuit for the corresponding appliance; when deactivated, it disconnects power.

This enables real-time switching without physically touching wall switches.

---

## Software Requirements

- Arduino IDE (for programming NodeMCU)
- ESP8266 board package installed in Arduino IDE
- Android control app (custom or compatible IoT app)
- USB cable for flashing firmware to NodeMCU

---

## Setup and Installation

1. **Assemble the hardware**
   - Connect NodeMCU GPIO pins to relay input pins.
   - Connect relay outputs to appliance lines (with proper electrical safety).
   - Power the NodeMCU and relay module.

2. **Configure NodeMCU**
   - Install Arduino IDE and ESP8266 board support.
   - Upload firmware with your Wi-Fi SSID and password.
   - Map GPIO pins to desired relay channels.

3. **Connect Android app**
   - Ensure phone and NodeMCU are on the same Wi-Fi network.
   - Configure app with NodeMCU IP/endpoint.
   - Test each switch from the app.

4. **Validate operation**
   - Toggle appliances one by one.
   - Confirm correct relay response and stable network communication.

---

## Usage

- Open the Android app.
- Select the appliance switch (e.g., Light, Fan, Socket).
- Tap to turn ON/OFF.
- The NodeMCU updates the corresponding relay instantly.

---

## Applications

- Smart home lighting control
- Office appliance automation
- Energy-saving by remote switch-off
- Basic IoT learning and prototyping

---

## Limitations

- Requires stable local Wi-Fi network
- No built-in cloud or internet remote access (in basic version)
- Relay-based control only (no power monitoring)
- Manual safety and enclosure precautions required

---

## Future Improvements

- Add voice assistant integration (Google Assistant/Alexa)
- Add scheduling and timer-based automation
- Add current/power monitoring sensors
- Add secure authentication and encrypted communication
- Cloud access for control from outside local network

---

## Safety Notes

> **Warning:** This project interfaces with AC mains electricity.  
> Always follow proper electrical safety standards.

- Use insulated enclosures
- Avoid exposed live wires
- Use proper rated relays and wiring
- Test with supervision
- Consult a qualified electrician for final installations

---

## Contributors

Developed as part of B.Tech coursework by the project team.

---
