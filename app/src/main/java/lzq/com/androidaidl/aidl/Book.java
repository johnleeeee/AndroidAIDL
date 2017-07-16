package lzq.com.androidaidl.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author：lizhiqiang
 * Time：2017/7/15 19:24
 * Description：This is Book
 */
public class Book implements Parcelable{

    public int BookId;
    public String BookName;

    public Book(int BookId, String BookName){
        this.BookId = BookId;
        this.BookName = BookName;
    }

    protected Book(Parcel in) {
        BookId = in.readInt();
        BookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(BookId);
        dest.writeString(BookName);
    }

    @Override
    public String toString() {
        return "Book{" +
                "BookId=" + BookId +
                ", BookName='" + BookName + '\'' +
                '}';
    }
}
