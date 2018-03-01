package com.codezjx.andlinker;

import android.os.IBinder;

/**
 * Created by codezjx on 2018/2/24.<br/>
 */
public interface AndLinkerBinder extends IBinder {

    void registerObject(Object target);

    void unRegisterObject(Object target);

    final class Factory {

        private Factory() {
            
        }
        
        public static AndLinkerBinder newBinder() {
            // Return inner package access LinkerBinder, prevent exposed.
            return new LinkerBinderImpl();
        }
    }
    
}
