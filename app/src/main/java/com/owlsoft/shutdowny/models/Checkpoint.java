package com.owlsoft.shutdowny.models;

import android.databinding.BaseObservable;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mac on 06.02.16.
 */
public class Checkpoint extends BaseObservable{
    public long id;
    public Calendar when;

    public Checkpoint(){
        this.when = Calendar.getInstance();
    }

    public Checkpoint(Calendar calendar){
        this.when = calendar;
    }

    public boolean isPassed(){
        return Calendar.getInstance(Locale.getDefault()).getTimeInMillis() >= when.getTimeInMillis();
    }
}