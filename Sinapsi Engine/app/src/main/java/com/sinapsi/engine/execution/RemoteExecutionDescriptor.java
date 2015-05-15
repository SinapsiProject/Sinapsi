package com.sinapsi.engine.execution;

import com.sinapsi.engine.VariableManager;
import com.sinapsi.model.impl.Macro;

import java.util.Deque;

/**
 * Class that wraps the the local variables and the execution stack
 * of an ExecutionInterface, in order to make it serializable for GSON
 * conversions.
 */
public class RemoteExecutionDescriptor {
    private int idMacro;
    private VariableManager localVariables;
    private Deque<Integer> indexes;

    public RemoteExecutionDescriptor(int idMacro, VariableManager localVariables, Deque<Integer> PCstack){
        this.localVariables = localVariables;
        this.indexes = PCstack;
        this.idMacro = idMacro;
    }


    public VariableManager getLocalVariables(){
        return localVariables;
    }

    public Deque<Integer> getStack(){
        return indexes;
    }

    public int getIdMacro(){
        return idMacro;
    }
}
