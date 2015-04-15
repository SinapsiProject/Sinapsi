#Sinapsi  
_Distributed intelligent automation system_  
  
Sinapsi gives you the ability to interconnect your devices in a very simple way, using a macro editor you can define a 
set of actions to be performed when a trigger is activated. For example, with a well formed macro, you can display a 
notification on your PC when the smartphone battery is running low. Unlike other applications of this kind, 
Sinapsi gives the ability to perform actions on multiple devices not necessarily in the same local network, 
thus generating a network of distributed devices which act and react with each other.
Sinapsi will be able to run on Android, Windows, Linux and Mac, future updates will bring Sinapsi on 
iOS devices and will allow a voice interface to interact with other devices.

##Database  
###ER  
![alt tag](http://i60.tinypic.com/73ed0i.png)    
   
##Model
###Web service
Contains the _database manager_, the _macro engine_ and provides an interface for the mobile/desktop clients   
   
###Client
Gives the opportunity to _register_ a new user/_log_, _connect_ to web services and _managing_ the macro, using a visual editor   
   
####Macro editor   
The user can create two type of macro, local macro and global macro. In a local macro, the actions are performed in the same
device that will waits for the activation of the trigger, in the global macro, the actions are performed in other devices.
    
####Connecting devices
The client application will provide an authentication system by e-mail or a Google account, the same e-mail  
can be used on other devices to make them visible to the devices that we have already connected to the web service.
   
####Action/Trigger registration
When a client connects to the web service for the first time, it send the list of triggers that can activate and the list of
actions that can perform.
  
######Authors: _Marco Grillo_, _Ayoub Ouarrak_, _Giuseppe Petrosino_
