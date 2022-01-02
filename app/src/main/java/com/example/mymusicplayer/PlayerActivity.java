package com.example.mymusicplayer;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    ImageButton btnPlay, btnNext, btnPrev;
    TextView nameOfSong, txtTimerStart, txtTimerEnd, txtAuthor;
    SeekBar seekbar;
    ImageButton btnBackPlayer, btnMoreVert, btnLike;
    String sname;
    Thread updateSeekBar;
    ImageView imgSong;

    byte[] byteImg = null;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;
    int positionOfSong;

    ArrayList<File> mySongs = new ArrayList<>();
    ArrayList<String> listOfSongsName = new ArrayList<>();

    FileOperations fileOperations;

    String songName;

    View.OnClickListener mainListener;
    View.OnClickListener anotherListener;
    boolean isFavorite = false;
    @Override
    protected void onDestroy() {
        mediaPlayer.pause();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_player);

        btnNext = findViewById(R.id.nextButton);
        btnPrev = findViewById(R.id.previousButton);
        btnPlay = findViewById(R.id.playButton);

        btnBackPlayer = findViewById(R.id.backPlayer);
        btnMoreVert =  findViewById(R.id.morePlayer);
        btnLike = findViewById(R.id.likeButton);

        nameOfSong = findViewById(R.id.songName);
        txtTimerStart =findViewById(R.id.txtTimeStart);
        txtTimerEnd = findViewById(R.id.txtTimeEnd);
        txtAuthor = findViewById(R.id.songAuthor);

        imgSong = findViewById(R.id.imageSound);

        seekbar = findViewById(R.id.seekBar);


        fileOperations = new FileOperations(this);

        // ------

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        songName = intent.getStringExtra("songName");
        positionOfSong = bundle.getInt("pos", 0);
        byteImg = bundle.getByteArray("img");
        nameOfSong.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(positionOfSong).toString());
        sname = mySongs.get(positionOfSong).getName().toString().replace("/storage/emulated/0/Android/media/", "");

        String author = getAuthor(sname);
        txtAuthor.setText(author);

        String name = getName(sname).replace(".mp3", "");
        nameOfSong.setText(name);

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while(currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekbar.setProgress(currentPosition);
                    } catch (InterruptedException | IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        seekbar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekbar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.MULTIPLY);
        seekbar.getThumb().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekbar.getProgress());
            }
        });

        String endTime = createTime(mediaPlayer.getDuration());
        txtTimerEnd.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                txtTimerStart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }
                else {
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                positionOfSong = ((positionOfSong + 1)%mySongs.size());
                Uri uri = Uri.parse(mySongs.get(positionOfSong).toString());

                sname = mySongs.get(positionOfSong).toString().replace("/storage/emulated/0/Android/media/", "");

                String author = getAuthor(sname);
                txtAuthor.setText(author);

                String name = getName(sname).replace(".mp3", "");
                nameOfSong.setText(name);
                mediaPlayer =  MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                positionOfSong = ((positionOfSong-1)<0)?(mySongs.size()-1):(positionOfSong-1);
                Uri uri = Uri.parse(mySongs.get(positionOfSong).toString());
                mediaPlayer =  MediaPlayer.create(getApplicationContext(), uri);
                sname = mySongs.get(positionOfSong).toString().replace("/storage/emulated/0/Android/media/", "");

                String author = getAuthor(sname);
                txtAuthor.setText(author);

                String name = getName(sname).replace(".mp3", "");
                nameOfSong.setText(name);

                mediaPlayer.start();
                btnPlay.setBackgroundResource(R.drawable.ic_pause);
            }
        });


        mainListener =  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("likeButton", "works");
                btnLike.setImageResource(R.drawable.ic_baseline_star_24);
                sname = mySongs.get(positionOfSong).toString().replace("/storage/emulated/0/Android/media/", "");
                String songName = getName(sname);
                fileOperations.writeFile(songName);

                btnLike.setImageResource(R.drawable.ic_baseline_star_24);
                btnLike.setOnClickListener(anotherListener);
            }
        };
        btnLike.setOnClickListener(mainListener);

        anotherListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = getName(sname);
                btnLike.setImageResource(R.drawable.ic_baseline_star_border_24);
                fileOperations.deleteTask(name);
                btnLike.setOnClickListener(mainListener);
            }
        };

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                btnNext.performClick();
            }
        });

        btnBackPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("WORKS _____________");
                finish();
            }
        });


        if(isFavorites()) {
            btnLike.setImageResource(R.drawable.ic_baseline_star_24);
            btnLike.setOnClickListener(anotherListener);
        }

    }

    boolean isFavorites() {
         String fileOutput = fileOperations.openText();
        try {
            JSONObject jsonObject = new JSONObject(fileOutput);
            JSONArray jsonArray = jsonObject.getJSONArray("music");

            for(int i = 0; i < jsonArray.length();i++) {
                JSONObject taskData = jsonArray.getJSONObject(i);
                listOfSongsName.add(taskData.getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(sname);
        for(int i = 0; i < listOfSongsName.size(); i += 1) {
            if(sname.endsWith(listOfSongsName.get(i))) {
                Log.i("IsFAVORITE", "WORKS");
                return true;
            }
        }
        return false;
    }

    // convert int time to string
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
    // get song's author
    public String getAuthor(String songName) {
        char[] charString = songName.toCharArray();
        String author = "";
        for(int i = 0; i < songName.length(); i += 1){
            if(charString[i] == '-'){
                break;
            } else {
                author += charString[i];
            }
        }
        return author;
    }
    // get song's name
    public String getName(String songName) {
        char[] charString = songName.toCharArray();
        String nameOfSong = "";
        boolean sign = false;
        for(int i = 0; i < songName.length(); i += 1){
            if(charString[i] == '-'){
               sign = true;
            } else {
                if(sign)  {nameOfSong += charString[i]; }
            }
        }
        return nameOfSong;
    }


}
