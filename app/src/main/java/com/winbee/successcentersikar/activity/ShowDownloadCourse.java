package com.winbee.successcentersikar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winbee.successcentersikar.R;
import com.winbee.successcentersikar.adapter.AdapterCourse;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ShowDownloadCourse extends AppCompatActivity implements AdapterCourse.OnCourseClickListner {
    private ArrayList<File> pathCourse = new ArrayList<>();
    private ArrayList<File> pathSubject = new ArrayList<>();
    private RecyclerView recyclerview;
    private AdapterCourse adapter;
    private boolean onCourse=true;
    String path ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_download_course);
        recyclerview=findViewById(R.id.recyclerViewCourse);

        String pathtem = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/"
                + this.getBaseContext().getPackageName()
                + "/files/Download";
        File file = new File(pathtem);
        searchVid(file,"");

        if (pathCourse.isEmpty()) {
            TextView textView=findViewById(R.id.errorTextShowDownloadVideo);
            textView.setVisibility(View.VISIBLE);
        }







    }

    public void searchVid(File dir, String pattern) {
        // Log.d("1234", "searchVid: ");

        //Get the listfile of that flder
        final File listFile[] = dir.listFiles();

        pathSubject.clear();

        if (listFile != null) {
            if (onCourse) {
                pathCourse.addAll(Arrays.asList(listFile));
                populateRecyclerView(pathCourse);
            }else {
                pathSubject.addAll(Arrays.asList(listFile));
                populateRecyclerView(pathSubject);
            }


        } else {
            Log.d("1234", "searchVid: error");
        }
    }

    private void populateRecyclerView(ArrayList<File> pathCourse) {
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterCourse(this, pathCourse, this);
        recyclerview.setAdapter(adapter);

    }

    @Override
    public void onCourseClicked(int position) {

        if (onCourse) {
            onCourse=false;
            path = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Android/data/"
                    + this.getBaseContext().getPackageName()
                    + "/files/Download/"+pathCourse.get(position).getName();

                File file = new File(path);
                searchVid(file,"");

        }else {
            String pathsubject =path+"/"+pathSubject.get(position).getName();
            Log.d("TAG", pathsubject);
            Intent intent= new Intent(this,ShowDownload.class);
            intent.putExtra("path",pathsubject);
            startActivity(intent);

        }



    }

    @Override
    public void onBackPressed() {
        if (onCourse) {
            super.onBackPressed();
        }else {
            populateRecyclerView(pathCourse);
            onCourse=true;
        }

    }

    public void goback(View view) {
        if (onCourse) {
            onBackPressed();
        }else {
            populateRecyclerView(pathCourse);
            onCourse=true;
        }

    }
}