package com.rarcher.rifd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import static android.text.Html.FROM_HTML_MODE_COMPACT;

public class About extends AppCompatActivity {

    String info="";

    TextView infor,i1,i2,i3,i4,i5,i6,i7,i8,i9,i10,last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        infor = findViewById(R.id.infor);
        i1 = findViewById(R.id.i1);
        i2 = findViewById(R.id.i2);
        i3 = findViewById(R.id.i3);
        i4 = findViewById(R.id.i4);
        i5 = findViewById(R.id.i5);
        i6 = findViewById(R.id.i6);
        i7 = findViewById(R.id.i7);
        i8 = findViewById(R.id.i8);
        i9 = findViewById(R.id.i9);
        i10 = findViewById(R.id.i10);
        last = findViewById(R.id.last);

        info+="本程序由Rarcher开发\n参考博客（排名不分先后）：\n            ";

        infor.setText(info);
        String info1="<a href=\"https://blog.csdn.net/sky2016_w/article/details/79026596\">android——实现NFC的读写</a>\n                  ";
        String info2="<a href=\"https://blog.csdn.net/wangchi718/article/details/45250377\">Android开发——NFC标签读写</a>\n                 ";
        String info3="<a href=\"https://blog.csdn.net/qq_26787115/article/details/50741068\">Android NFC开发（一）——初探NFC，了解当前前沿技术</a>\n               ";
        String info4="<a href=\"https://blog.csdn.net/CSDN_GYG/article/details/72884849\">Android NFC开发-理论篇</a>\n           ";
        String info5="<a href=\"https://blog.csdn.net/android_xiaozhou/article/details/26727829\">RFID的KeyA/KeyB和区读写控制位</a>\n             ";
        String info6="<a href=\"https://blog.csdn.net/viviwen123/article/details/8665972\">MifareClassic卡自定义keyA和keyB</a>\n                  ";
        String info7="<a href=\"https://blog.csdn.net/viviwen123/article/details/8712992\">MifareClassic卡通过Access Bits来控制keyA和keyB的读写权限</a>\n            ";
        String info8="<a href=\"https://blog.csdn.net/douniwan5788/article/details/7491455\">破解mifare Classic（M1）非接触式射频IC卡--Mifare crack Hack 笔记二</a>\n              ";
        String info9="<a href=\"https://blog.csdn.net/qinlicang/article/details/42713561\">NFC读写MifareClassic协议的NFC卡</a>\n                ";
        String info10="<a href=\"https://blog.csdn.net/coslay/article/details/25075595\">android nfc中MifareClassic格式的读写</a>\n                ";

        String lasts="请勿对本软件进行反编译，爆破等操作，本软件也不会植入任何广告，任何收费。学生精力有限，佛系更新";
        i1.setText(Html.fromHtml(info1,Html.FROM_HTML_MODE_LEGACY));
        i1.setMovementMethod(LinkMovementMethod.getInstance());

        i2.setText(Html.fromHtml(info2,Html.FROM_HTML_MODE_LEGACY));
        i2.setMovementMethod(LinkMovementMethod.getInstance());

        i3.setText(Html.fromHtml(info3,Html.FROM_HTML_MODE_LEGACY));
        i3.setMovementMethod(LinkMovementMethod.getInstance());

        i4.setText(Html.fromHtml(info4,Html.FROM_HTML_MODE_LEGACY));
        i4.setMovementMethod(LinkMovementMethod.getInstance());

        i5.setText(Html.fromHtml(info5,Html.FROM_HTML_MODE_LEGACY));
        i5.setMovementMethod(LinkMovementMethod.getInstance());

        i6.setText(Html.fromHtml(info6,Html.FROM_HTML_MODE_LEGACY));
        i6.setMovementMethod(LinkMovementMethod.getInstance());

        i7.setText(Html.fromHtml(info7,Html.FROM_HTML_MODE_LEGACY));
        i7.setMovementMethod(LinkMovementMethod.getInstance());
        i8.setText(Html.fromHtml(info8,Html.FROM_HTML_MODE_LEGACY));
        i8.setMovementMethod(LinkMovementMethod.getInstance());

        i9.setText(Html.fromHtml(info9,Html.FROM_HTML_MODE_LEGACY));
        i9.setMovementMethod(LinkMovementMethod.getInstance());

        i10.setText(Html.fromHtml(info10,Html.FROM_HTML_MODE_LEGACY));
        i10.setMovementMethod(LinkMovementMethod.getInstance());

        last.setText(lasts);


    }
}
