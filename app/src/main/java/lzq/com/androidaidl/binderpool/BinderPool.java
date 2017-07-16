package lzq.com.androidaidl.binderpool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.concurrent.CountDownLatch;

/**
 * Author：lizhiqiang
 * Time：2017/7/16 22:49
 * Description：This is BinderPool
 */
public class BinderPool {

    private static final String TAG = "BinderPool";
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SERCURITY_CENTER = 1;

    private Context context;
    private static volatile BinderPool sInstance;
    private IBinderPool mBinderPool;
    private CountDownLatch mConnectionBinderPoolCountDownLatch;


    private BinderPool(Context context){
        this.context = context;
        connectionBinderPoolService();
    }

    public static BinderPool getInstance(Context context){
        if(sInstance == null){
            synchronized (BinderPool.class){
                if (sInstance == null){
                    sInstance = new BinderPool(context);
                }
            }

        }
        return sInstance;
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient,0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectionBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient,0);
            mBinderPool = null;
            connectionBinderPoolService();
        }
    };

    public synchronized void connectionBinderPoolService(){
        mConnectionBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(context, BinderPoolService.class);
        context.bindService(service , mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectionBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static class BinderPooImpl extends IBinderPool.Stub{

        public BinderPooImpl(){
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {

            IBinder binder = null;
            switch (binderCode){
                case BINDER_COMPUTE:

                    binder = new ComputeImpl();

                    break;
                case BINDER_SERCURITY_CENTER:

                    binder = new SecurityCenterImpl();

                    break;
                default:
                    break;
            }

            return binder;
        }
    }

    public IBinder queryBinder(int binderCode){
        IBinder binder = null;
        if(mBinderPool != null){
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }

}
