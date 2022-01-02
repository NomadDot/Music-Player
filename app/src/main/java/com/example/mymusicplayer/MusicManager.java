package com.example.mymusicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class MusicManager extends AppCompatActivity {

    ListView listView;
    String[] items;
    Context context;
    LayoutInflater inflater;


    MusicManager(Context ctxt){
        context = ctxt;
        setContentView(R.layout.fragment_main2);
    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for(File singleFile: files) {
            if(singleFile.isDirectory() && !singleFile.isHidden()) {
                arrayList.addAll(findSong(singleFile));
            }
            else {
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
                    arrayList.add(singleFile);
                }
            }

        }
        return arrayList;
    }

    void displaySongs() {
        listView = findViewById(R.id.listView);
        final ArrayList<File> songs = findSong(Environment.getExternalStorageDirectory());
        items = new String[songs.size()];
        for(int i = 0; i<songs.size();i += 1){
            items[i] = songs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }

        MyCustomAdapter myCustomAdapter = new MyCustomAdapter();
        listView.setAdapter(myCustomAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName = (String) listView.getItemAtPosition(position);
                context.startActivity(new Intent(context, PlayerActivity.class)
                        .putExtra("songs", songs)
                        .putExtra("songName", songName)
                        .putExtra("pos", position));
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
            inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View myView = inflater.inflate(R.layout.list_item, null);
            TextView textSong = myView.findViewById(R.id.songName);

            textSong.setSelected(true);
            textSong.setText(items[i]);

            return myView;
        }
    }
}
