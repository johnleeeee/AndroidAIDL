package lzq.com.androidaidl.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author：lizhiqiang
 * Time：2017/7/15 19:32
 * Description：This is BookManagerService
 */
public class BookManagerService extends Service{

    private static final String TAG = "BMS";
    private AtomicBoolean mIsServiceDestory = new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<IOnNewBookArrivedListener>();

    //创建Binder实例
    private Binder mBinder = new IBookManager.Stub(){

        @Override
        public List<Book> getBookList() throws RemoteException {
            SystemClock.sleep(5000);
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            //权限验证
            int check = checkCallingOrSelfPermission("lzq.com.androidaidl.permission.ACCESS_BOOK_SERVICE");
            Log.e(TAG,"check = "+check);

            if(check == PackageManager.PERMISSION_DENIED){
                return false;
            }
            //包名验证
            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if(packages != null && packages.length > 0){
                packageName = packages[0];
            }
            Log.d(TAG,"onTransact : " +packageName);
            if(!packageName.startsWith("lzq.com")){
                return false;
            }
            return super.onTransact(code, data, reply, flags);

        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            Log.d(TAG,"*********************registerListener***************");

            mListenerList.register(listener);
            final int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG,"registerListener,current size:"+N);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            Log.d(TAG,"*********************unregisterListener***************");

            boolean success = mListenerList.unregister(listener);

            if(success){
                Log.d(TAG,"unregister success");
            }else{
                Log.d(TAG,"not found , unregister faild");
            }
            final int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG,"unregister current size:"+ N);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"android"));
        mBookList.add(new Book(2,"ios"));
        new Thread(new ServiceWorker()).start();
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i<N;i++){
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            if(listener != null){
                listener.onNewBookArrived(book);
            }
        }
        mListenerList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable{

        @Override
        public void run() {
            while (!mIsServiceDestory.get()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size()+1;
                Book book = new Book(bookId,"new book#"+bookId);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("lzq.com.androidaidl.permission.ACCESS_BOOK_SERVICE");
        Log.d(TAG,"onBinder check : "+check);
        if(check == PackageManager.PERMISSION_DENIED){
            return null;
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestory.set(true);
        super.onDestroy();

    }
}
