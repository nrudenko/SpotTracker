package com.github.nrudenko.spottracker.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class HotSpot implements Parcelable {
    public final LatLng latLng;
    public final String name;

    public HotSpot(LatLng latLng, String name) {
        this.latLng = latLng;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.latLng, flags);
        dest.writeString(this.name);
    }

    private HotSpot(Parcel in) {
        this.latLng = in.readParcelable(LatLng.class.getClassLoader());
        this.name = in.readString();
    }

    public static final Parcelable.Creator<HotSpot> CREATOR = new Parcelable.Creator<HotSpot>() {
        public HotSpot createFromParcel(Parcel source) {
            return new HotSpot(source);
        }

        public HotSpot[] newArray(int size) {
            return new HotSpot[size];
        }
    };
}