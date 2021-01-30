package com.winbee.successcentersikar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.service.autofill.UserData;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.winbee.successcentersikar.NewModels.PdfContent;
import com.winbee.successcentersikar.NewModels.PdfContentArray;
import com.winbee.successcentersikar.R;
import com.winbee.successcentersikar.RetrofitApiCall.ApiClient;
import com.winbee.successcentersikar.Utils.LocalData;
import com.winbee.successcentersikar.Utils.ProgressBarUtil;
import com.winbee.successcentersikar.Utils.SharedPrefManager;
import com.winbee.successcentersikar.Utils.Variable;
import com.winbee.successcentersikar.WebApi.ClientApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.balsikandar.crashreporter.CrashReporter.getContext;


public class YoutubeLibaray extends AppCompatActivity implements Player.EventListener {
    private static final String TAG = "VimeoActivityforlog";
    private static long LAST_CLICK_TIME = 0;
    private final int mDoubleClickInterval = 400; // Milliseconds
    private float currenttime = 0;
    private int forwardtimes = 1;
    RecyclerView messageRecyclerView;
    RelativeLayout relativeLayoutLive, title_bar_home, details_layout, relativeBack, relativeForward;
    private ProgressBarUtil progressBarUtil;
    private Button btn_pdf;
    private LinearLayout footer;
    String Referal_code;
    private TextView txt_subject, txt_topic, txt_teacher;
    private ArrayList<PdfContentArray> list;

    String UserId, Username, android_id;
    ImageView WebsiteHome, img_share, backimage, forwardImage, img_noti;
    LinearLayout layout_user, layout_test_series, layout_home, layout_doubt, layout_notification;


    //Youtube through exo

    private static final int ITAG_FOR_AUDIO = 140;
    private boolean isError;
    SimpleExoPlayer player;
    private PlayerView playerView;

    private FrameLayout exo_ffwd, exo_rew, moreButton;
    String downloadUrl;
    VideoMeta videoMeta;

    boolean fullscreen = false;
    private List<YtFragmentedVideo> formatsToShowList;

    int currentquality = 360;

