# sideProject01

This project consists of a raspberry pie and a android phone application. The simple phone application authenticates with a server running express node js, then allows the phone user to turn on and record a camera installed on a raspberry pie through bluetooth. The application also allows the user to stop and start at various times and upload their files to a server.

The raspberry consists of a bluetooth server written in python waiting for a connection from a phone. Once a connection is established, the python application will respond to several commands ex. starting the camera/stopping/record video to file/upload file to server. 
