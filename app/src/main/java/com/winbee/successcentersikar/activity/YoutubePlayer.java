package com.winbee.successcentersikar.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.winbee.successcentersikar.NewModels.LiveChatMessage;
import com.winbee.successcentersikar.NewModels.LiveMessage;
import com.winbee.successcentersikar.NewModels.LiveMessageArray;
import com.winbee.successcentersikar.R;
import com.winbee.successcentersikar.RetrofitApiCall.ApiClient;
import com.winbee.successcentersikar.Utils.LocalData;
import com.winbee.successcentersikar.Utils.ProgressBarUtil;
import com.winbee.successcentersikar.Utils.SharedPrefManager;
import com.winbee.successcentersikar.WebApi.ClientApi;
import com.winbee.successcentersikar.adapter.ServerMessageAdapter;
import com.winbee.successcentersikar.model.LiveChatModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.balsikandar.crashreporter.CrashReporter.getContext;

public class YoutubePlayer extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String API_KEY = "AIzaSyBlEPocq2s2bDmWDMBRXAf8Mhf3wlFNYGI";
    public static final String VIDEO_ID = "j36wPW4bGIs";
    private Button btn_study_material;
    private ImageView img_share, WebsiteHome;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    EditText editTextMessageLive;
    ImageView sendMessageLive;
    RecyclerView messageRecyclerView;
    private TextView textNoMessageLive;
    ProgressBar progressBar;
    String Referal_code;
    RelativeLayout relativeLayoutLive;
    ArrayList<LiveChatModel> messagesArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static String userIdAuth;
    LinearLayout layout_user, layout_test_series, layout_home, layout_doubt, layout_notification;
    private YouTubePlayerView youTubePlayerView;
    Boolean fullscreen = false;
    YouTubePlayer player;
    String UserId, Username, android_id;
    private ArrayList<LiveMessageArray> list;
    private ServerMessageAdapter adapter;
    private ProgressBarUtil progressBarUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);
        progressBarUtil = new ProgressBarUtil(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        UserId = SharedPrefManager.getInstance(this).refCode().getUserId();
        Username = SharedPrefManager.getInstance(this).refCode().getName();
        Referal_code = SharedPrefManager.getInstance(this).refCode().getREFERRAL_CODE();
        android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        layout_user = findViewById(R.id.layout_user);
        layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(YoutubePlayer.this, DashboardCourseActivity.class);
                startActivity(profile);
            }
        });
        layout_test_series = findViewById(R.id.layout_test_series);
        layout_test_series.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent test = new Intent(YoutubePlayer.this, SubjectActivity.class);
                startActivity(test);
                // Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        layout_home = findViewById(R.id.layout_home);
        layout_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(YoutubePlayer.this, MainActivity.class);
                startActivity(profile);
            }
        });
        layout_doubt = findViewById(R.id.layout_doubt);
        layout_doubt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(YoutubePlayer.this, DiscussionActivity.class);
                startActivity(profile);
            }
        });
        layout_notification = findViewById(R.id.layout_notification);
        layout_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(YoutubePlayer.this, NotificationActivity.class);
                startActivity(profile);
            }
        });
        WebsiteHome = findViewById(R.id.WebsiteHome);
        WebsiteHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                //return true;
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        editTextMessageLive = findViewById(R.id.editTextMessageLive);
        sendMessageLive = findViewById(R.id.sendMessageLive);
        sendMessageLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageSend();
            }
        });
        messageRecyclerView = findViewById(R.id.liveMessageRecyclerViewLive);
        textNoMessageLive = findViewById(R.id.textNoMessageLive);
        progressBar = findViewById(R.id.progressBarLive);
        relativeLayoutLive = findViewById(R.id.relative_layout_live);
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
                    //e.toString();
                    //+ BuildConfig.APPLICATION_ID +"\n\n"
                }
            }
        });


        btn_study_material = findViewById(R.id.btn_study_material);
        btn_study_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(YoutubePlayer.this, StudyMaterial.class);
                startActivity(intent);
            }
        });
        /** Initializing YouTube Player View **/
        youTubePlayerView = findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(API_KEY, this);
    }

    private void setSupportActionBar(Toolbar toolbar) {
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player, boolean wasRestored) {
        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        player.play();
        this.player = player;
        player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean b) {
                if (b) {
                    player.play();
                    fullscreen = true;

                }
                if (!b) {
                    if (!player.isPlaying()) {
                        player.play();
                    }
                    fullscreen = false;

                }
            }
        });

        /** Start buffering **/
        if (!wasRestored) {
            //player.cueVideo(topicName.getURL());
            player.cueVideo(LocalData.VideoUrl);
            // player.cueVideo(VIDEO_ID);
        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onPlaying() {
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };


    private void messageSend() {
        final String message = editTextMessageLive.getText().toString();
        LocalData.Message = message;
        if (TextUtils.isEmpty(message)) {
            editTextMessageLive.setError("enter some message");
            editTextMessageLive.requestFocus();
            return;
        }


        callMessageSendApi();

    }

    private void callMessageSendApi() {
        ClientApi apiCall = ApiClient.getClient().create(ClientApi.class);
        Call<LiveChatMessage> call = apiCall.getLiveMessage(1, UserId, Username, LocalData.DocumentId, LocalData.Message, android_id);
        Log.i("tag", "callMessageSendApi: " + UserId + Username + LocalData.LiveId + LocalData.Message);
        call.enqueue(new Callback<LiveChatMessage>() {
            @Override
            public void onResponse(Call<LiveChatMessage> call, Response<LiveChatMessage> response) {
                int statusCode = response.code();
                if (statusCode == 200 && response.body().getResponse()) {
                    if (!response.body().getError()) {
                        editTextMessageLive.getText().clear();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                        callAllMessageFetch();
                    } else {
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                                YoutubePlayer.this);
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
                    System.out.println("Sur: response code" + response.message());
                    Toast.makeText(getApplicationContext(), "Ërror due to" + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LiveChatMessage> call, Throwable t) {
                System.out.println("Suree: " + t.getMessage());
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void callAllMessageFetch() {
        ClientApi apiCall = ApiClient.getClient().create(ClientApi.class);
        Call<LiveMessage> call = apiCall.getLiveMessageFetch(2, UserId, android_id, LocalData.DocumentId);
        call.enqueue(new Callback<LiveMessage>() {
            @Override
            public void onResponse(Call<LiveMessage> call, Response<LiveMessage> response) {
                LiveMessage liveMessage = response.body();
                int statusCode = response.code();
                list = new ArrayList();
                if (statusCode == 200) {
                    if (response.body().getResponse() == true) {
                        if (response.body().getError() == false) {
                            textNoMessageLive.setVisibility(View.GONE);
                            list = new ArrayList<>(Arrays.asList(Objects.requireNonNull(liveMessage).getData()));
                            System.out.println("Suree body: " + list);
                            adapter = new ServerMessageAdapter(YoutubePlayer.this, list);
                            messageRecyclerView.setAdapter(adapter);
                        } else {
                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                                    YoutubePlayer.this);
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
                        textNoMessageLive.setVisibility(View.VISIBLE);
                    }


                } else {
                    System.out.println("Suree: response code" + response.message());
                    Toast.makeText(getApplicationContext(), "Ërror due to" + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LiveMessage> call, Throwable t) {
                System.out.println("Suree: " + t.getMessage());
                progressBarUtil.hideProgress();
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fullscreen) {
                player.setFullscreen(false);
                player.play();

            } else {
                super.onBackPressed();
            }
        }


    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent home = new Intent(YoutubePlayer.this, MainActivity.class);
            startActivity(home);
        } else if (id == R.id.nav_profile) {
            Intent profile = new Intent(YoutubePlayer.this, DashboardCourseActivity.class);
            startActivity(profile);

        } else if (id == R.id.nav_course) {
            Intent course = new Intent(YoutubePlayer.this, MyPurchasedCourseActivity.class);
            startActivity(course);
        } else if (id == R.id.nav_test_series) {
            Intent test = new Intent(YoutubePlayer.this, AllPurchasedTestActivity.class);
            startActivity(test);
        } else if (id == R.id.nav_txn) {
            Intent txn = new Intent(YoutubePlayer.this, MyTransactionActivity.class);
            startActivity(txn);
        } else if (id == R.id.nav_live_class) {
            Intent live = new Intent(YoutubePlayer.this, YouTubeVideoList.class);
            startActivity(live);
        } else if (id == R.id.nav_pdf) {
            Intent pdf = new Intent(YoutubePlayer.this, AllPurchasedPdfActivity.class);
            startActivity(pdf);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(YoutubePlayer.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(YoutubePlayer.this, ContactUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_referal) {
            Intent intent = new Intent(YoutubePlayer.this, ReferalActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Success Center Sikar");
                String shareMessage = "\nSuccess Center Sikar download the application.\n ";
                shareMessage = shareMessage + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName();
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch (Exception e) {
            }

        } else if (id == R.id.nav_logout) {
            forceLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void forceLogout() {
        SharedPrefManager.getInstance(this).logout();
        startActivity(new Intent(this, LoginActivity.class));
        Objects.requireNonNull(this).finish();
    }
}
