package com.sinapsi.client;

import com.sinapsi.client.persistence.InconsistentMacroChangeException;
import com.sinapsi.client.persistence.syncmodel.MacroSyncConflict;
import com.sinapsi.client.web.OnlineStatusProvider;
import com.sinapsi.model.MacroInterface;

import java.util.List;

/**
 * This class provides a thread-safe way to make sync requests and changes to the data.
 * This prevents race condition by avoiding concurrent calls to SyncManager's addMacro(),
 * deleteMacro(), updateMacro() and sync(). This is done by making every operation in a
 * new thread and using a lock to keep other sync threads waiting.
 */
public class SafeSyncManager {
    private SyncManager syncManager;
    private OnlineStatusProvider onlineStatusProvider;
    private SyncLock lock = new SyncLock();


    public SafeSyncManager(SyncManager syncManager, OnlineStatusProvider onlineStatusProvider) {
        this.syncManager = syncManager;
        this.onlineStatusProvider = onlineStatusProvider;
    }

    public void addMacro(final MacroInterface macro, final SyncManager.MacroSyncCallback callback) {
        if (onlineStatusProvider.isOnline()) {
            new SafeSyncThread(lock, new PreSyncAction() {
                @Override
                public void run() throws InconsistentMacroChangeException {
                    syncManager.addMacro(macro);
                }
            }, callback).start();
        } else {
            try{
                syncManager.addMacro(macro);
                callback.onSyncSuccess(syncManager.getAllMacros());
            }catch (Throwable e){
                callback.onSyncFailure(e);
            }
        }
    }

    public void updateMacro(final MacroInterface macro, SyncManager.MacroSyncCallback callback) {
        if (onlineStatusProvider.isOnline()) {
            new SafeSyncThread(lock, new PreSyncAction() {
                @Override
                public void run() throws InconsistentMacroChangeException {
                    syncManager.updateMacro(macro);
                }
            }, callback).start();
        } else {
            try{
                syncManager.updateMacro(macro);
                callback.onSyncSuccess(syncManager.getAllMacros());
            }catch (Throwable e){
                callback.onSyncFailure(e);
            }
        }
    }

    public void removeMacro(final int id, SyncManager.MacroSyncCallback callback) {
        if(onlineStatusProvider.isOnline()){
            new SafeSyncThread(lock, new PreSyncAction() {
                @Override
                public void run() throws InconsistentMacroChangeException {
                    syncManager.removeMacro(id);
                }
            }, callback).start();
        } else {
            try{
                syncManager.removeMacro(id);
                callback.onSyncSuccess(syncManager.getAllMacros());
            }catch (Throwable e){
                callback.onSyncFailure(e);
            }
        }
    }

    public void getMacros(SyncManager.MacroSyncCallback callback) {
        if(onlineStatusProvider.isOnline()){
            new SafeSyncThread(lock, new PreSyncAction() {
                @Override
                public void run() throws InconsistentMacroChangeException {
                    //just do nothing
                }
            }, callback).start();
        } else {
            try{
                callback.onSyncSuccess(syncManager.getAllMacros());
            }catch (Throwable e){
                callback.onSyncFailure(e);
            }
        }
    }

    public int getMinId() {
        return syncManager.getMinId();
    }


    private class SyncLock {
        private boolean isLocked = false;

        private synchronized void lock() throws InterruptedException {
            while (isLocked) {
                wait();
            }
            isLocked = true;
        }

        private synchronized void unlock() {
            isLocked = false;
            notify();
        }
    }

    private class SafeSyncThread extends Thread {


        public SafeSyncThread(final SyncLock lock, final PreSyncAction whatToDoBeforeSync, final SyncManager.MacroSyncCallback mscallback) {
            super(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.lock();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }

                    try {
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

                        syncManager.sync(msc);

                    } catch (Throwable e) {
                        lock.unlock();
                        e.printStackTrace();
                        mscallback.onSyncFailure(e);
                    }
                }
            });
        }
    }

    private interface PreSyncAction {
        public void run() throws InconsistentMacroChangeException;
    }


}
