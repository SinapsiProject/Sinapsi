package com.sinapsi.client;

import com.sinapsi.client.persistence.InconsistentMacroChangeException;
import com.sinapsi.client.persistence.syncmodel.MacroSyncConflict;
import com.sinapsi.client.web.OnlineStatusProvider;
import com.sinapsi.model.MacroInterface;

import java.util.List;

/**
 * Created by Giuseppe on 05/07/15.
 */
public class SafeSyncManager {
    private SyncManager syncManager;
    private OnlineStatusProvider onlineStatusProvider;
    private SyncLock lock = new SyncLock();



    public SafeSyncManager(SyncManager syncManager, OnlineStatusProvider onlineStatusProvider){
        this.syncManager = syncManager;
        this.onlineStatusProvider = onlineStatusProvider;
    }

    public void addMacro(final MacroInterface macro, final SyncManager.MacroSyncCallback callback){
        new SafeSyncThread(lock, new PreSyncAction() {
            @Override
            public void run() throws InconsistentMacroChangeException {
                syncManager.addMacro(macro);
            }
        }, callback).start();
    }

    public void updateMacro(final MacroInterface macro, SyncManager.MacroSyncCallback callback){
        new SafeSyncThread(lock, new PreSyncAction() {
            @Override
            public void run() throws InconsistentMacroChangeException {
                syncManager.updateMacro(macro);
            }
        }, callback).start();
    }

    public void removeMacro(final int id, SyncManager.MacroSyncCallback callback){
        new SafeSyncThread(lock, new PreSyncAction() {
            @Override
            public void run() throws InconsistentMacroChangeException {
                syncManager.removeMacro(id);
            }
        }, callback).start();
    }

    public void getMacros(SyncManager.MacroSyncCallback callback){
        new SafeSyncThread(lock, new PreSyncAction() {
            @Override
            public void run() throws InconsistentMacroChangeException {
                //just do nothing
            }
        }, callback).start();
    }

    public int getMinId(){
        return syncManager.getMinId();
    }


    private class SyncLock{
        private boolean isLocked = false;

        private synchronized void lock() throws InterruptedException{
            while(isLocked){
                wait();
            }
            isLocked = true;
        }

        private synchronized void unlock(){
            isLocked = false;
            notify();
        }
    }

    private class SafeSyncThread extends Thread{
        public SafeSyncThread(final SyncLock lock, final PreSyncAction whatToDoBeforeSync, final SyncManager.MacroSyncCallback mscallback){
            super(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.lock();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }

                    try{
                        whatToDoBeforeSync.run();

                        //a local MacroSyncCallback that calls the mscallback's methods after unlocking
                        SyncManager.MacroSyncCallback msc = new SyncManager.MacroSyncCallback() {
                            @Override
                            public void onSyncSuccess(List<MacroInterface> currentMacros) {
                                lock.unlock();
                                mscallback.onSyncSuccess(currentMacros);
                            }

                            @Override
                            public void onSyncConflicts(List<MacroSyncConflict> conflicts, SyncManager.ConflictResolutionCallback conflictCallback) {
                                mscallback.onSyncConflicts(conflicts, conflictCallback);
                            }

                            @Override
                            public void onSyncFailure(Throwable error) {
                                lock.unlock();
                                mscallback.onSyncFailure(error);
                            }
                        };

                        if(onlineStatusProvider.isOnline()){
                            syncManager.sync(msc);
                        }else{
                            msc.onSyncSuccess(syncManager.getAllMacros());
                        }

                    }catch (Throwable e){
                        lock.unlock();
                        e.printStackTrace();
                        mscallback.onSyncFailure(e);
                    }
                }
            });
        }
    }

    private interface PreSyncAction{
        public void run() throws InconsistentMacroChangeException;
    }


}
