![alt tag](http://s4.postimg.org/dkvk01ecr/logo_2.png)   
_Distributed intelligent automation system_  
  
Sinapsi gives you the ability to interconnect your devices in a very simple way; using a macro editor you can define a 
set of actions to be performed when a trigger is activated. For example, with a well formed macro, you can display a 
notification on your PC when the smartphone battery is running low. Unlike other applications of this kind, 
Sinapsi gives the ability to perform actions on multiple devices, not necessarily under the same local network, 
thus generating a network of distributed devices which act and react each other.
Sinapsi will be able to run on Android, Windows, Linux and Mac. Future updates will make Sinapsi available on 
iOS devices, introducing a voice interface to communicate with other devices.
   
##Model
###Web service
Contains the _database manager_, the _macro engine_ and provides an interface for the mobile/desktop clients. 
   
###Client
Gives the opportunity to _register_ a new user/_log_, _connect_ to web services and _managing_ the macro, using a visual editor.   
   
####Macro editor   
The user can create two type of macro, local macro and global macro. In a local macro, the actions are performed in the same
device waiting for the activation of the trigger. In the global macro, the actions are performed in other devices.
    
####Connecting devices
The client application will provide an authentication system by e-mail or Google authentication system. The same e-mail
can be used on other devices to make them visible to the connected devices.
   
####Action/Trigger registration
When a client connects to the web service for the first time, it sends the list of triggers that can activate and the list of
actions that can perform.
    
   
#####Authors: _Marco Grillo_, _Ayoub Ouarrak_, _Giuseppe Petrosino_
