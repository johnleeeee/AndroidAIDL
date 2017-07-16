// IBinderPool.aidl
package lzq.com.androidaidl.binderpool;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder queryBinder(int binderCode);
}
