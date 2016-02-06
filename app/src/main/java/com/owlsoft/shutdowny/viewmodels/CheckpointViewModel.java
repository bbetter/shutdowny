package com.owlsoft.shutdowny.viewmodels;

import android.databinding.BaseObservable;
import android.view.View;

import com.owlsoft.shutdowny.models.Checkpoint;

import java.text.SimpleDateFormat;

/**
 * Created by mac on 06.02.16.
 */
public class CheckpointViewModel extends BaseObservable{
    private final Checkpoint checkpoint;

    public View.OnClickListener removeHandler;

    public CheckpointViewModel(Checkpoint checkpoint){
        this.checkpoint = checkpoint;
    }

    public String getWhenText(){
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
        return format.format(checkpoint.when.getTime());
    }

    public void setOnRemoveHandler(View.OnClickListener listener){
        removeHandler = listener;
    }

}
