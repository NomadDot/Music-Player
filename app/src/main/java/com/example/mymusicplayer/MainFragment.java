package com.example.mymusicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mymusicplayer.PlayerActivity;
import com.example.mymusicplayer.R;

import java.io.File;
import java.util.ArrayList;

public class MainFragment extends Fragment {
    View view;
    ListView listView;
    String[] items;
    Context ctxt;
    LayoutInflater inflater;
    TextView txtDuration;
    static MediaPlayer mediaPlayer;

    ArrayList<File> songs;
    ArrayList<byte[]> arrayOfBytesImage;
    ArrayList<String> listOfDurations = new ArrayList<>();

    MainFragment(ArrayList<String> listOfDurationsOperand, ArrayList<File> songsOperand) {
        songs = songsOperand;
        listOfDurations = listOfDurationsOperand;
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        songs = findSong(Environment.getExternalStorageDirectory());
//        ctxt = getContext();
//        super.onAttach(context);
//        Log.d("ON ATTACH", "WORK ");
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main2, container, false);
        listView = view.findViewById(R.id.listView);
        ctxt = view.getContext();
        displaySongs();
        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
    }


    void displaySongs() {

        //songs = findSong(Environment.getExternalStorageDirectory());
        items = new String[songs.size()];
        for(int i = 0; i<songs.size();i += 1){
            items[i] = songs.get(i).getName().toString().replace(".mp3","");
        }

        MyCustomAdapter myCustomAdapter = new MyCustomAdapter();
        listView.setAdapter(myCustomAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String) listView.getItemAtPosition(position);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(songs.get(position).toString());
                byte[] art = retriever.getEmbeddedPicture();

                ctxt.startActivity(new Intent(ctxt, PlayerActivity.class)
                        .putExtra("songs", songs)
                        .putExtra("songName", songName)
                        .putExtra("pos", position)
                        .putExtra("img", art));
            }
        });

    }

    public class MyCustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
                inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View myView = inflater.inflate(R.layout.list_item, null);
                TextView textAuthor = myView.findViewById(R.id.songAuthorListItem);
                TextView textSong = myView.findViewById(R.id.songNameListItem);
                ImageView imgSong = myView.findViewById(R.id.imgsong);

                String author = getAuthor(items[i]);
                String name = getName(items[i]);
                textSong.setSelected(true);
                textAuthor.setText(author);
                textSong.setText(name);
                txtDuration = myView.findViewById(R.id.txtTimeEndListItem);
                txtDuration.setText(listOfDurations.get(i));

//                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//                retriever.setDataSource(songs.get(i).toString());
//                byte[] art = retriever.getEmbeddedPicture();
//
//                if( art != null){
//                    imgSong.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
//                }

                return myView;
        }

    }
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