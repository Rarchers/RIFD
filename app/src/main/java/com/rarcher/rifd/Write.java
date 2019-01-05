package com.rarcher.rifd;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class Write extends AppCompatActivity {
    String TAG = "WRITE";
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    EditText editText;
    Button start,ge ;
    Boolean ok = false;
    String infos="";
    byte[] nulls=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        editText = findViewById(R.id.writes);
        start = findViewById(R.id.start);
        ge = findViewById(R.id.geshihua);
        mNfcAdapter = mNfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infos = editText.getText().toString();
                ok=true;
            }
        });
        ge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ok = true;
                infos = "                                                                                                                                                       ";
            }
        });


    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(mNfcAdapter.EXTRA_TAG);

        String[] techList = tag.getTechList();
        boolean flag = false;
        for (String tech : techList) {
            if (tech.indexOf("MifareUltralight") >= 0) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            Toast.makeText(this, "不支持MifareUltralight数据格式", Toast.LENGTH_LONG).show();
            return;
        }

        if (ok){
            Log.d(TAG, "onNewIntent: start!!!");   split(tag,infos);}
   

    }

    private void split(Tag tag,String string){
        int len = string.length();
        while(len%4!=0){
            string+=" ";
            len++;
        }
        int blocks=4;
        char str[]=string.toCharArray();
        for(int i=0;i<len;i+=4){
            String s =string.substring(i,i+4);
            Log.d(TAG, "split: "+s);
            writeTag(tag,s,blocks);
            blocks++;
        }




    }



    private void writeTag(Tag tag,String infos,int blocks) {
        MifareUltralight light = MifareUltralight.get(tag);
        Log.d(TAG, "writeTag: "+light);
        try {
            light.connect();
            Log.d(TAG, "writeTag: connect");
            light.writePage(blocks,infos.getBytes(Charset.forName("GB2312")));

            Toast.makeText(this, "成功写入MifareUltralight格式数据!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"写入失败\n"+e,Toast.LENGTH_LONG).show();
        } finally {
            try {
                light.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

}