    public static String youtubeLink;
    private ImageView fullscreenButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_libaray);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Referal_code = SharedPrefManager.getInstance(this).refCode().getREFERRAL_CODE();
        title_bar_home = findViewById(R.id.title_bar_home);


        UserId = SharedPrefManager.getInstance(this).refCode().getUserId();
        Username = SharedPrefManager.getInstance(this).refCode().getName();
        android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        details_layout = findViewById(R.id.details_layout);
        progressBarUtil = new ProgressBarUtil(this);
        relativeLayoutLive = findViewById(R.id.relative_layout_live);
        txt_subject = findViewById(R.id.txt_subject);
        txt_subject.setText("Subject - " + LocalData.SubjectName);
        txt_topic = findViewById(R.id.txt_topic);
        txt_topic.setText("Topic - " + LocalData.TopicName);
        txt_teacher = findViewById(R.id.txt_teacher);
        txt_teacher.setText("Teacher - " + LocalData.TeacherName);


        footer = findViewById(R.id.footer);

        layout_user = findViewById(R.id.layout_user);
        layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(YoutubeLibaray.this, DashboardCourseActivity.class);
                startActivity(profile);
            }
        });
        img_noti = findViewById(R.id.img_noti);
        img_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(YoutubeLibaray.this, NotificationActivity.class);
                startActivity(profile);
            }
        });
        layout_test_series = findViewById(R.id.layout_test_series);
        layout_test_series.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent test = new Intent(YoutubeLibaray.this, SubjectActivity.class);
                startActivity(test);
                // Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        layout_home = findViewById(R.id.layout_home);
        layout_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(YoutubeLibaray.this, MainActivity.class);
                startActivity(profile);
            }
        });
        layout_doubt = findViewById(R.id.layout_doubt);
        layout_doubt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(YoutubeLibaray.this, DiscussionActivity.class);
                startActivity(profile);
            }
        });
        layout_notification = findViewById(R.id.layout_notification);
        layout_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(YoutubeLibaray.this, ReferalActivity.class);
                startActivity(profile);
            }
        });
        WebsiteHome = findViewById(R.id.WebsiteHome);
        WebsiteHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YoutubeLibaray.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btn_pdf = findViewById(R.id.btn_pdf);
        btn_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo
                Intent intent = new Intent(YoutubeLibaray.this, StudyMaterial.class);
                startActivity(intent);
                finish();
            }
        });
        img_share = findViewById(R.id.img_share);
        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Success Center Sikar");
                    String shareMessage = "\nSuccess Center Sikar download the application.\n ";
                    String referalMessage = "\nMy Referal Code is .\n " + Referal_code;
                    shareMessage = shareMessage + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage + referalMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                }
            }
        });


        callTopicApiService();

        youtubeThroughExoSteup();
        Toast.makeText(this, "Loading your video...", Toast.LENGTH_SHORT).show();

    }

    private void youtubeThroughExoSteup() {
        playerView = findViewById(R.id.video_player_view);
        exo_rew = findViewById(R.id.exoo_rew);
        exo_ffwd = findViewById(R.id.exoo_ffwd);
        fullscreenButton = findViewById(R.id.fullscreenButton);
        moreButton = findViewById(R.id.moreButton);

        moreButton.setVisibility(View.GONE);
        fullscreenButton.setVisibility(View.GONE);

        exo_ffwd.setEnabled(false);
        exo_rew.setEnabled(false);

        exo_rew.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Log.d("TAG", "onSingleClick: rew");
                playerView.showController();
            }

            @Override
            public void onDoubleClick(View v) {
                long rewtime = player.getCurrentPosition() - 10000;
                if (player.getCurrentPosition() < 10000) {
                    player.seekTo(0);
                } else {
                    player.seekTo(rewtime);

                }
                Log.d("TAG", "onDoubleClick: rew " + rewtime);


            }
        });
        exo_ffwd.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Log.d("TAG", "onSingleClick: forward");
                playerView.showController();
            }

            @Override
            public void onDoubleClick(View v) {
                Log.d("TAG", "onDoubleClick: forward ");
                long ffwwtime = player.getCurrentPosition() + 10000;
                if (player.getCurrentPosition() > player.getDuration() - 10000) {
                    player.seekTo(player.getDuration());
                } else {
                    player.seekTo(ffwwtime);

                }
            }
        });
        String url = LocalData.VideoUrl;
        String completeUrl = "https://www.youtube.com/watch?v=" + url;
        Log.d(TAG, "youtubeThroughExoSteup: " + completeUrl);

        Log.d(TAG, "youtubeThroughExoSteup: "+LocalData.CourseName+" "+LocalData.SubjectName+" "+LocalData.TopicName);
        Log.d(TAG, "youtubeThroughExoSteup: "+LocalData.CourseName+" "+LocalData.TopicName+" "+LocalData.SubjectName);

        Log.d("TAG", "downloadFromUrl: "+SharedPrefManager.getInstance(this).refCode().getUsername()
                +"  "+SharedPrefManager.getInstance(this).refCode().getUserId());

        playVideo(completeUrl);

        setOnclickListnertoMoreButton();

        setOnclickListnertoFullscreenButton();


    }

    private void setOnclickListnertoFullscreenButton() {

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fullscreen) {
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(YoutubeLibaray.this, R.drawable.exo_controls_fullscreen_enter));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = (int) (250 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    title_bar_home.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);


                    fullscreen = false;
                } else {
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(YoutubeLibaray.this, R.drawable.exo_controls_fullscreen_exit));
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

                    title_bar_home.setVisibility(View.GONE);
                    footer.setVisibility(View.GONE);

                    playerView.setLayoutParams(params);


                    fullscreen = true;
                }
            }
        });

    }

    private void setOnclickListnertoMoreButton() {

        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(YoutubeLibaray.this, v);
                String quality, playbackSpeed, download;
                quality = "Quality";
                playbackSpeed = "Playback Speed";
                download = "Download";

                popupMenu.getMenu().add(quality);
                popupMenu.getMenu().add(playbackSpeed);
                popupMenu.getMenu().add(download);

//                popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_baseline_video_quality_24);
//                popupMenu.getMenu().getItem(1).setIcon(R.drawable.ic_baseline_speed_24);
//                popupMenu.getMenu().getItem(2).setIcon(R.drawable.ic_baseline_arrow_circle_down_24);


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().toString() == quality) {
                            Quality(v);

                        }
                        if (item.getTitle().toString() == playbackSpeed) {
                            playbackspeed(v);

                        }
                        if (item.getTitle().toString() == download) {

                             gotodownload();

                        }

                        return false;
                    }
                });

                popupMenu.show();

            }
        });


    }

    private void gotodownload() {
            startActivity(new Intent(this ,DownloadVideo.class));
    }

    private void callTopicApiService() {
        progressBarUtil.showProgress();
        ClientApi mService = ApiClient.getClient().create(ClientApi.class);
        Call<PdfContent> call = mService.getCoursePdf(5, UserId, "WB_010", LocalData.TopicDocumentId, android_id);
        call.enqueue(new Callback<PdfContent>() {
            @Override
            public void onResponse(Call<PdfContent> call, Response<PdfContent> response) {
                PdfContent pdfContent = response.body();

                int statusCode = response.code();
                list = new ArrayList();
                if (statusCode == 200) {
                    if (response.body().getError() == false) {
                        System.out.println("Suree body: " + response.body());
                        list = new ArrayList<>(Arrays.asList(Objects.requireNonNull(pdfContent).getData()));
                        //LocalData.PdfUrl=list.get(0).getURL();
                        Log.i("pdf", "onResponse: " + LocalData.PdfUrl);
                        progressBarUtil.hideProgress();
                    } else {
                        android.app.AlertDialog.Builder alertDialogBuilder =
                                new android.app.AlertDialog.Builder(YoutubeLibaray.this);
                        alertDialogBuilder.setTitle("Alert");
                        alertDialogBuilder
                                .setMessage(response.body().getError_Message())
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        forceLogout();
                                    }
                                });

                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } else {
                    System.out.println("Suree: response code" + response.message());
                    Toast.makeText(getApplicationContext(), response.body().getError_Message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<PdfContent> call, Throwable t) {
                System.out.println("Suree: " + t.getMessage());
            }
        });
    }

    private void addFormatToList(YtFile ytFile, SparseArray<YtFile> ytFiles) {
        //   Log.d("TAG1", "addFormatToList: " + ytFiles.size());


        int height = ytFile.getFormat().getHeight();
        if (height != -1) {
            for (YtFragmentedVideo frVideo : formatsToShowList) {
                if (frVideo.height == height && (frVideo.videoFile == null ||
                        frVideo.videoFile.getFormat().getFps() == ytFile.getFormat().getFps())) {
                    return;
                }
            }
        }
        YtFragmentedVideo frVideo = new YtFragmentedVideo();
        frVideo.height = height;
        if (ytFile.getFormat().isDashContainer()) {
            if (height > 0) {
                frVideo.videoFile = ytFile;
                frVideo.audioFile = ytFiles.get(ITAG_FOR_AUDIO);
            } else {
                frVideo.audioFile = ytFile;
            }
        } else {
            frVideo.videoFile = ytFile;
        }
        formatsToShowList.add(frVideo);
    }

    private void playThatVideo(String downloadUrl, boolean isError, String url, YtFragmentedVideo ytFragmentedVideo) {

        Log.d("1234", "playThatVideo: " + ytFragmentedVideo.audioFile);
        Log.d("1234", "playThatVideo: " + ytFragmentedVideo.videoFile);

        if (ytFragmentedVideo.audioFile == null) {
            if (player == null) {
                DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);

                player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
                playerView.setPlayer(player);

                DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, "exoplayer"));
                ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(downloadUrl));

                player.prepare(mediaSource);
                player.addListener(this);
                player.setPlayWhenReady(true);
            } else {


                long currentduration = player.getCurrentPosition();
                player.stop();
                player = null;


                DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
                player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector).build();
                playerView.setPlayer(player);

                DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(
                        this, Util.getUserAgent(this, "exoplayer"));
                ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(downloadUrl));

                player.prepare(mediaSource);
                player.addListener(this);
                player.setPlayWhenReady(true);
                player.seekTo(currentduration);

            }

        } else {
            long currentduration = player.getCurrentPosition();
            player.stop();
            player = null;


            DefaultTrackSelector trackSelectorforvideo = new DefaultTrackSelector(this);
            player = new SimpleExoPlayer.Builder(this).setTrackSelector(trackSelectorforvideo).build();
            playerView.setPlayer(player);

            DefaultDataSourceFactory defaultDataSourceFactoryforvideo = new DefaultDataSourceFactory(
                    this, Util.getUserAgent(this, "exoplayer"));

            MediaSource mediaSourcevideo = new ProgressiveMediaSource.Factory(defaultDataSourceFactoryforvideo)
                    .createMediaSource(Uri.parse(ytFragmentedVideo.videoFile.getUrl()));
            MediaSource mediaSourceforAudio = new ProgressiveMediaSource.Factory(defaultDataSourceFactoryforvideo)
                    .createMediaSource(Uri.parse(ytFragmentedVideo.audioFile.getUrl()));

            MergingMediaSource mergedSource = new MergingMediaSource(mediaSourcevideo, mediaSourceforAudio);


            player.prepare(mergedSource);
            player.addListener(this);
            player.setPlayWhenReady(true);
            player.seekTo(currentduration);


        }
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);


    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case SimpleExoPlayer.STATE_READY:
                /// qualitybutton.setEnabled(true);
                exo_ffwd.setEnabled(true);
                exo_rew.setEnabled(true);
                moreButton.setEnabled(true);
                fullscreenButton.setEnabled(true);
                moreButton.setVisibility(View.VISIBLE);
                fullscreenButton.setVisibility(View.VISIBLE);


                break;
        }
    }

    public void playbackspeed(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);
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
                return false;
            }
        });
        popupMenu.show();

    }

    public void Quality(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        for (int i = 0; i < formatsToShowList.size(); i++) {
            YtFragmentedVideo ytFrVideo = formatsToShowList.get(i);
            if (ytFrVideo.height == -1) {
                Log.d("TAG", "Audio" + ytFrVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s");
            } else {
                Log.d("TAG", (ytFrVideo.videoFile.getFormat().getFps() == 60) ? ytFrVideo.height + "p60" :
                        ytFrVideo.height + "p");
                popupMenu.getMenu().add(ytFrVideo.height + "p");
            }
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int index;
                String title = item.getTitle().toString().substring(0, item.getTitle().length() - 1);
                Log.d("TAG", "onMenuItemClick: " + title);
                for (int i = 0; i < formatsToShowList.size(); i++) {
                    YtFragmentedVideo ytFrVideo = formatsToShowList.get(i);
                    if (ytFrVideo.height == -1) {
                        Log.d("TAG", "Audio" + ytFrVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s");
                    } else {
                        Log.d("TAG", (ytFrVideo.videoFile.getFormat().getFps() == 60) ? ytFrVideo.height + "p60" :
                                ytFrVideo.height + "p");
                        if (ytFrVideo.height == Integer.parseInt(title)) {
                            if (ytFrVideo.height != currentquality) {
                                currentquality = Integer.parseInt(title);
                                playThatVideo(ytFrVideo.videoFile.getUrl(), false, formatsToShowList.get(0).audioFile.getUrl(), formatsToShowList.get(i));
                            }
                        }
                    }


                }

                return false;
            }
        });
        popupMenu.show();
    }


    public void playVideo(String youtube) {

        youtubeLink = youtube;
        new YouTubeExtractor(this) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {

                    downloadUrl = "";
                    try {
                        isError = false;
                        //   downloadUrl = ytFiles.get(itag).getUrl();
                        videoMeta = vMeta;
                        formatsToShowList = new ArrayList<>();
                        for (int i = 0, itag; i < ytFiles.size(); i++) {
                            itag = ytFiles.keyAt(i);
                            YtFile ytFile = ytFiles.get(itag);


                            if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 0) {
                                addFormatToList(ytFile, ytFiles);
                            }
                        }
                        Collections.sort(formatsToShowList, new Comparator<YtFragmentedVideo>() {
                            @Override
                            public int compare(YtFragmentedVideo lhs, YtFragmentedVideo rhs) {
                                return lhs.height - rhs.height;
                            }
                        });
                        // playThatVideo(formatsToShowList.get(2).videoFile.getUrl(),false);
                        Log.d("TAG", "onExtractionComplete: " + formatsToShowList.size());
                        for (int i = 0; i < formatsToShowList.size(); i++) {
                            YtFragmentedVideo ytFrVideo = formatsToShowList.get(i);
                            if (ytFrVideo.height == -1) {
                                Log.d("TAG", "Audio" + ytFrVideo.audioFile.getFormat().getAudioBitrate() + " kbit/s");
                            } else {
                                Log.d("TAG", (ytFrVideo.videoFile.getFormat().getFps() == 60) ? ytFrVideo.height + "p60" :
                                        ytFrVideo.height + "p");
                                if (ytFrVideo.height == currentquality) {
                                    playThatVideo(ytFrVideo.videoFile.getUrl(), false, formatsToShowList.get(0).audioFile.getUrl(), formatsToShowList.get(i));
                                }
                            }


                        }

                    } catch (Exception e) {
                        isError = true;
                        downloadUrl = "error";
                        Log.d("TAG", "onExtractionComplete: " + e.getMessage());
                    }
                    //playThatVideo(downloadUrl, isError);
                    // Log.d("TAG", "onExtractionComplete: " + downloadUrl);
                }
            }
        }.extract(youtubeLink, true, true);

    }


    public abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v);
            } else {
                onSingleClick(v);
            }
            lastClickTime = clickTime;
        }

        public abstract void onSingleClick(View v);

        public abstract void onDoubleClick(View v);
    }


    private void forceLogout() {
        SharedPrefManager.getInstance(this).logout();
        startActivity(new Intent(this, LoginActivity.class));
        Objects.requireNonNull(this).finish();
    }


    private class YtFragmentedVideo {
        int height;
        YtFile audioFile;
        YtFile videoFile;
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
                player.release();
            } else {
                player.stop();
                player.release();
            }
        }
        super.onDestroy();
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
    protected void onResume() {
        if (player != null) {
            if (player.isPlaying()) {
                player.setPlayWhenReady(true);
            }
        }
        super.onResume();
    }

    @Override

    public void onBackPressed() {
        if (!fullscreen) {
            super.onBackPressed();
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (getSupportActionBar() != null) {
                getSupportActionBar().show();
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
            playerView.setLayoutParams(params);
            footer.setVisibility(View.VISIBLE);
            title_bar_home.setVisibility(View.VISIBLE);
            fullscreen = false;
        }
    }

}