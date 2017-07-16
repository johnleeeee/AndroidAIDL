package lzq.com.androidaidl.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import lzq.com.androidaidl.R;

/**
 * Author：lizhiqiang
 * Time：2017/7/15 19:38
 * Description：This is BookManagerActivity
 */
public class BookManagerActivity extends Activity{

    private static final String TAG = "BookManagerActivity";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private IBookManager mRemoteBookManager ;

    private Handler mhandle = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.d(TAG,"receive new book:"+msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    //设置死亡代理
    private IBinder.DeathRecipient mDeathRecopient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG,"binder died . tname:" + Thread.currentThread().getName());
            if (mRemoteBookManager == null){
                return;
            }
            mRemoteBookManager.asBinder().unlinkToDeath(mDeathRecopient,0);
            mRemoteBookManager = null;
            //TODO:这里重新绑定远程Service
        }
    };

    //连接服务端
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            mRemoteBookManager = bookManager;
            try {
                mRemoteBookManager.asBinder().linkToDeath(mDeathRecopient,0);
                List<Book> list = bookManager.getBookList();//最好是在子线程调用。如果有耗时操作
                Log.i(TAG,"query book list, list type:"+list.getClass().getCanonicalName());
                Log.i(TAG,"query book list :"+list.toString());
                Book newBook = new Book(3,"Android开发艺术探索");
                bookManager.addBook(newBook);
                Log.i(TAG,"add book: "+newBook);
                List<Book> newlist = bookManager.getBookList();//最好是在子线程调用
                Log.i(TAG,newlist.toString());
                bookManager.registerListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteBookManager = null;
            Log.e(TAG,"onServiceDisconnected. tname:"+Thread.currentThread().getName());
        }
    };
    //监听接口
    private IOnNewBookArrivedListener mIOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mhandle.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED,newBook).sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定Service
        Intent intent = new Intent(this,BookManagerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    public void onButtonClick(View view){
        Toast.makeText(BookManagerActivity.this,"click button1",Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mRemoteBookManager != null){
                    try {
                        List<Book> newList = mRemoteBookManager.getBookList();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if(mRemoteBookManager != null
                && mRemoteBookManager.asBinder().isBinderAlive()){
            try {
                Log.d(TAG,"unregister listener:" + mIOnNewBookArrivedListener);
                mRemoteBookManager.unregisterListener(mIOnNewBookArrivedListener);
            } catch (RemoteException e) {
                Log.d(TAG,"RemoteException:");
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
