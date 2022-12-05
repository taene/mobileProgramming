package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listViewMP3;
    Button btnPlay, btnStop;
    // 수정 부분
    TextView tvMP3;
    TextView tvTime;
    ProgressBar pbMP3;

    ArrayList<String> mp3List;
    String selectedMP3;

    String mp3Path = Environment.getExternalStorageDirectory().getPath() + "/" ;
    MediaPlayer mPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("간단 MP3 플레이어");
        ActivityCompat.requestPermissions(this, new String[]
                {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE);

        // SDCard의 파일을 읽어서 리스트뷰에 출력
        mp3List = new ArrayList<String>(); // 가변적 문자열

        File[] listFiles = new File(mp3Path).listFiles();
        String fileName, extName;
        for (File file : listFiles) {
            fileName = file.getName();
            Log.d("AAAA",fileName);
            extName = fileName.substring(fileName.length() - 3);
            if (extName.equals((String) "mp3")) // 확장명이 mp3일 때만 추가함.
                mp3List.add(fileName);
        }

        ListView listViewMP3 = (ListView) findViewById(R.id.listViewMP3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, mp3List);
        listViewMP3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewMP3.setAdapter(adapter);
        listViewMP3.setItemChecked(0, true);

        listViewMP3
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        selectedMP3 = mp3List.get(arg2);
                    }
                });

        //selectedMP3 = mp3List.get(0);

        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnStop = (Button) findViewById(R.id.btnStop);
        tvMP3 = (TextView) findViewById(R.id.tvMP3);
        pbMP3 = (ProgressBar) findViewById(R.id.pbMP3);

        // 이부분 수정
        tvTime = (TextView) findViewById(R.id.tvTime);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(mp3Path + selectedMP3);
                    mPlayer.prepare();
                    mPlayer.start();
                    btnPlay.setClickable(false);
                    btnStop.setClickable(true);
                    tvMP3.setText("실행중인 음악 :  " + selectedMP3);

                    new Thread() {
                        SimpleDateFormat timeFormat = new SimpleDateFormat(
                                "mm:ss");

                        public void run() {
                            if (mPlayer == null)
                                return;
                            pbMP3.setMax(mPlayer.getDuration());
                            while (mPlayer.isPlaying()) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pbMP3.setProgress(mPlayer
                                                .getCurrentPosition());
                                        tvTime.setText("진행 시간 : "
                                                + timeFormat.format(mPlayer
                                                .getCurrentPosition()));
                                    }
                                });
                                SystemClock.sleep(200);
                            }
                        }
                    }.start();

                } catch (IOException e) {
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPlayer.stop();
                mPlayer.reset();
                btnPlay.setClickable(true);
                btnStop.setClickable(false);
                tvMP3.setText("실행중인 음악 :  ");

                pbMP3.setProgress(0); // 현재 위치를 지정
                tvTime.setText("진행 시간 : ");
            }
        });

        //처음 실행할 때 중지버튼 비활성화
        btnStop.setClickable(false);

    }
}