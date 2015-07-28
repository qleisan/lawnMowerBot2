import pygame
import time

pygame.init()
j = pygame.joystick.Joystick(0)
j.init()
print 'Initialized Joystick : %s' % j.get_name()

"""
Returns a vector of the following form:
[LThumbstickX, LThumbstickY, Unknown Coupled Axis???, 
RThumbstickX, RThumbstickY, 
Button 1/X, Button 2/A, Button 3/B, Button 4/Y, 
Left Bumper, Right Bumper, Left Trigger, Right Triller,
Select, Start, Left Thumb Press, Right Thumb Press]

Note:
No D-Pad.
Triggers are switches, not variable. 
Your controller may be different
"""

MAX_SPEED = 90
MIN_SPEED = 15

def get():
    out = [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0]
    it = 0 #iterator
    pygame.event.pump()
    
    #Read input from the two joysticks       
    for i in range(0, j.get_numaxes()):
        out[it] = j.get_axis(i)
        it+=1
    #Read input from buttons
    for i in range(0, j.get_numbuttons()):
        out[it] = j.get_button(i)
        it+=1
    return out

def main():
    while True:
        time.sleep(0.2)
        gamePad = get()
        #print gamePad
        Lmotor =  int(round (gamePad[1] * MAX_SPEED * -1))  # positive is speed forward (not CW or CCW)
        Rmotor =  int(round (gamePad[3] * MAX_SPEED * -1))  # positive is speed forward (not CW or CCW)
        if abs(Lmotor) < MIN_SPEED:
            Lmotor = 0
        if abs(Rmotor) < MIN_SPEED:
            Rmotor = 0
        buttonRightTop = gamePad[9]
        buttonRightBottom = gamePad[11]
        print Lmotor, Rmotor, buttonRightTop, buttonRightBottom

if __name__ == "__main__":
    main()
