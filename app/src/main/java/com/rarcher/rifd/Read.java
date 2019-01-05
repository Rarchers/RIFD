package com.rarcher.rifd;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Read extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    Button root;
    Button ascii;
    TextView d;
    String TAG = "MAIN";
    Boolean asc=false;
    String dataroot="暂无数据";
    String dataascii="暂无数据"; String uifinfo = "";
    String support = "";
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
                d.setText(dataascii);support="";
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.setText(dataroot);support="";
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
        boolean flag = false;
        boolean haveMifareUltralight = false;
        for (String tech : techList) {
            Log.d(TAG, "onNewIntent+++++++++++++++++++++++++++++++++++++++++++++++++++++++ "+tech);
            if (tech.indexOf("MifareClassic") >= 0) {
                haveMifareUltralight = true;
                break;
            }
            if (tech.indexOf("MifareUltralight") >= 0) {
                flag = true;
                break;
            }

        }

        if (!haveMifareUltralight) {
            uifinfo ="当前卡片UID："+uid;
            support=uifinfo+"\n\n";
            support+="不支持MifareClassic，或者读取失败\n";
           // Toast.makeText(this, "不支持MifareClassic，或者读取失败", Toast.LENGTH_LONG).show();
            d.setText(support);
           // return;
        }
        if (!flag) {
            uifinfo ="当前卡片UID："+uid;
            support=uifinfo+"\n\n";
            support+="不支持MifareUltralight，或者读取失败\n";
            //Toast.makeText(this, "不支持MifareUltralight数据格式", Toast.LENGTH_LONG).show();
            d.setText(support);
           // return;
        }

        if (flag){
            uifinfo ="当前卡片UID："+uid;
            support=uifinfo+"\n\n";
            support+="以MifareUltralight格式读取：：\n\n";
            String data = readmifareultralight(tag);
            if (data != null)
                support+=data+"\n\n";
            d.setText(support);
        }

        if (haveMifareUltralight){

            String data = "当前卡片UID："+uid+"\n"+readTag(tag);
            dataroot = data;
            String data2 = "当前卡片UID："+uid+"\n"+readTag2(tag);
            dataascii = data2;
            if (data != null) {
                Log.i(data, "ouput");
                d.setText(data);
            }

        }




    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: pause");
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    private String readmifareultralight(Tag tag) {
        MifareUltralight light = MifareUltralight.get(tag);
        try {
            light.connect();

            String data="";
            for (int i = 4;i<64;i+=4){
                byte[] bytes = light.readPages(i);
                data+="数据块"+i+"::"+ByteArrayToHexString(bytes)+"\n";//,Charset.forName("utf-8")
                data+="数据块"+i+"::"+hexStringToString(ByteArrayToHexString(bytes))+"\n";//,Charset.forName("utf-8")
                data+="\n\n";
            }



            return  data;//
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void readnfca(Tag tagFromIntent){
        NfcA nfca = NfcA.get(tagFromIntent);
        try{
            nfca.connect();
            if (nfca.isConnected()) {//NTAG216的芯片
                 byte[] SELECT = {
                         (byte) 0x30,
                         (byte) 5 & 0x0ff,  //0x05,
                          };
                 byte[] response = nfca.transceive(SELECT);
                 nfca.close();
                 if(response!=null){
                    // Log.d(TAG, "readnfca: 1111111111111111111111111");
                     String x = new String(response, Charset.forName("utf-8"));
                     uifinfo+="\n"+x;
                     d.setText(uifinfo);
                 }
            }
        }
                 catch(Exception e){

                 }

    }

    public String readTagUltralight(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            int size=mifare.PAGE_SIZE;
            byte[] payload = mifare.readPages(0);
            String result="page1："+ByteArrayToHexString(payload)+"\n"+"总容量："+String.valueOf(size)+"\n";
            //这里只读取了其中几个page
            byte[] payload1 = mifare.readPages(4);
            byte[] payload2 = mifare.readPages(8);
            byte[] payload3 = mifare.readPages(12);

            byte[] payload4 = mifare.readPages(16);
             byte[] payload5 = mifare.readPages(20);
             byte[] payload6 = mifare.readPages(24);
            result+="page4:"+ByteArrayToHexString(payload1)+"\npage8:"+ByteArrayToHexString(payload2)+"\npage12："+ByteArrayToHexString(payload3)+"\n"
                    +"\npage16："+ByteArrayToHexString(payload4)+"\n"
                    +"\npage20："+ByteArrayToHexString(payload5)+"\n"
                    +"\npage24："+ByteArrayToHexString(payload6)+"\n"
                    ;
             return result;
            // String a =  new String(payload4, Charset.forName("US-ASCII"));
            // + new String(payload5, Charset.forName("US-ASCII"));
             } catch (IOException e)
             {
             Log.e(TAG, "IOException while writing MifareUltralight message...",
                     e);
             return "读取失败！";
             } catch (Exception ee) {
            Log.e(TAG, "IOException while writing MifareUltralight message...",
                    ee);
            return "读取失败！";
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing tag...", e);
                }
            }
        }}
    /**
     * 16进制转换成为string类型字符串
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "GB2312");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
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
    //废弃！！！！
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
                    typeS = "经典类型";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "plus类型";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "pro类型";
                    break;
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "未知类型";
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
                                +hexStringToString(bytesToHexString(data))  + "\n";
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
        String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A","B", "C", "D", "E", "F" };
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
