### Features

- Easy EEPROM (electrically erasable programmable read-only memory) programming, thanks to user-friendly GUI;
- Saving and opening *.csv files, containing binary data;
- Easily save different EEPROMs with easy access to their respective Arduino firmware and datasheets

# eeprom-programmer

The project is made mainly for educational purposes. It enables the user to visually experience the process of programming an EEPROM, which cannot be seen when using industrial programmers as the DIY platform allows slowing everything down and even adding LEDs showing current status of the operation. In spite of all the above, it still remains a fully functionable programmer, which I am planning to use in my further projects.


### Functionality alterations

In order to use different Arduino model, there may be some changes that will have to be applied. All of those refer to Arduino sketch only, as the Java app is written in a manner, that excludes any hardware incompatibilities.
Firstly, some models may have a smaller amount of digital I/O pins, which can be solved by using shift registers, such as 74HC595. Please note, that
```
int address [11] = {22,24,26,28,30,32,34,36,38,40,42};   //LSB to MSB
int data [8] = {23,25,27,29,31,33,35,37};   //LSB to MSB
```
refer to digital pins of Arduino MEGA, connected to address and data pins of AT28C16 EEPROM.

Secondly, the pinout will vary on different EEPROMs that you may use, hence the sketch will have to be modified (see respective datasheet).

### Further changes

I am planning to add some new features to the app in due course, which may include *.bin files handling for improved compatibility with industrial programmers. Note, that this is purely hobbistic development and is intended to be used in such projects only.

### Author

See my other projects on my [Github @bartek-krk](https://github.com/bartek-krk "Github")

### Credits

A great library [jSerialComm](https://github.com/Fazecast/jSerialComm "jSerialComm") was used in this project.
