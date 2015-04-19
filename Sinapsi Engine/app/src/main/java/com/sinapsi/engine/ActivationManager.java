package com.sinapsi.engine;

import com.sinapsi.model.Trigger;

/**
 * Created by Giuseppe on 19/04/15.
 */
public interface ActivationManager {

    //TODO: manage distribution

    public void addToNotifyList(Trigger t);
    public void removeFromNotifyList(Trigger t);
}
