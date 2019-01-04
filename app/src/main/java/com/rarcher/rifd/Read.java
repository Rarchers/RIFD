package com.rarcher.rifd;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Read extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    Button root;
    Button ascii;
    TextView d;
    String TAG = "MAIN";
    Boolean asc=false;
    String dataroot;
    String dataascii;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        ascii = findViewById(R.id.ascii);
        root = findViewById(R.id.root);
        mNfcAdapter = mNfcAdapter.getDefaultAdapter(this);
        d = findViewById(R.id.d);
        Log.d(TAG, "onCreate: create");
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

        ascii.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.setText(dataascii);
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.setText(dataroot);
            }
        });
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
    public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: newintent");
        d.setText("读取中....");
        Tag tag = intent.getParcelableExtra(mNfcAdapter.EXTRA_TAG);
        byte[] uidBytes = tag.getId();
        String uid = bytesToHexString(uidBytes);

        String[] techList = tag.getTechList();
        boolean haveMifareUltralight = false;
        for (String tech : techList) {
            if (tech.indexOf("MifareClassic") >= 0) {
                haveMifareUltralight = true;
                break;
            }
        }
        if (!haveMifareUltralight) {
            Toast.makeText(this, "不支持MifareClassic，或者读取失败", Toast.LENGTH_LONG).show();
            d.setText("读取失败，可尝试将卡片移开后重新读取");
            return;
        }
        String data = "当前卡片UID："+uid+"\n"+readTag(tag);
        dataroot = data;
        String data2 = "当前卡片UID："+uid+"\n"+readTag2(tag);
        dataascii = data2;
        if (data != null) {
            Log.i(data, "ouput");
            d.setText(data);

        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: pause");
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    //字符序列转换为16进制字符串
    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }
    public static String hex2Str(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 2; i < hex.length() - 1; i += 2) {
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal

            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }
    public String readTag(Tag tag) {
        Log.d(TAG, "readTag: reading");
        MifareClassic mfc = MifareClassic.get(tag);
        for (String tech : tag.getTechList()) {
            System.out.println(tech);
        }

        boolean auth = false;        //读取TAG
        try {            String metaInfo = "";
            // Enable I/O operations to the tag from this TagTechnology object.
            mfc.connect();
            int type = mfc.getType();//获取TAG的类型
            int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaInfo+="使用默认密钥(当前):     \n"+MifareClassic.KEY_DEFAULT+"\n\n";
            metaInfo+="\n\ntest::default\n\n";
            metaInfo+=MifareClassic.KEY_DEFAULT+"\n";
            metaInfo+="MIFARE Application Directory (MAD) specification密钥:    \n"+MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY+"\n\n";
            metaInfo+="NDEF on MIFARE Classic specification密钥:     \n"+MifareClassic.KEY_NFC_FORUM+"\n\n";

            metaInfo += "卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
                    + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize()
                    + "B\n";            for (int j = 0; j < sectorCount; j++) {
                //Authenticate a sector with key A.
                auth = mfc.authenticateSectorWithKeyA(j,MifareClassic.KEY_DEFAULT);
                 /*auth = mfc.authenticateSectorWithKeyA(j,
                         key2A);*/
                int bCount;
                int bIndex;
                if (auth) {
                    metaInfo += "Sector " + j + ":验证成功\n";
                    // 读取扇区中的块
                    bCount = mfc.getBlockCountInSector(j);
                    bIndex = mfc.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mfc.readBlock(bIndex);
                        metaInfo += "Block " + bIndex + " : "
                                + bytesToHexString(data) + "\n";
                        bIndex++;
                    }
                } else {
                    metaInfo += "Sector " + j + ":验证失败\n";
                }
            }
            return metaInfo;
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finally {
            if (mfc != null) {
                try {
                    mfc.close();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
        return null;
    }
    public String readTag2(Tag tag) {
        Log.d(TAG, "readTag: reading");
        MifareClassic mfc = MifareClassic.get(tag);
        for (String tech : tag.getTechList()) {
            System.out.println(tech);
        }

        boolean auth = false;        //读取TAG
        try {            String metaInfo = "";
            // Enable I/O operations to the tag from this TagTechnology object.
            mfc.connect();
            int type = mfc.getType();//获取TAG的类型
            int sectorCount = mfc.getSectorCount();//获取TAG中包含的扇区数
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
            }
            metaInfo+="使用默认密钥(当前):     \n"+MifareClassic.KEY_DEFAULT+"\n\n";
            metaInfo+="\n\ntest::default\n\n";
            metaInfo+=MifareClassic.KEY_DEFAULT+"\n";
            metaInfo+="MIFARE Application Directory (MAD) specification密钥:    \n"+MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY+"\n\n";
            metaInfo+="NDEF on MIFARE Classic specification密钥:     \n"+MifareClassic.KEY_NFC_FORUM+"\n\n";

            metaInfo += "卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
                    + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize()
                    + "B\n";            for (int j = 0; j < sectorCount; j++) {
                //Authenticate a sector with key A.
                auth = mfc.authenticateSectorWithKeyA(j,MifareClassic.KEY_DEFAULT);
                 /*auth = mfc.authenticateSectorWithKeyA(j,
                         key2A);*/
                int bCount;
                int bIndex;
                if (auth) {
                    metaInfo += "Sector " + j + ":验证成功\n";
                    // 读取扇区中的块
                    bCount = mfc.getBlockCountInSector(j);
                    bIndex = mfc.sectorToBlock(j);
                    for (int i = 0; i < bCount; i++) {
                        byte[] data = mfc.readBlock(bIndex);
                        metaInfo += "Block " + bIndex + " : "
                                +hex2Str(bytesToHexString(data))  + "\n";
                        bIndex++;
                    }
                } else {
                    metaInfo += "Sector " + j + ":验证失败\n";
                }
            }
            return metaInfo;
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finally {
            if (mfc != null) {
                try {
                    mfc.close();
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        }
        return null;
    }
    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",	        "B", "C", "D", "E", "F" };
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }






}
