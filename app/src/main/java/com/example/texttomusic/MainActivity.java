package com.example.texttomusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import android.Manifest;


public class MainActivity extends AppCompatActivity {
    ListView listView;
    Button bt3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                specialPermission();
            }

        } else {
            permission();
        }
        ArrayList<File> text = fetchTextFile(Environment.getExternalStorageDirectory());
        String[] items = new String[text.size()];
        for (int i = 0; i < text.size(); i++) {
            items[i] = text.get(i).getName().replace(".mp3", "");
        }
        ArrayAdapter<String> ad = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(ad);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this , PYopner.class);
                intent.putExtra("textFile" , text);
                intent.putExtra("positionForFile" , i);
                startActivity(intent);
            }
        });
    }

    public ArrayList<File> fetchTextFile(File file) {
        ArrayList arrayList = new ArrayList();
        File[] text = file.listFiles();
        if (text != null) {
            for (File myFile : text) {
                if (!myFile.isHidden() && myFile.isDirectory()) {
                    arrayList.addAll(fetchTextFile(myFile));
                } else {
                    if (myFile.getName().endsWith(".txt") && !myFile.getName().startsWith(".")) {
                        arrayList.add  (myFile);
                    }
                }
            }
        }
        return arrayList;
    }

    public void specialPermission() {


        try {

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } catch (Exception e) {

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
            }
        }
    }

    public void permission() {
        Dexter.withContext(MainActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        permission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }
}
