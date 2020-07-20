import os
import glob
import time
import bluetooth
import RPi.GPIO as GPIO
from picamera import PiCamera
import threading
import picamera
import io
import pexpect
from Queue import Queue
from bluetooth import *

class CameraLifeCycle(threading.Thread):
    def __init__(self, queue,  args=(), kwargs=None):
        threading.Thread.__init__(self, args=(), kwargs=None)
        self.incrForSavedClips = 0
        
        self.nameofJourney = args[0]
        self.nameOfVideo = args[1]
        
        self.path = ""
        self.stream = ""
        self.incrementer = 0;
        self.camera = PiCamera()
        self.queue = queue
        self.daemon = True
        
    def close(self):
        print("closing CameraLifeCycle")
        self.camera.close();
        
    def run(self):
        print("running thread")

        self.startCamera()

        while True:
            val = self.queue.get()
            print(val)  
            #end the loop 
            if val == "end":
                self.endJourney()
                return
            if val == "startrecording":
                self.camera.wait_recording(30)
                self.write_video()
                
    def write_video(self):
        print("Writing video")
        with self.stream.lock:
            #find the first head frame in the video
            for frame in self.stream.frames:
                if frame.frame_type == picamera.PiVideoFrameType.sps_header:
                    self.stream.seek(frame.position)
                    break
            #write the rest of the stream to the disk
            pathofVideo =  self.path + "/" + str(self.incrementer) + self.nameOfVideo
            self.incrementer += 1
            print("pathofVideo is", pathofVideo)
            with io.open(pathofVideo, 'wb') as output:
                output.write(self.stream.read())
            
    def createFilePath(self):
        path = "/home/pi/Documents/cameraProject/savedVideos/" + self.nameofJourney
        print("Creating filepath " + path)

        print(path)
        if not os.path.exists(path):
            os.makedirs(path)
            
        self.path = path

    def startCamera(self):
        print("starting startCamera function")

        #create folder
        self.createFilePath()

        #start camera recording
        self.stream = picamera.PiCameraCircularIO(self.camera, seconds=20)
        self.camera.start_recording(self.stream, format='h264')
        
    def endJourney(self):
        print("camera stopping recording")
        self.camera.stop_recording()

    def recordJourney(self):
        print("starting recordJourney function")

def controller(thread, command):
    
    if(command == "end"):
        thread.queue.put("end")
        
    elif(command == "startrecording"):
        thread.queue.put("startrecording")

def uploadDocuments():
    print("Uploading Videos..")
    
    child = pexpect.spawn("scp -r -i  /home/pi/Documents/cameraProject/test01 /home/pi/Documents/cameraProject/savedVideos/ root@157.245.91.32:/home/samaxe/Documents/phoneApp/uploads")
    i = child.expect("Enter passphrase for key '/home/pi/Documents/cameraProject/test01':")
    if i==0:
        print("Sendline")
        child.sendline("natreasure23602")
        child.expect(pexpect.EOF, timeout=1000)
        print("Finished copying files to server")
    elif i==1:
        print "Error in connection to server"


def main():
    
    os.system('modprobe W1-gpio')
    os.system('modprobe W1-therm')

    server_sock=BluetoothSocket(RFCOMM)
    server_sock.bind(("",PORT_ANY))
    server_sock.listen(1)

    port = server_sock.getsockname()[1]
    uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
    clientAddress = "C4:93:D9:E8:8C:8F"

    advertise_service(server_sock, "RaspberryPie Server", service_id = uuid)
    service_classes = [uuid, SERIAL_PORT_CLASS ]
        # profiles = [SERIAL_PORT_PROFILE ]
        #)
    startedVideos = False
        
    

    while True:
        print "waiting for connection on RFCOMM channel %d" % port
        
        client_sock, client_info = server_sock.accept()
        print "Accepted connection from ", client_info
                              
        nameofFolder = "journey1Test"
        nameofVideo = "journey.h264"

        try:
            data = client_sock.recv(1024)
            if len(data) == 0:
                break
            print "received [%s]" % data

            parsedData = data.split(" ")
            if(parsedData[0] == "Start"):
                print "Start  Recived"
                startedVideos = True
                
                q = Queue()
                threadContr = CameraLifeCycle(q, args=(nameofFolder,nameofVideo,));
                threadContr.start()

            elif(parsedData[0] == "Pause" and startedVideos):
                controller(threadContr, "pause")

            elif(parsedData[0] == "Record" and startedVideos):
                print "Record Recieved"
                controller(threadContr, "startrecording")

            elif(parsedData[0] == "Stop" and startedVideos):
                print "Stop Recieved"
                controller(threadContr, "end")
                threadContr.join()
                startedVideos = False
                threadContr.close();
                
                
            elif(parsedData[0] == "upload" and startedVideos == False):
                uploadDocuments()
                    

        except IOError:
            pass

        except KeyboardInterrupt:
            print "disconnected "
            client_sock.close()
            server_sock.close()

            print "All done "
            break

main()