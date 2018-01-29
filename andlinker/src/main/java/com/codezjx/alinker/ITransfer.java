package com.codezjx.alinker;

import android.os.IBinder;

public interface ITransfer extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements ITransfer {
        private static final String DESCRIPTOR = "com.codezjx.alinker.ITransfer";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.codezjx.alinker.ITransfer interface,
         * generating a proxy if needed.
         */
        public static ITransfer asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof ITransfer))) {
                return ((ITransfer) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_execute: {
                    data.enforceInterface(DESCRIPTOR);
                    Request _arg0;
                    if ((0 != data.readInt())) {
                        _arg0 = Request.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    Response _result = this.execute(_arg0);
                    if ((flags & IBinder.FLAG_ONEWAY) != 0) {
                        // One-way mode just execute and return directly.
                        return true;
                    }
                    reply.writeNoException();
                    if ((_result != null)) {
                        reply.writeInt(1);
                        _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    if ((_arg0 != null)) {
                        reply.writeInt(1);
                        _arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
                case TRANSACTION_register: {
                    data.enforceInterface(DESCRIPTOR);
                    ICallback _arg0;
                    _arg0 = ICallback.Stub.asInterface(data.readStrongBinder());
                    this.register(_arg0);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_unRegister: {
                    data.enforceInterface(DESCRIPTOR);
                    ICallback _arg0;
                    _arg0 = ICallback.Stub.asInterface(data.readStrongBinder());
                    this.unRegister(_arg0);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements ITransfer {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public Response execute(Request request) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                Response _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((request != null)) {
                        _data.writeInt(1);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    // One-way mode just transact and return directly.
                    if (request != null && request.isOneWay()) {
                        mRemote.transact(Stub.TRANSACTION_execute, _data, null, IBinder.FLAG_ONEWAY);
                        return null;
                    }
                    mRemote.transact(Stub.TRANSACTION_execute, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        _result = Response.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    if ((0 != _reply.readInt())) {
                        request.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void register(ICallback callback) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((callback != null)) ? (callback.asBinder()) : (null)));
                    mRemote.transact(Stub.TRANSACTION_register, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void unRegister(ICallback callback) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((callback != null)) ? (callback.asBinder()) : (null)));
                    mRemote.transact(Stub.TRANSACTION_unRegister, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_execute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_register = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_unRegister = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    }

    public Response execute(Request request) throws android.os.RemoteException;

    public void register(ICallback callback) throws android.os.RemoteException;

    public void unRegister(ICallback callback) throws android.os.RemoteException;
}
