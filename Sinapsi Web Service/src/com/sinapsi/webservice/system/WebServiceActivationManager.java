package com.sinapsi.webservice.system;

import com.sinapsi.engine.ActivationManager;
import com.sinapsi.engine.Trigger;
import com.sinapsi.engine.execution.ExecutionInterface;

public class WebServiceActivationManager extends ActivationManager {
    
    public WebServiceActivationManager(
            ExecutionInterface defaultExecutionInterface) {
        super(defaultExecutionInterface);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void addToNotifyList(Trigger t){
        
    }
    
    @Override
    public void removeFromNotifyList(Trigger t){
        
    }
    
    
}
