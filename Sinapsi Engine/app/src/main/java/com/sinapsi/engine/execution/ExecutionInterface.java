package com.sinapsi.engine.execution;

import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.model.DeviceInterface;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Collection of objects and utilities needed to control execution of
 * a macro or to access system infos or system calls during macro
 * execution. This should be instantiated every time a macro starts,
 * and then the same instance is passed within the components of the
 * macro.
 */
public class ExecutionInterface {

    private DeviceInterface device;
    private SystemFacade system;
    private VariableManager globalVars;
    private VariableManager localVars;
    private Deque<ActionListExecution> stack = new ArrayDeque<>();

    /**
     * Creates a new ExecutionInterface.
     * @param system the system facade of this device (use null for remote devices)
     * @param device this device, the one on which the macro is executed.
     */
    public ExecutionInterface(SystemFacade system, DeviceInterface device,
                              VariableManager globalVars){
        this.system = system;
        this.device = device;
        this.localVars = new VariableManager();
        this.globalVars = globalVars;
    }

    /**
     * Device getter
     * @return the device
     */
    public DeviceInterface getDevice(){
        return device;
    }

    /**
     * System facade getter
     * @return the system facade
     */
    public SystemFacade getSystemFacade(){
        return system;
    }

    /**
     * Local variable Manager getter
     * @return the variable manager
     */
    public VariableManager getLocalVars(){
        return localVars;
    }

    /**
     * Globals variable Manager getter
     * @return the variable manager
     */
    public VariableManager getGlobalVars(){
        return globalVars;
    }


    /**
     * Scope level getter
     * @return the scope level
     */
    public int getScopeLevel(){
        return stack.size();
    }

    /**
     * Pushes a new scope in the form of an action list in
     * the execution stack.
     * @param ale the action list
     * @return the count of scopes on the stack
     */
    public int pushScope(ActionListExecution ale){
        stack.push(ale);
        return stack.size();
    }

    /** Pops out the scope from the top of the execution stack.
     * @return the count of scopes on the stack
     */
    public int popScope(){
        if(!stack.isEmpty()) {
            stack.pop();
            return stack.size();
        }else{
            return -1;
        }
    }

    private boolean isPaused = false;
    private boolean isCancelled = false;

    /**
     * Tries to execute the macro. Note that the execution
     * continues from the program counter of the top of the stack.
     */
    public void execute(){
        while(!isPaused && !stack.isEmpty() && !isCancelled){
            ActionListExecution ale = stack.peek();
            ale.executeNext(this);
            if(ale.isEnded()) popScope();
        }
    }

    /**
     * Returns at the beginning of the macro, this means that
     * all the scopes except for the root one are removed from
     * the stack and that the pc is set to 0.
     */
    public void resetMacroPC(){
        if(stack.isEmpty()) return;
        while(stack.size()>1){
            stack.pop();
        }
        resetScopePC();
    }

    /**
     * Resets the program counter to 0 in this scope. Useful for
     * Loops.
     */
    public void resetScopePC(){
        if(stack.isEmpty()) return;
        stack.peek().resetCounter();
    }

    /**
     * Sets the execution state to unpaused and continues the execution
     * of the macro.
     */
    public void unpause(){
        isPaused = false;
        execute();
    }

    /**
     * Sets the execution state to paused, so the next action will not be
     * activated until unpause() is called.
     */
    public void pause(){
        isPaused = true;
    }

    /**
     * Sets the execution state to cancelled, so the next action will not
     * be activated.
     */
    public void cancel(){
        isCancelled = true;
    }

    /**
     * Sets the execution state to ongoing, but if it was cancelled before
     * execute() must be called in order to actually restart execution.
     */
    public void uncancel(){
        isCancelled = false;
    }

    /**
     * Current scope execution getter.
     * @return the current scope execution
     */
    public ActionListExecution getCurrentScopeExecution(){
        if(stack.isEmpty()) return null;
        return stack.peek();
    }

    //TODO: public void continueOnRemoteDevice(DeviceInterface x, RemoteExecutionManager y);
    //----: ^^^^ implement here distributed execution ^^^^



}
