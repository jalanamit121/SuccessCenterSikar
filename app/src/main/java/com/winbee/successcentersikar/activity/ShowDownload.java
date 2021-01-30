package com.winbee.successcentersikar.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.winbee.successcentersikar.R;
import com.winbee.successcentersikar.adapter.AdapterShowDownload;

import java.io.File;
import java.util.ArrayList;

public class ShowDownload extends AppCompatActivity implements AdapterShowDownload.OnVideoClickListner, Player.EventListener {

    private RecyclerView recyclerView;

    AdapterShowDownload adapter;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    String path;
    private boolean playingVideo = false;
    private ImageView playbackSpeed;
    private RelativeLayout titlebar;
    private boolean fullscreen=false;
    private ImageView fullscreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_download);

        recyclerView = findViewById(R.id.recyclerView);
        playerView = findViewById(R.id.exoplayerDownload);
        playbackSpeed = findViewById(R.id.playbackSpeedOfflineDownload);
        titlebar=findViewById(R.id.title_bar_home);
        fullscreenButton = findViewById(R.id.fullScreenButton);



        Intent intent = getIntent();
        if (intent != null) {
            path = intent.getStringExtra("path");
        }


        File file = new File(path);

        final File listFile[] = file.listFiles();

        searchVid(file, "abc");
        searchVid(file, "cba");
        Log.d("1234", "onCreate: " + pathForVideo);


        if (pathForVideo.isEmpty()) {
            TextView textView=findViewById(R.id.errorTextShowDownloadVideo);
            textView.setVisibility(View.VISIBLE);
        }



        playbackSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ShowDownload.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.speed, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String id = String.valueOf(item.getTitle());
                        String value = id.substring(0, id.length() - 1);
                        String playbackspeed = value + "f";

                        Log.d("TAG", "onMenuItemClick: " + id);
                        Log.d("TAG", "onMenuItemClick: " + value);
                        Log.d("TAG", "onMenuItemClick: " + Float.parseFloat(playbackspeed));

                        PlaybackParameters param = new PlaybackParameters(Float.parseFloat(playbackspeed));
                        player.setPlaybackParameters(param);


                        if (fullscreen) {
                            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().hide();
                            }
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = ViewGroup.LayoutParams.MATCH_PARENT;

                            playerView.setLayoutParams(params);

                            titlebar.setVisibility(View.GONE);
                        }















                        return false;
                    }
                });
                popupMenu.show();

            }
        });


    }

    private ArrayList<File> pathForVideo = new ArrayList<>();
    private ArrayList<File> pathForAudio = new ArrayList<>();

    public void searchVid(File dir, String pattern) {
        // Log.d("1234", "searchVid: ");

        //Get the listfile of that flder
        final File listFile[] = dir.listFiles();


        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].getName().endsWith(pattern)) {
                    // Do what ever u want, add the path of the video to the list
                    if (pattern == "abc") {
                        pathForVideo.add(listFile[i]);
                    }
                    if (pattern == "cba") {
                        pathForAudio.add(listFile[i]);
                    }

                    // Log.d("1234", "searchVid: " + listFile[i]);
                }
            }

            populateRecyclerView();
        } else {
            Log.d("1234", "searchVid: error");
        }
    }

    private void populateRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdapterShowDownload(this, pathForVideo, this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onVideoClicked(int position) {
        Log.d("1234", "onVideoClicked: ");
        recyclerView.setVisibility(View.GONE);
        titlebar.setVisibility(View.VISIBLE);
        playvideo(position);
    }

    private void playvideo(int position) {
        playingVideo = true;
        String audioUriString = null;
        Uri uriaudio = null;

        playerView.setVisibility(View.VISIBLE);
        player = new SimpleExoPlayer.Builder(this).setTrackSelector(new DefaultTrackSelector(this)).build();
        playerView.setPlayer(player);

        Uri uri = Uri.fromFile(pathForVideo.get(position));
        //  Log.d("TAG", "playvideo: " + uri);

        String videoUriWithoutFormat = uri.toString().substring(0,
                uri.toString().length() - 4);

        String videoUriString = videoUriWithoutFormat + ".mp4";
        Log.d("TAG", "playvideo: " + videoUriString);


        for (int i = 0; i < pathForAudio.size(); i++) {


            //  Log.d("TAG", "playvideo: " + uriaudio);
            Uri uriaudiotem = Uri.fromFile(pathForAudio.get(i));

            String audioUriWithoutFormat = uriaudiotem.toString().substring(0, uriaudiotem.toString().length() - 4);
            // Log.d("TAG", "" + audioUriWithoutFormat + "   " + videoUriWithoutFormat);


            if (audioUriWithoutFormat.equals(videoUriWithoutFormat)) {
                uriaudio = Uri.fromFile(pathForAudio.get(i));
            }

        }

        //  Log.d("TAG", "playvideo: " + audioUriString);
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "exoplayer"));


        if (uriaudio == null) {


            ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(uri);
            player.prepare(mediaSource);
        } else {


            MediaSource mediaSourcevideo = new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(uri);
            MediaSource mediaSourceforAudio = new ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(uriaudio);

            MergingMediaSource mergedSource = new MergingMediaSource(mediaSourcevideo, mediaSourceforAudio);

            player.prepare(mergedSource);
        }

        player.setPlayWhenReady(true);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        player.addListener(this);
        playingVideo = true;


    }


    @Override
    protected void onStop() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
                player.release();
            } else {
                player.stop();
                player.release();
            }
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if (player != null) {
            if (player.isPlaying()) {
                player.setPlayWhenReady(false);
            }
        }


        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (playingVideo) {
            if (fullscreen) {
                fullscreenButton.setImageDrawable(ContextCompat.getDrawable(ShowDownload.this, R.drawable.exo_controls_fullscreen_enter));
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().show();
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = (int) (250 * getApplicationContext().getResources().getDisplayMetrics().density);
                playerView.setLayoutParams(params);
                titlebar.setVisibility(View.VISIBLE);


                fullscreen = false;

            }else {
                if (player != null) {
                    if (player.isPlaying()) {
                        player.stop();
                        player.release();
                    } else {
                        player.stop();
                        player.release();
                    }
                }
                playingVideo = false;
                playerView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                titlebar.setVisibility(View.VISIBLE);

            }


        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case SimpleExoPlayer.STATE_READY:


        }
    }

    public void goback(View view) {
        if (playingVideo) {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                    player.release();
                } else {
                    player.stop();
                    player.release();
                }
            }
            playingVideo = false;
            playerView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            titlebar.setVisibility(View.VISIBLE);

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }

    public void changeScreen(View view) {

        if (fullscreen) {
            fullscreenButton.setImageDrawable(ContextCompat.getDrawable(ShowDownload.this, R.drawable.exo_controls_fullscreen_enter));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (250 * getApplicationContext().getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
            titlebar.setVisibility(View.VISIBLE);
            fullscreen = false;
        } else {
            fullscreenButton.setImageDrawable(ContextCompat.getDrawable(ShowDownload.this, R.drawable.exo_controls_fullscreen_exit));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            if (getSupportActionBar() != null) {
                getSupportActionBar().hide();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;


            playerView.setLayoutParams(params);
            titlebar.setVisibility(View.GONE);
            fullscreen = true;
        }
    }
}
