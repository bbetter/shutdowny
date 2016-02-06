package com.owlsoft.shutdowny;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.ikovac.timepickerwithseconds.TimePicker;
import com.owlsoft.shutdowny.adapters.CheckpointAdapter;
import com.owlsoft.shutdowny.models.Checkpoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerListView;
    private CheckpointAdapter adapter;
    private Dialog dialog;
    private DbHelper dbHelper;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if(intent!=null && intent.hasExtra("fromBroadcast")){
            Toast.makeText(this,"root your device plz",Toast.LENGTH_LONG).show();
        }

        dbHelper = new DbHelper(MainActivity.this);
        startAlarmManager();
        adapter = new CheckpointAdapter(this, new ArrayList<>(dbHelper.getAllCheckPoints()));
        adapter.setDbOperationsListener(new CheckpointAdapter.DbOperationsListener() {
            @Override
            public boolean onItemRemoved(Checkpoint checkpoint) {
                return dbHelper.removeCheckpoint(checkpoint);
            }

            @Override
            public boolean onItemAdded(Checkpoint checkpoint) {
                return dbHelper.createCheckPoint(checkpoint);
            }
        });
        recyclerListView = (RecyclerView) findViewById(R.id.list);
        recyclerListView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        recyclerListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog == null) {
                    View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.picker_dialog_layout,null);
                    DatePicker datePicker = (DatePicker) v.findViewById(R.id.datePicker);
                    TimePicker timePicker = (TimePicker) v.findViewById(R.id.timePicker);
                    dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Choose date & time")
                            .setView(v)
                            .setPositiveButton("Done", new PositiveClickListener(datePicker,timePicker))
                            .create();
                }
                dialog.show();
            }
        });
    }

    public void startAlarmManager(){
        if (alarmManager == null) {
            alarmManager = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
        }
        List<Checkpoint> checkPoints = dbHelper.getAllCheckPoints();
        for(Checkpoint chk: checkPoints){
            Intent shutDownIntent = new Intent(this, ShutdownBroadcastReceiver.class);
            shutDownIntent.setData(Uri.parse("custom://"+String.valueOf(chk.id)));
            PendingIntent pintent = PendingIntent.getBroadcast(this,1,shutDownIntent,0);
            alarmManager.cancel(pintent);
            alarmManager.set(AlarmManager.RTC_WAKEUP,chk.when.getTimeInMillis(),pintent);
        }
    }

    class PositiveClickListener implements DialogInterface.OnClickListener{

        private DatePicker datePicker;
        private TimePicker timePicker;

        public PositiveClickListener(DatePicker datePicker , TimePicker timePicker){
            this.datePicker = datePicker;
            this.timePicker = timePicker;
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Calendar calendarInstance = Calendar.getInstance(Locale.getDefault());
            calendarInstance.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),timePicker.getCurrentHour(),timePicker.getCurrentMinute(),timePicker.getCurrentSeconds());
            adapter.addItem(new Checkpoint(calendarInstance));
            startAlarmManager();
        }
    }
}
