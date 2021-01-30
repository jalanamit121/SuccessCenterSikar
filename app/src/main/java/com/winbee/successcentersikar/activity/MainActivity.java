package com.winbee.successcentersikar.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.balsikandar.crashreporter.BuildConfig;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sanojpunchihewa.updatemanager.UpdateManager;
import com.sanojpunchihewa.updatemanager.UpdateManager.FlexibleUpdateDownloadListener;
import com.sanojpunchihewa.updatemanager.UpdateManager.UpdateInfoListener;
import com.sanojpunchihewa.updatemanager.UpdateManagerConstant;
import com.winbee.successcentersikar.NewModels.CourseContent;
import com.winbee.successcentersikar.NewModels.CourseContentArray;
import com.winbee.successcentersikar.NewModels.LogOut;
import com.winbee.successcentersikar.NewModels.PdfSell;
import com.winbee.successcentersikar.NewModels.PdfSellArray;
import com.winbee.successcentersikar.R;
import com.winbee.successcentersikar.RetrofitApiCall.ApiClient;
import com.winbee.successcentersikar.RetrofitApiCall.OnlineTestApiClient;
import com.winbee.successcentersikar.Utils.AppUpdateChecker;
import com.winbee.successcentersikar.Utils.OnlineTestData;
import com.winbee.successcentersikar.Utils.ProgressBarUtil;
import com.winbee.successcentersikar.Utils.SharedPrefManager;
import com.winbee.successcentersikar.WebApi.ClientApi;
import com.winbee.successcentersikar.adapter.AllCourseAdapter;
import com.winbee.successcentersikar.adapter.AllPdfSellAdapter;
import com.winbee.successcentersikar.adapter.AllPerminumTestAdapter;
import com.winbee.successcentersikar.model.BannerModel;
import com.winbee.successcentersikar.model.LiveStatus;
import com.winbee.successcentersikar.model.SectionDetailsDataModel;
import com.winbee.successcentersikar.model.SectionDetailsMainModel;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.supercharge.shimmerlayout.ShimmerLayout;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.balsikandar.crashreporter.CrashReporter.getContext;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    UpdateManager mUpdateManager;
    private String Username, UserID, android_id, UserMobile;
    private String UserPassword;
    private GifImageView image_gif;
    String Referal_code;
    TextView txt_discount, user_name;
    TextView txtFlexibleUpdateProgress;
    private ShimmerLayout shimmerLayout, shimmerLayout1, shimmerLayout2;
    ImageView img_share;
    private ProgressBarUtil progressBarUtil;
    private ArrayList<CourseContentArray> list1;
    private ArrayList<PdfSellArray> pdfSellArrays;
    ImageSlider img_video_thumbails;
    private RecyclerView video_list_recycler, recycle_test_series, gec_home_recycle_pdf;
    private AllCourseAdapter CourseAdapter;
    private AllPdfSellAdapter pdfSellAdapter;
    ImageView WebsiteHome, img_noti;
    SwipeRefreshLayout refresh_main;
    boolean version = false;
    LinearLayout layout_user, layout_test_series, layout_home, layout_doubt, layout_notification,
            layout_course, layout_class, layout_test, layout_quiz;
    String sCurrentVersion, sLastestVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
        }


        if (connected) {
            doYourStuff();
        }else {

            startActivity(new Intent(this,OfflineDownloadActivity.class));
            this.finish();
        }




    }

    private void doYourStuff() {
        Log.d("TAG", "onCreate: home");
        FacebookSdk.setAutoInitEnabled(true);
        txtFlexibleUpdateProgress = findViewById(R.id.txt_flexible_progress);
        FacebookSdk.fullyInitialize();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Referal_code = SharedPrefManager.getInstance(this).refCode().getREFERRAL_CODE();
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.winbee.successcentersikar",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        //firebase Common notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Common", "Common", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        //firebase live class notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("liveClass", "liveClass", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        //firebase test series notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("testSeries", "testSeries", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        FirebaseMessaging.getInstance().subscribeToTopic("SuccessCenterSikar")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "";
                        if (!task.isSuccessful()) {
                            msg = "failed";
                        }

                        //Toast.makeText(GecHomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        AppUpdateChecker appUpdateChecker = new AppUpdateChecker(this);  //pass the activity in constructure
        appUpdateChecker.checkForUpdate(false); //mannual check false here


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        shimmerLayout1 = findViewById(R.id.shimmerLayout1);
        shimmerLayout = findViewById(R.id.shimmerLayout);
        shimmerLayout2 = findViewById(R.id.shimmerLayout2);
        android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        txt_discount = findViewById(R.id.txt_discount);
        user_name = findViewById(R.id.user_name);
        Username = SharedPrefManager.getInstance(this).refCode().getName();
        UserID = SharedPrefManager.getInstance(this).refCode().getUserId();
        user_name.setText("Hello" + " " + Username);
        img_video_thumbails = findViewById(R.id.img_video_thumbails);
        progressBarUtil = new ProgressBarUtil(this);
        video_list_recycler = findViewById(R.id.gec_home_recycle);
        recycle_test_series = findViewById(R.id.recycle_test_series);
        gec_home_recycle_pdf = findViewById(R.id.gec_home_recycle_pdf);
        AllCourseAdapter allCourseAdapter = new AllCourseAdapter(this, list1);
        video_list_recycler.setLayoutManager(new GridLayoutManager(this, 1));
        video_list_recycler.setAdapter(allCourseAdapter);
        WebsiteHome = findViewById(R.id.WebsiteHome);
        UserMobile = SharedPrefManager.getInstance(this).refCode().getUsername();
        UserPassword = SharedPrefManager.getInstance(this).refCode().getPassword();
        image_gif = findViewById(R.id.image_gif);
        image_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent live = new Intent(MainActivity.this, YouTubeVideoList.class);
                startActivity(live);
            }
        });
        layout_user = findViewById(R.id.layout_user);
        layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(MainActivity.this, DashboardCourseActivity.class);
                startActivity(profile);
            }
        });
        img_noti = findViewById(R.id.img_noti);
        img_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(MainActivity.this, ShowDownloadCourse.class);
                startActivity(profile);
            }
        });
        layout_test_series = findViewById(R.id.layout_test_series);
        layout_test_series.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent test = new Intent(MainActivity.this, SubjectActivity.class);
                startActivity(test);
                // Toast.makeText(MainActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        layout_home = findViewById(R.id.layout_home);
        layout_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(MainActivity.this, MainActivity.class);
                startActivity(profile);
            }
        });
        layout_doubt = findViewById(R.id.layout_doubt);
        layout_doubt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(MainActivity.this, DiscussionActivity.class);
                startActivity(profile);
            }
        });
        layout_notification = findViewById(R.id.layout_notification);
        layout_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(MainActivity.this, ReferalActivity.class);
                startActivity(profile);
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

        layout_course = findViewById(R.id.layout_course);
        layout_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(MainActivity.this, MyPurchasedCourseActivity.class);
                startActivity(profile);
            }
        });
        layout_class = findViewById(R.id.layout_class);
        layout_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(MainActivity.this, YouTubeVideoList.class);
                startActivity(profile);
            }
        });
        layout_test = findViewById(R.id.layout_test);
        layout_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(MainActivity.this, SubjectActivity.class);
                startActivity(profile);
            }
        });
        layout_quiz = findViewById(R.id.layout_quiz);
        layout_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profile = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(profile);
            }
        });


        callBannerService();
        callTestApiService();
        callApiService();
        callLiveClassService();
        callPdfService();

        refresh_main = findViewById(R.id.refresh_main);
        refresh_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callBannerService();
                callTestApiService();
                callApiService();
                callPdfService();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh_main.setRefreshing(false);
                    }
                }, 4000);
            }
        });
        WebsiteHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.txt_user);
        TextView nav_user_nuber = (TextView) hView.findViewById(R.id.txt_user_number);
        nav_user.setText(SharedPrefManager.getInstance(this).refCode().getName());
        nav_user_nuber.setText(SharedPrefManager.getInstance(this).refCode().getUsername());

        img_video_thumbails = findViewById(R.id.img_video_thumbails);
        img_video_thumbails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, YouTubeVideoList.class);
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
        Username = intent.getStringExtra("username");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent home = new Intent(MainActivity.this, MainActivity.class);
            startActivity(home);
        } else if (id == R.id.nav_profile) {
            Intent profile = new Intent(MainActivity.this, DashboardCourseActivity.class);
            startActivity(profile);

        } else if (id == R.id.nav_course) {
            Intent course = new Intent(MainActivity.this, MyPurchasedCourseActivity.class);
            startActivity(course);
        } else if (id == R.id.nav_test_series) {
            Intent test = new Intent(MainActivity.this, AllPurchasedTestActivity.class);
            startActivity(test);
        } else if (id == R.id.nav_pdf) {
            Intent pdf = new Intent(MainActivity.this, AllPurchasedPdfActivity.class);
            startActivity(pdf);
        } else if (id == R.id.nav_txn) {
            Intent txn = new Intent(MainActivity.this, MyTransactionActivity.class);
            startActivity(txn);
        } else if (id == R.id.nav_live_class) {
            Intent live = new Intent(MainActivity.this, YouTubeVideoList.class);
            startActivity(live);

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(MainActivity.this, ContactUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));

        } else if (id == R.id.nav_referal) {
            Intent intent = new Intent(MainActivity.this, ReferalActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
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

        } else if (id == R.id.nav_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void callBannerService() {
        progressBarUtil.showProgress();
        ClientApi apiCAll = ApiClient.getClient().create(ClientApi.class);
        Call<ArrayList<BannerModel>> call = apiCAll.getBanner("WB_010");
        call.enqueue(new Callback<ArrayList<BannerModel>>() {
            @Override
            public void onResponse(Call<ArrayList<BannerModel>> call, Response<ArrayList<BannerModel>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
//                    System.out.println("Suree body: " + response.body().getFile());
//                    Picasso.get().load(response.body().getFile())
//                            .placeholder(R.drawable.dummyimage)
//                            .into(img_video_thumbails);
                    ArrayList<BannerModel> bannerModel = response.body();
                    List<SlideModel> bannerModels = new ArrayList<>();
                    //  bannerModels.add(new SlideModel(String.valueOf(Arrays.asList(bannerModel.getFile()))));
                    for (int i = 0; i < bannerModel.size(); i++) {
                        bannerModels.add(new SlideModel(bannerModel.get(i).getFile()));
                    }
                    img_video_thumbails.setImageList(bannerModels, false);
                    progressBarUtil.hideProgress();
                } else {
                    progressBarUtil.hideProgress();
                    System.out.println("Suree: response code" + response.message());
                    Toast.makeText(getApplicationContext(), "NetWork Issue,Please Check Network Connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BannerModel>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();

                System.out.println("Suree: Error " + t.getMessage());
            }
        });
    }

    // live class status
    private void callLiveClassService() {
        progressBarUtil.showProgress();
        ClientApi apiCAll = ApiClient.getClient().create(ClientApi.class);
        Call<LiveStatus> call = apiCAll.getLiveStatus("WB_010");
        call.enqueue(new Callback<LiveStatus>() {
            @Override
            public void onResponse(Call<LiveStatus> call, Response<LiveStatus> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if (response.body().getLivestatus() == true) {
                        image_gif.setVisibility(View.VISIBLE);
                        progressBarUtil.hideProgress();
                    } else {
                        image_gif.setVisibility(View.GONE);
                        progressBarUtil.hideProgress();
                    }
                } else {
                    progressBarUtil.hideProgress();
                    System.out.println("Suree: response code" + response.message());
                    Toast.makeText(getApplicationContext(), "NetWork Issue,Please Check Network Connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LiveStatus> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();

                System.out.println("Suree: Error " + t.getMessage());
            }
        });
    }


    private void callApiService() {
        progressBarUtil.showProgress();
        apiCallTest();
        ClientApi apiCAll = ApiClient.getClient().create(ClientApi.class);
        Call<CourseContent> call = apiCAll.getBranchId(1, "WB_010", "WB_010", UserID, android_id);
        call.enqueue(new Callback<CourseContent>() {
            @Override
            public void onResponse(Call<CourseContent> call, Response<CourseContent> response) {
                apiCalledTest();
                CourseContent courseContent = response.body();
                int statusCode = response.code();
                list1 = new ArrayList();
                if (statusCode == 200) {
                    if (response.body().getError() == false) {
                        System.out.println("Suree body: " + response.body());
                        list1 = new ArrayList<>(Arrays.asList(Objects.requireNonNull(courseContent).getData()));
                        CourseAdapter = new AllCourseAdapter(MainActivity.this, list1);
                        video_list_recycler.setAdapter(CourseAdapter);
                        progressBarUtil.hideProgress();
                    } else {
                        android.app.AlertDialog.Builder alertDialogBuilder =
                                new android.app.AlertDialog.Builder(MainActivity.this);
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
                    progressBarUtil.hideProgress();
                    System.out.println("Suree: response code" + response.message());
                    Toast.makeText(getApplicationContext(), "NetWork Issue,Please Check Network Connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CourseContent> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBarUtil.hideProgress();
                System.out.println("Suree: Error " + t.getMessage());
            }
        });
    }

    //for pdf content
    private void callPdfService() {
        progressBarUtil.showProgress();
        apiCallPdf();
        ClientApi apiCAll = ApiClient.getClient().create(ClientApi.class);
        Call<PdfSell> call = apiCAll.fetchPdf("WB_010", UserID, android_id);
        call.enqueue(new Callback<PdfSell>() {
            @Override
            public void onResponse(Call<PdfSell> call, Response<PdfSell> response) {
                apiCalledPdf();
                PdfSell pdfSell = response.body();
                int statusCode = response.code();
                list1 = new ArrayList();
                if (statusCode == 200) {
                    if (response.body().getError() == false) {
                        System.out.println("Suree body: " + response.body());
                        pdfSellArrays = new ArrayList<>(Arrays.asList(Objects.requireNonNull(pdfSell).getData()));
                        pdfSellAdapter = new AllPdfSellAdapter(MainActivity.this, pdfSellArrays);
                        gec_home_recycle_pdf.setAdapter(pdfSellAdapter);
                        progressBarUtil.hideProgress();
                    } else {
                        android.app.AlertDialog.Builder alertDialogBuilder =
                                new android.app.AlertDialog.Builder(MainActivity.this);
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
                    progressBarUtil.hideProgress();
                    System.out.println("Suree: response code" + response.message());
                    Toast.makeText(getApplicationContext(), "NetWork Issue,Please Check Network Connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PdfSell> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBarUtil.hideProgress();
                System.out.println("Suree: Error " + t.getMessage());
            }
        });
    }

    //for test series
    private void callTestApiService() {
        progressBarUtil.showProgress();
        apiCall();
        ClientApi apiClient = OnlineTestApiClient.getClient().create(ClientApi.class);
        Call<SectionDetailsMainModel> call = apiClient.fetchSectionDetails(OnlineTestData.org_code, OnlineTestData.auth_code, UserID);
        call.enqueue(new Callback<SectionDetailsMainModel>() {
            @Override
            public void onResponse(Call<SectionDetailsMainModel> call, Response<SectionDetailsMainModel> response) {
                progressBarUtil.hideProgress();
                apiCalled();
                SectionDetailsMainModel sectionDetailsMainModel = response.body();
                if (sectionDetailsMainModel != null) {
                    if (sectionDetailsMainModel.getMessage().equalsIgnoreCase("TRUE")) {
                        List<SectionDetailsDataModel> sectionDetailsDataModelList = new ArrayList<>(Arrays.asList(sectionDetailsMainModel.getData()));
                        AllPerminumTestAdapter allPerminumTestAdapter = new AllPerminumTestAdapter(MainActivity.this, sectionDetailsDataModelList);
                        recycle_test_series.setAdapter(allPerminumTestAdapter);
                    } else {
                        Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<SectionDetailsMainModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("call fail " + t);
                progressBarUtil.hideProgress();
            }
        });
    }


    private void logout() {

        progressBarUtil.showProgress();
        ClientApi mService = ApiClient.getClient().create(ClientApi.class);
        Call<LogOut> call = mService.refCodeLogout(3, UserMobile, UserPassword, "SCS001", android_id);
        call.enqueue(new Callback<LogOut>() {
            @Override
            public void onResponse(Call<LogOut> call, Response<LogOut> response) {
                int statusCode = response.code();
                if (statusCode == 200 && response.body().getLoginStatus() != false) {
                    if (response.body().getError() == false) {
                        progressBarUtil.hideProgress();
                        SharedPrefManager.getInstance(MainActivity.this).logout();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                                MainActivity.this);
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
                    progressBarUtil.hideProgress();
                    Toast.makeText(MainActivity.this, response.body().getMessageFailure(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<LogOut> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed" + t.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println(t.getLocalizedMessage());
            }
        });
    }

    private void forceLogout() {
        SharedPrefManager.getInstance(this).logout();
        startActivity(new Intent(this, LoginActivity.class));
        Objects.requireNonNull(this).finish();
    }


    // showing the update pop up to user
//    private class GetLastesVersion extends AsyncTask<String,Void,String> {
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                sLastestVersion = Jsoup
//                        .connect("https://play.google.com/store/apps/details?id="+getPackageName())
//                        .timeout(3000)
//                        .get()
//                        .select("div.hAyfc:nth-child(4)>"+"span:nth-child(2)> div:nth-child(1)"+"> span:nth-child(1)")
//                        .first()
//                        .ownText();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return sLastestVersion;
//        }
//        @Override
//        protected void onPostExecute(String s) {
//            sCurrentVersion = BuildConfig.VERSION_NAME;
//            if (sLastestVersion !=null){
//                String ver1[] =sCurrentVersion.split("\\.");//"1.3.1"
//                String ver2[]=sLastestVersion.split("\\.");//"1.3.2"
//                int len1= ver1.length;
//                int len2= ver2.length;
//
//
//                for(int i = 0; i < len1 ; i++)
//                {
//                    if(!ver1[i].equals(ver2[i]))
//                    {
//                        Log.i("log", "onPostExecute: "+ver1[i]+" "+ver2[i]);
//                        version = true;
//                        updateAlertDialog();
//                    }
//                }
//            }
//        }
//    }
//    private void appUpdate(){
//        // Creates instance of the manager.
//        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
//
//// Returns an intent object that you use to check for an update.
//        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//
//// Checks that the platform will allow the specified type of update.
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                    // For a flexible update, use AppUpdateType.FLEXIBLE
//                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//                // Request the update.
//            }
//        });
//    }
//    private void updateAlertDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        //Set title
//        builder.setTitle(getResources().getString(R.string.app_name));
//        builder.setMessage("New Update Available");
//        builder.setCancelable(false);
//        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("market://details?id="+getPackageName())));
//
//                //dismiss dialog
//                dialogInterface.dismiss();
//            }
//        });
//
//        builder.show();
//    }

    private void apiCall() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();
    }

    private void apiCalled() {
        shimmerLayout.setVisibility(View.GONE);
        shimmerLayout.stopShimmerAnimation();
    }

    private void apiCallTest() {
        shimmerLayout1.setVisibility(View.VISIBLE);
        shimmerLayout1.startShimmerAnimation();
    }

    private void apiCalledTest() {
        shimmerLayout1.setVisibility(View.GONE);
        shimmerLayout1.stopShimmerAnimation();
    }

    private void apiCallPdf() {
        shimmerLayout2.setVisibility(View.VISIBLE);
        shimmerLayout2.startShimmerAnimation();
    }

    private void apiCalledPdf() {
        shimmerLayout2.setVisibility(View.GONE);
        shimmerLayout2.stopShimmerAnimation();
    }


}

