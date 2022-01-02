package com.example.mymusicplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class LikesFragment extends Fragment {
    View view;
    FileOperations fileOperations;
    ArrayList<String> listOfSongsName = new ArrayList<>();
    Context context;
    ListView listView;
    LayoutInflater inflater;
    String[] items;
    TextView txtDuration;
    ArrayList<String> listOfDurations = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_likes, container, false);
        listView = view.findViewById(R.id.listViewLikes);
        context = view.getContext();
        fileOperations = new FileOperations(context);
        displaySong();
        return view;
    }

    @Override
    public void onStop() {
        listOfSongsName.clear();
        super.onStop();
    }

    void readSongsInJSON() {
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

    }

   public ArrayList<File> findAndDisplaySongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findAndDisplaySongs(singleFile));
            } else {
                for (int i = 0; i < listOfSongsName.size(); i += 1){
                    if (singleFile.getName().endsWith(listOfSongsName.get(i))) {
                        System.out.println(singleFile.getName());
                        Log.e("testins ______________", "WORK");
                        arrayList.add(singleFile);
                        Uri uri = Uri.parse(singleFile.toString());
                        MediaPlayer mediaPlayer1 = MediaPlayer.create(context, uri);
                        String duration = "";
                        duration = createTime(mediaPlayer1.getDuration());
                        listOfDurations.add(duration);

                    }
                }
            }
        }
        return arrayList;
    }

   public void displaySong() {
        readSongsInJSON();
        ArrayList<File> listOfSongs = findAndDisplaySongs(Environment.getExternalStorageDirectory());

       items = new String[listOfSongs.size()];
       for(int i = 0; i< listOfSongs.size();i += 1){
           items[i] = listOfSongs.get(i).getName().toString().replace(".mp3","");
       }

       LikesFragment.MyCustomAdapter myCustomAdapter = new LikesFragment.MyCustomAdapter();
       listView.setAdapter(myCustomAdapter);

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String songName = (String) listView.getItemAtPosition(position);
               MediaMetadataRetriever retriever = new MediaMetadataRetriever();
               retriever.setDataSource(listOfSongs.get(position).toString());
               byte[] art = retriever.getEmbeddedPicture();

               context.startActivity(new Intent(context, PlayerActivity.class)
                       .putExtra("songs", listOfSongs)
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
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(listOfSongsName.get(i).toString());
//            byte[] art = retriever.getEmbeddedPicture();
//
//            if( art != null){
//                imgSong.setImageBitmap(BitmapFactory.decodeByteArray(art, 0, art.length));
//            }

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