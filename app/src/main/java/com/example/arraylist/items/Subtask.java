package com.example.arraylist.items;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class Subtask implements Parcelable {

    public Long id;
    public String task;

    public Subtask(Long id, String task) {
        this.id = id;
        this.task = task;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected Subtask(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        task = in.readString();
    }

    public static final Creator<Subtask> CREATOR = new Creator<Subtask>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Subtask createFromParcel(Parcel in) {
            return new Subtask(in);
        }

        @Override
        public Subtask[] newArray(int size) {
            return new Subtask[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(task);
        dest.writeString(id.toString());
    }
}
