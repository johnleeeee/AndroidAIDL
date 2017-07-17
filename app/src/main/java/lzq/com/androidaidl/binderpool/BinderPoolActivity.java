package lzq.com.androidaidl.binderpool;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import lzq.com.androidaidl.R;

/**
 * Author：lizhiqiang
 * Time：2017/7/16 23:21
 * Description：This is BinderPoolActivity
 */
public class BinderPoolActivity extends Activity{

    private static final String TAG = "BinderPoolActivity";

    private ISecurityCenter mSecurityCenter;
    private ICompute mCompute ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doWrok();
            }
        }).start();
    }

    private void doWrok(){

        BinderPool binderPool = BinderPool.getInstance(BinderPoolActivity.this);

        IBinder securityBinder = binderPool.queryBinder(BinderPool.BINDER_SERCURITY_CENTER);

        mSecurityCenter = SecurityCenterImpl.asInterface(securityBinder);

        String msg = "hello world-安卓";
        Log.e(TAG,msg);
        try {
            String password = mSecurityCenter.encrypt(msg);
            Log.e(TAG,"encrypt"+password);
            Log.e(TAG,"encrypt"+mSecurityCenter.decrypt(msg));

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        mCompute = ComputeImpl.asInterface(computeBinder);

        try {
            Log.e(TAG,"3+5 = "+ mCompute.add(3,5));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
