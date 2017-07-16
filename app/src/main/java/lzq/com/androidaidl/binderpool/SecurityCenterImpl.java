package lzq.com.androidaidl.binderpool;

import android.os.RemoteException;

/**
 * Author：lizhiqiang
 * Time：2017/7/16 22:43
 * Description：This is SecurityCenterImpl
 */
public class SecurityCenterImpl extends ISecurityCenter.Stub{

    private static final char SECERT_CODE = '^';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for(int i = 0; i < chars.length ; i++){
            chars[i] ^= SECERT_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {

        return encrypt(password);
    }
}
