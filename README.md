# lawnMowerBot2
Robotic lawn mower project

https://www.flickr.com/photos/133257962@N07/sets/72157652502239349

Current status - 150516

Lawn3_client: (Remote control app)
- imported to andoid studio (from eclipse)
- builds and runs ok
- issues: "gradle warning about file encoding"

Lawn3_server: (andoid phone used as "embedded computer" - communicating with Arduino)
- imported to andoid studio (from eclipse)
- NB! Use Wifi "hotspot", client will try to connect to 192.168.1.1

lawn3_arduino: (ArdunioMegaADK software controls motor controller communicates with Android phone over USB)
- should be ok

ToDo:
- add wire sensor and update SW for autonomous operation
- (a lot of improvements)
