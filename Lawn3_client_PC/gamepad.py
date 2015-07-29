import pygame
import time
import socket

TCP_IP = '192.168.2.185'
TCP_PORT = 21111

MAX_SPEED = 90
MIN_SPEED = 15

MAX_UPDATE_FREQ = 10

pygame.init()
j = pygame.joystick.Joystick(0)
j.init()
print 'Initialized Joystick : %s' % j.get_name()


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


def readline(sock):
    buffer = ''
    data = True
    count = 0;
    while data!='\n':
        data = sock.recv(1)
        buffer += data
        count+=1;
        #print count, data
    return buffer

def main():

    print("Attempting to connect: %s:%d" % (TCP_IP, TCP_PORT))
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect((TCP_IP, TCP_PORT))
    start_time = time.time()
    while True:
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
        #print Lmotor, Rmotor, buttonRightTop, buttonRightBottom

        # --- Lmotor command ---
        str = "Lmotor %d\n" % Lmotor
        #print str
        s.send(str)
        line = readline(s)
        print line

        # --- Rmotor command ---
        str = "Rmotor %d\n" % Rmotor
        #print str
        s.send(str)
        line = readline(s)
        print line

        # --- handle cut motor commands ---
        if (buttonRightBottom==1):
            s.send('cutoff\n')
            line = readline(s)
            print line
        elif (buttonRightTop==1):
            s.send('cuton\n')
            line = readline(s)
            print line

        #limit update rate
        now = time.time()
        delay = 1.0/MAX_UPDATE_FREQ - (now - start_time)
        print delay
        if delay > 0:
            time.sleep(delay)
        else:
            print "too slow"

        start_time = time.time()
    #s.close()

if __name__ == "__main__":
    main()
