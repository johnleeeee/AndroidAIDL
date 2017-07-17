package lzq.com.androidaidl.binderpool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Author：lizhiqiang
 * Time：2017/7/16 23:17
 * Description：This is BinderPoolService
 */
public class BinderPoolService extends Service{

    private static final String TAG = "BinderPoolService";
    //服务端通过binder池的实例得到对应的binder
    private Binder mBinderPool = new BinderPool.BinderPooImpl();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinderPool;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
