package com.sinapsi.engine.execution;

import com.sinapsi.engine.VariableManager;

import java.util.Deque;

/**
 * Class that wraps the the local variables and the execution stack
 * of an ExecutionInterface, in order to make it serializable for GSON
 * conversions.
 */
public class RemoteExecutionDescriptor {
    private VariableManager localVariables;
    private Deque<ActionListExecution> stack;

    public RemoteExecutionDescriptor(VariableManager localVariables, Deque<ActionListExecution> stack){
        this.localVariables = localVariables;
        this.stack = stack;
    }

    public VariableManager getLocalVariables(){
        return localVariables;
    }

    public Deque<ActionListExecution> getStack(){
        return stack;
    }
}
