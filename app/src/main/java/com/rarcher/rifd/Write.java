package com.rarcher.rifd;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Write extends AppCompatActivity {
    String TAG = "WRITE";
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        if (mNfcAdapter == null) {
            Toast.makeText(this, "设备不支持NFC！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: "+mNfcAdapter);
        if (mNfcAdapter != null) {mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null,
                null);
            Log.d(TAG, "onResume: running");}
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: pause");
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }
}
