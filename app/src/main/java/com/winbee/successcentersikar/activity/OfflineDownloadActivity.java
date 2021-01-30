package com.winbee.successcentersikar.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.winbee.successcentersikar.R;

public class OfflineDownloadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_download);
    }

    public void GotoDownload(View view) {
        startActivity(new Intent(this,ShowDownloadCourse.class));
    }

    public void GoToHOme(View view) {
        startActivity(new Intent(this,MainActivity.class));
        this.finish();
    }
}