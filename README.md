#Sinapsi  
Distributed intelligent automation system  
##Model  
###Entity  
- **User** :email/password/google/IFTTT  
- **Device** :metadata (triggers & action allowed)  
- **Macro** :list of action activated by triggers  
- **Trigger**  
- **Action**  
###ER  
_User_   1 -—> n _Device_  
_User_   1 —-> n _Macro_  
_Device_ n —-> n _Action_  
_Device_ n —-> n _Trigger_  
_Macro_  n —-> 1 _Trigger_  
_Macro_  n —-> n _Action_  
   
###DB  
- **User**(_id_, email, password, google-auth)
- **Device**(_id_, name, model, type(desktop/mobile), _fk(user)_, version)
- **Macro**(_id_, name, _fk(user)_, _fk(trigger)_, _fk(trigger-device)_, trigger-json) 
- **Trigger**(_id_, min-version)
- **Action**(_id_, min-version)
- **Availability-Trigger**(_fk(device)_, _fk(trigger)_)
- **Availability-Action**(_fk(device)_, _fk(action)_) 
- **Action-Macro-list**(_fk(macro)_, _fk(action)_, counter, _fk(action-device)_, action-json)
 
