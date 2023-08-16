package com.example.texttomusic;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;


public class PYopner extends AppCompatActivity {
    TextView textView  , tx2;
    Button convert;
    MediaPlayer mediaPlayer;
    ImageView imageView;
    ArrayList<File> texts = new ArrayList<File>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyopner);
        textView =  findViewById(R.id.textView);
        tx2 = findViewById(R.id.textView2);
        convert = findViewById(R.id.convert);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        texts = (ArrayList)bundle.getParcelableArrayList("textFile");
        int position = intent.getIntExtra("positionForFile" , 0);
        textView.setText(texts.get(position).getName());
        String outFileName = texts.get(position).getName().replace(".txt" , ".mp3");
        File internalStorageDir = PYopner.this.getFilesDir();
        File file = new File(internalStorageDir, outFileName);
        String filePath = file.getAbsolutePath();
        SharedPreferences sP = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sP.edit();
        ed.putString("path", internalStorageDir.getPath());
        ed.commit();
        File f = texts.get(position);
        String data = "";
        try {
            File Obj = new File(f.toURI());
            Scanner Reader = new Scanner(Obj);
            while (Reader.hasNextLine()) {
                data = data.concat(Reader.nextLine());
            }
            Reader.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            tx2.setText("The Entered file is Empty.");
        }
if(!isOnline()){
    Toast.makeText(this, "Please connect to network ", Toast.LENGTH_SHORT).show();
}
imageView = findViewById(R.id.imageView);
        imageView.setVisibility(View.INVISIBLE);
        String finalData = data;
        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalData==null) {
                    tx2.setText("The Entered file is Empty.");
                }
                else{
                    RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setInterpolator(new LinearInterpolator());
                    imageView.setVisibility(View.VISIBLE);
                    rotate.setDuration(1000);
                    imageView.startAnimation(rotate);
                    rotate.setRepeatCount(Animation.INFINITE);
                    if(!Python.isStarted()) {
                        Python.start(new AndroidPlatform(PYopner.this));
                    }
                    Python py = Python.getInstance();
                    PyObject mod = py.getModule("p1");
                    mod.callAttr("convert_text_to_speech", finalData, filePath);

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    imageView.clearAnimation();
                                    imageView.setVisibility(View.INVISIBLE);
                                    Uri uri = Uri.parse(file.toString());
                                    mediaPlayer = MediaPlayer.create(PYopner.this , uri);
                                    mediaPlayer.start();
                                }
                            }, 2000);
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(PYopner.this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}