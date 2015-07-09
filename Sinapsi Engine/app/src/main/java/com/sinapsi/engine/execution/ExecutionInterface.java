package com.sinapsi.engine.execution;

import com.sinapsi.engine.VariableManager;
import com.sinapsi.engine.log.SinapsiLog;
import com.sinapsi.engine.system.SystemFacade;
import com.sinapsi.engine.Action;
import com.sinapsi.model.DeviceInterface;
import com.sinapsi.model.MacroInterface;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

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
    private MacroInterface macro = null;
    private VariableManager globalVars;
    private VariableManager localVars;
    private SinapsiLog log;
    private Deque<ActionListExecution> stack = new ArrayDeque<>();
    private WebExecutionInterface webExecutionInterface;

    /**
     * Creates a new ExecutionInterface.
     * @param system the system facade of this device (use null for remote devices)
     * @param device this device, the one on which the macro is executed.
     */
    public ExecutionInterface(SystemFacade system,
                              DeviceInterface device,
                              WebExecutionInterface webExecution,
                              VariableManager globalVars,
                              SinapsiLog log){
        this.system = system;
        this.device = device;
        this.webExecutionInterface = webExecution;
        this.localVars = new VariableManager();

        this.globalVars = globalVars;
        this.log = log;
    }

    /**
     * Helper cloning method to get a new instance of execution interface
     * with same SystemFacade, DeviceInterface, global vars and SinapsiLog,
     * but new instances of local vars and execution stack.
     * @return a new instance
     */
    public ExecutionInterface cloneInstance(){
        return new ExecutionInterface(this.getSystemFacade(),
                this.getDevice(),
                webExecutionInterface,
                this.getGlobalVars(),
                this.getLog());
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
     * Getter of the macro that is executed
     * @return the macro
     */
    public MacroInterface getMacro() {
        return macro;
    }

    /**
     * Setter of the macro that is executed
     * @param mi the macro
     */
    public void setMacro(MacroInterface mi){
        this.macro = mi;
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
     * Engine's log system getter
     * @return the log system
     */
    public SinapsiLog getLog() {
        return log;
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
        if(macro != null && !stack.isEmpty()) log.log("EXECUTION", "Pushed a new scope during execution of macro "
                + macro.getId() + ":'" + macro.getName() + "'.");
        stack.push(ale);
        return stack.size();
    }

    /** Pops out the scope from the top of the execution stack.
     * @return the count of scopes on the stack
     */
    public int popScope(){
        if(macro != null && !stack.isEmpty()) log.log("EXECUTION", "Popped a scope from the stack during execution of macro "
                + macro.getId() + ":'" + macro.getName() + "'.");
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
            log.log("EXECUTION","execute() iteration");
            ActionListExecution ale = stack.peek();
            Action a = ale.getNextAction();
            DeviceInterface ad = a.getExecutionDevice();
            int adid = ad.getId();
            int did = device.getId();
            if(adid == did)
                ale.executeNext(this);
            else{
                webExecutionInterface.continueExecutionOnDevice(this, a.getExecutionDevice());
                cancel();
            }

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
        if(macro != null && !stack.isEmpty()) log.log("EXECUTION", "Execution of macro " + macro.getId() + ":'" + macro.getName() +
                "' unpaused after action at scope " + getScopeLevel() + ", index " + getCurrentScopeExecution().getCounter());
        isPaused = false;
        execute();
    }

    /**
     * Sets the execution state to paused, so the next action will not be
     * activated until unpause() is called.
     */
    public void pause(){
        if(macro != null && !stack.isEmpty()) log.log("EXECUTION", "Execution of macro " + macro.getId() + ":'" + macro.getName() +
                "' paused after action at scope " + getScopeLevel() + ", index " + getCurrentScopeExecution().getCounter());
        isPaused = true;
    }

    /**
     * Sets the execution state to cancelled, so the next action will not
     * be activated.
     */
    public void cancel(){
        if(macro != null && !stack.isEmpty()) log.log("EXECUTION", "Execution of macro " + macro.getId() + ":'" + macro.getName() +
                "' cancelled after action at scope " + getScopeLevel() + ", index " + getCurrentScopeExecution().getCounter());
        isCancelled = true;
    }

    /**
     * Sets the execution state to ongoing, but if it was cancelled before
     * execute() must be called in order to actually restart execution.
     */
    public void uncancel(){
        if(macro != null && !stack.isEmpty()) log.log("EXECUTION", "Execution of macro " + macro.getId() + ":'" + macro.getName() +
                "' uncancelled after action at scope " + getScopeLevel() + ", index " + getCurrentScopeExecution().getCounter());
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

    /**
     * Call this to continue the execution of a remote macro on this device,
     * on this Execution interface.
     * @param localVars the updated localVariables
     * @param indexes the updated execution stack indexes
     */
    public void continueExecutionFromRemote(MacroInterface macro, VariableManager localVars, Deque<Integer> indexes){
        this.localVars = localVars;

        this.macro = macro;
        Iterator<Integer> indexIter = indexes.descendingIterator();
        while(indexIter.hasNext()){
            int index = indexIter.next();
            pushScope(new ActionListExecution(macro.getActions()));
            //TODO: get the action list from the par json
            getCurrentScopeExecution().moveCounterTo(index);
        }

    }


    /**
     * Getter of the whole execution stack
     * @return the execution stack
     */
    public Deque<ActionListExecution> getExecutionStack() {
        return stack;
    }

    /**
     * returns a stack of integers containing all the program counters of the execution stack
     * @return the stack
     */
    public Deque<Integer> getExecutionStackIndexes() {
        Deque<Integer> result = new ArrayDeque<>();
        Iterator<ActionListExecution> iale = stack.descendingIterator();
        while(iale.hasNext()){
            ActionListExecution ale = iale.next();
            result.push(ale.getCounter());
        }
        return result;
    }
}
