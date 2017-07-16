// IBookManager.aidl
package lzq.com.androidaidl.aidl;

import lzq.com.androidaidl.aidl.Book;
import lzq.com.androidaidl.aidl.IOnNewBookArrivedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
    }
