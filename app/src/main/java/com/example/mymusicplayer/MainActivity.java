package com.example.mymusicplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    MainFragment fragmentMain;
    OnlineFragment onlineFragment;
    LikesFragment likesFragment;

    BottomNavigationView bottomNavigationView;

    ArrayList<File> songs = new ArrayList<>();
    ArrayList<String> listOfDurations = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songs = findSong(Environment.getExternalStorageDirectory());
        bottomNavigationView = findViewById(R.id.bottomNavigView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        fragmentMain = new MainFragment(listOfDurations, songs);
        onlineFragment = new OnlineFragment();
        likesFragment = new LikesFragment();


        replaceFragment(fragmentMain);

    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        for(File singleFile: files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3") && checkDash(singleFile.getName())) {
                    arrayList.add(singleFile);
                    Uri uri = Uri.parse(singleFile.toString());
                    MediaPlayer mediaPlayer1 = MediaPlayer.create(this, uri);
                    String duration = "";
                    duration = createTime(mediaPlayer1.getDuration());
                    listOfDurations.add(duration);
//  Ділянка коду для опитмиізації пошуку зображень в мп3
//                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                    retriever.setDataSource(singleFile.toString());
//                    byte[] art = retriever.getEmbeddedPicture();
//                    nullByteObject = new byte[0];
//                    nullByteObject[0] = 0;
//                    if (art == null) {
//                        arrayOfBytesImage.add(nullByteObject);
//                    } else {
//                        arrayOfBytesImage.add(art);
//                    }
                }
            }
        }
        return arrayList;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.main_screen:
                    replaceFragment(fragmentMain);
                    break;
                case R.id.favorite_screen:
                    replaceFragment(likesFragment);
                    break;
                case R.id.internet_screen:
                    replaceFragment(onlineFragment);
                    break;
            }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }


    public void runtimePermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public boolean checkDash(String songName) {
        char[] charString = songName.toCharArray();
        String nameOfSong = "";
        for(int i = 0; i < songName.length(); i += 1) {
            if (charString[i] == '-') {
                return true;
            }
        }
        return false;
    }

    public String createTime(int duration) {
        String time = "";
        int minutes = duration/1000/60;
        int seconds = duration/1000%60;

        time += minutes+":";

        if(seconds < 10) {
            time+="0";
        }
        time+=seconds;

        return time;
    }

}

