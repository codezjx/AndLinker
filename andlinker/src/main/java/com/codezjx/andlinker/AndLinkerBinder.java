package com.codezjx.andlinker;

import android.os.IBinder;

/**
 * AndLinker {@link IBinder} object to return in {@link android.app.Service#onBind(android.content.Intent)} method.
 */
public interface AndLinkerBinder extends IBinder {

    /**
     * Register service interface implementation.
     */
    void registerObject(Object target);

    /**
     * Unregister service interface implementation.
     */
    void unRegisterObject(Object target);

    /**
     * {@link AndLinkerBinder} factory class.
     */
    final class Factory {

        private Factory() {
            
        }
        
        /**
         * Factory method to create the {@link AndLinkerBinder} impl instance.
         */
        public static AndLinkerBinder newBinder() {
            // Return inner package access LinkerBinder, prevent exposed.
            return new LinkerBinderImpl();
        }
    }
    
}
