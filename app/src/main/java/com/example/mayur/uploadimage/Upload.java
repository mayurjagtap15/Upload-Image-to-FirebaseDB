package com.example.mayur.uploadimage;

import android.test.suitebuilder.annotation.Suppress;

import com.google.firebase.database.Exclude;

public class Upload {

     @Exclude
    public String getmKey() {
        return mKey;
    }

    @Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    private String mKey;

    public Upload(String name, String imageurl) {
        this.name = name;
        this.imageurl = imageurl;


    }

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    String imageurl;

    public Upload() {

        // Empty Constructor needed

    }

}

