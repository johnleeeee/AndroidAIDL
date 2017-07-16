// IOnNewBookArrivedListener.aidl
package lzq.com.androidaidl.aidl;

import lzq.com.androidaidl.aidl.Book;

// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book newBook);
}
