A Controller Area Network (CAN bus) is a robust vehicle bus standard designed to allow microcontrollers and devices to communicate with each other in applications without a host computer. It is a message-based protocol, designed originally for multiplex electrical wiring within automobiles to save on copper, but is also used in many other contexts.

I suggest watching this video: https://www.youtube.com/watch?v=FqLDpHsxvf8


The modern automobile may have as many as 70 electronic control units (ECU) for various subsystems. Typically the biggest processor is the engine control unit. Others are used for transmission, airbags, antilock braking/ABS, cruise control, electric power steering, audio systems, power windows, doors, mirror adjustment, battery and recharging systems for hybrid/electric cars, etc. Some of these form independent subsystems, but communications among others are essential. A subsystem may need to control actuators or receive feedback from sensors. The CAN standard was devised to fill this need. One key advantage is that interconnection between different vehicle systems can allow a wide range of safety, economy and convenience features to be implemented using software alone - functionality which would add cost and complexity if such features were "hard wired" using traditional automotive electrics. Examples include:

__1. Auto start/stop:__ Various sensor inputs from around the vehicle (speed sensors, steering angle, air conditioning on/off, engine temperature) are collated via the CAN bus to determine whether the engine can be shut down when stationary for improved fuel economy and emissions.

__2. Electric park brakes:__ The "hill hold" functionality takes input from the vehicle's tilt sensor (also used by the burglar alarm) and the road speed sensors (also used by the ABS, engine control and traction control) via the CAN bus to determine if the vehicle is stopped on an incline. Similarly, inputs from seat belt sensors (part of the airbag controls) are fed from the CAN bus to determine if the seat belts are fastened, so that the parking brake will automatically release upon moving off.

__3. Parking assist systems:__ when the driver engages reverse gear, the transmission control unit can send a signal via the CAN bus to activate both the parking sensor system and the door control module for the passenger side door mirror to tilt downward to show the position of the curb. The CAN bus also takes inputs from the rain sensor to trigger the rear windscreen wiper when reversing.

__4. Auto lane assist/collision avoidance systems:__ The inputs from the parking sensors are also used by the CAN bus to feed outside proximity data to driver assist systems such as Lane Departure warning, and more recently, these signals travel through the CAN bus to actuate brake by wire in active collision avoidance systems.

__5. Auto brake wiping:__ Input is taken from the rain sensor (used primarily for the automatic windscreen wipers) via the CAN bus to the ABS module to initiate an imperceptible application of the brakes whilst driving to clear moisture from the brake rotors. Some high performance Audi and BMW models incorporate this feature.

In recent years, the LIN bus standard has been introduced to complement CAN for non-critical subsystems such as air-conditioning and infotainment, where data transmission speed and reliability are less critical.

[Ref: Wikipedia]
