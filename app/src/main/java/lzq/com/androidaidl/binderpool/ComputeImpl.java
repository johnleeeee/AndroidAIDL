package lzq.com.androidaidl.binderpool;

import android.os.RemoteException;

/**
 * Author：lizhiqiang
 * Time：2017/7/16 22:41
 * Description：This is ComputeImpl
 */
public class ComputeImpl extends ICompute.Stub{
    @Override
    public int add(int a, int b) throws RemoteException {

        return a + b;
    }
}
