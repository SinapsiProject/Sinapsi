![alt tag](http://i61.tinypic.com/1zzszt0.png)   
#Sinapsi
***Distributed intelligent automation system***
  
Sinapsi gives you the ability to interconnect your devices in a very simple way; using a macro editor you can define a 
set of actions to be performed when a trigger is activated. For example, with a well formed macro, you can display a 
notification on your PC when the smartphone battery is running low. Unlike other applications of this kind, 
Sinapsi gives the ability to perform actions on multiple devices, not necessarily under the same local network, 
thus generating a network of distributed devices which act and react each other.
Sinapsi will be able to run on Android, Windows, Linux and Mac. Future updates will make Sinapsi available on 
iOS devices, introducing a voice interface to communicate with other devices.
   
##Model
###Web service
Contains the _database manager_, and the _web façade_ that provides an interface for the mobile/desktop clients. 
   
###Client
Clients have the opportunity to _register_ a new user/_log_, _connect_ to Sinapsi and _managing_ the macro, using a visual editor or a script editor.   
   
####Macro editor   
The user can create two type of macro, local macro and global macro. In a local macro, the actions are performed in the same device where was created the macro. In the global macro, the actions are performed in other devices.
    
####Connecting devices
The client application will provide an authentication system by e-mail or Google authentication system. The same e-mail can be used on other devices to make them visible to the connected devices, and allow the execution of global macro.
   
####Action/Trigger registration
When a client connects to the web service for the first time, it sends the list of triggers that can activate and the list of actions that can perform.
    
##Security
The connection between server and clients is crypted using [BGP library](https://github.com/AyoubOuarrak/Bit-Good-Privacy). 
###Communication
When a client wants to join Sinapsi, first, it generates the public and private key, and sends it to the server, e-mail , the newly generated public key and the encrypted session key. The server receives the request and generates, public key and private key, and sends to the client its public key and the encrypted session key. The client can now login, encrypting credentials with the server's public key and sending the generated string to the server. The server receive the encrypted string and decrypt it using its private key and the encrypted session key of the client. Now the server can response to the client crypting all data using the client's public key, and the client decrypt data recived from server, using its private key and encrypted session key of the server.
##Installation
###Android
When we finish the Android client , we will put the apk to the official website of Sinapsi and also in the Google
playstore  
   
###Linux/Windows & OSX
When we finish the Desktop client , we will put the executable files to the official website of Sinapsi
    
##Contribution
We want that Sinapsi be a universal tool to make amazing things with our devices, so we would be happy to receive yours feedback about the project. If you have in mind an idea to make Sinapsi more awesome, contact us:    marco.grillo@studenti.unipr.it , ayoub.ouarrak@studenti.unipr.it , giuseppe.petrosino@studenti.unipr.it
   
###I'm a script kiddy and I want to contribute 
The structure of code is complicated, and... Well, may God have mercy on you.
   
###I am a very pretty girl and I want to contribute
You're Welcome, we really need female staff in our team.
   
##Authors 
_Marco Grillo_ (https://github.com/MarcoGrillo)   
_Ayoub Ouarrak_ (https://github.com/AyoubOuarrak)   
_Giuseppe Petrosino_ (https://github.com/ParsleyJ)   
   
##License
   
The MIT License (MIT) 

Copyright (c) 2015 Marco Grillo, Ayoub Ouarrak, Giuseppe Petrosino   
   
Permission is hereby granted, free of charge, to any person obtaining a copy   
of this software and associated documentation files (the "Software"), to deal   
in the Software without restriction, including without limitation the rights   
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell   
copies of the Software, and to permit persons to whom the Software is   
furnished to do so, subject to the following conditions:   
   
The above copyright notice and this permission notice shall be included in   
all copies or substantial portions of the Software.   
   
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,   
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER   
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,   
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN   
THE SOFTWARE.   
