package com.example.mymusicplayer;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;
// class for writting, deleting and reading data to/from json file
public class FileOperations {
    Context context;

    public FileOperations(Context context) {
        this.context = context;
    }
    private final String FILE_NAME = "musicName.json";

    public void writeFile( String data) {

        File newFile = new File("data/data/com.example.mymusicplayer/files/" + FILE_NAME);
        if(!newFile.exists()) {
            try {
                newFile.createNewFile();
                FileOutputStream fis = context.openFileOutput(FILE_NAME, MODE_PRIVATE);
                fis.write(("{\n" +
                        "  \"music\": [\n" + "   \n" + "  ]\n" +
                        "}").getBytes());
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String openData = openText();
        JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(openData);
            JSONArray tasksArray = jsonResponse.getJSONArray("music");
            JSONObject newEntry = new JSONObject();
            newEntry.put("name", data);
            tasksArray.put(newEntry);
            jsonResponse.put("music", tasksArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String stringFromJson = jsonResponse.toString();

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(FILE_NAME, MODE_PRIVATE);

            fos.write(stringFromJson.getBytes());
        }
        catch(IOException ex) {
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){
            }
        }
    }


    public void deleteTask(String dataToDelete){
        String data = openText();
        JSONObject jsonResponse = null;
        JSONArray arr = null;
        try {
            jsonResponse = new JSONObject(data);
            arr = jsonResponse.getJSONArray("music");
            System.out.println(arr.length());
            for (int i = 0; i<arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.has("name") && obj.get("name").equals(dataToDelete)) {
                    arr.remove(i);
                    break;
                }
            }
            jsonResponse.put("music",arr);

            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput(FILE_NAME, MODE_PRIVATE);
                fos.write(jsonResponse.toString().getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String openText(){
        String text = "";
        FileInputStream fin = null;
        try {
            fin = context.openFileInput(FILE_NAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            text = new String (bytes, "UTF-8");
            fin.close();
        }
        catch(IOException ex) { }
        return text;
    }
}