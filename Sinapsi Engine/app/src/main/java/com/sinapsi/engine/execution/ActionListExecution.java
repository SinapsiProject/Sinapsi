package com.sinapsi.engine.execution;

import com.sinapsi.engine.Action;

import java.util.List;

/**
 * Class representing the execution of an action list.
 */
public class ActionListExecution {



    private int counter = 0;
    private List<Action> actions;
    private ActionListExecutionEventAdapter events = new ActionListExecutionEventAdapter() {};

    /**
     * One parameter ctor.
     * @param actions the action list
     */
    public ActionListExecution(List<Action> actions){
        this.actions = actions;
    }

    /**
     * Ctor.
     * @param actions the action list
     * @param aleea an instance of an implementation of
     *              ActionListExecutionEventAdapter, used
     *              to handle this ActionListExecution's
     *              events.
     */
    public ActionListExecution(List<Action> actions, ActionListExecutionEventAdapter aleea){
        this.actions = actions;
        events = aleea;
    }


    private void checkCounter(int index){
        if(index < 0 || index >= actions.size())
            throw new IndexOutOfBoundsException(
                    "Tried to move counter to " + index
                    + " but valid values are from 0 to " + (actions.size()));
    }

    /**
     * Sets the program counter to 0.
     */
    public void resetCounter() {
        counter = 0;
    }

    /**
     * Increases the program counter by 1.
     */
    public void increaseCounter(){
        counter++;
    }

    /**
     * Program counter getter.
     * @return the program counter.
     */
    public int getCounter(){
        return counter;
    }

    /**
     * Program counter checked setter.
     * @param index the index
     * @throws java.lang.IndexOutOfBoundsException if
     *         index less than zero or if is greater
     *         or equal than the size of the action list.
     */
    public void moveCounterTo(int index){
        checkCounter(index);
        counter = index;
    }

    /**
     * Checks if the execution of this action list is ended.
     * @return true if the program counter is greater
     *         or equal than the size of the action list,
     *         false otherwise.
     */
    public boolean isEnded(){
        return counter >= actions.size();
    }

    /**
     * Executes the action at the given program counter, and
     * calls the event handler methods of the ActionListExecutionEventAdapter,
     * then increases the program counter by 1.
     * @param ei
     */
    public void executeNext(ExecutionInterface ei){
        if(isEnded()){
            events.onActionListExecutionEnded(ei);
        }
        else {
            if(counter == 0){
                events.onActionListExecutionStarted(ei);
            }
            if(events.onActionActivating(actions.get(counter),ei)) {
                actions.get(counter).activate(ei);
                events.onActionActivated(actions.get(counter),ei);
            }
            increaseCounter();
        }
    }

    /**
     * Gets the action that will be executed if executeNext() is called.
     * @return the action
     */
    public Action getNextAction(){
        return actions.get(counter);
    }

    /**
     * Abstract class of listener methods to handle events regarding
     * action list execution.
     */
    public abstract class ActionListExecutionEventAdapter{
        /**
         * Called just when the program counter has reached the index
         * number after the last action in the list.
         * Override this to handle this event.
         * @param ei the execution interface.
         */
        public void onActionListExecutionEnded(ExecutionInterface ei){
            //override this if needed
        }

        /**
         * Called just before the execution of the first action in the list.
         * Note: this is called before onActionActivating().
         * Override this to handle this event.
         * @param ei the execution interface.
         */
        public void onActionListExecutionStarted(ExecutionInterface ei){
            //override this if needed
        }

        /**
         * Called just before the execution of an action.
         * Override this to handle this event.
         * @param ei the execution interface.
         * @param a the action
         * @return
         */
        public boolean onActionActivating(Action a, ExecutionInterface ei){
            //override this if needed
            return true;
        }

        /**
         * Called just after the execution of an action
         * Override this to handle this event.
         * @param ei the execution interface.
         * @param a the action
         */
        public void onActionActivated(Action a, ExecutionInterface ei){
            //override this if needed
        }
    }
}
