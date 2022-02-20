package com.ex.group.folder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nprotect.keycryptm.IxAbstractKeypadView;
import com.nprotect.keycryptm.IxKeypadNumView;
import com.nprotect.keycryptm.IxKeypadQwertyView;

import java.security.SecureRandom;
/*

public class keypadCustomInputActivity extends Activity implements b {
    private a a;

    private byte[] b = new byte[0];

    private byte[] c = new byte[0];

    private Resources d;

    private String e;

    private IxAbstractKeypadView f;

    private RelativeLayout g;

    private EditText h;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        requestWindowFeature(1);
        getApplicationContext();
        this.d = getResources();
        this.e = getPackageName();
        int i;
        if ((i = this.d.getIdentifier("zix_custom_input", "layout", this.e)) != 0) {
            setContentView(i);
            Intent intent = getIntent();
            TextView textView;
            String str3;
            if ((textView = (TextView)findViewById(this.d.getIdentifier("zix_title", "id", this.e))) != null && (str3 = intent.getStringExtra("o_text_of_title")) != null)
                textView.setText(str3);
            this.h = (EditText)findViewById(this.d.getIdentifier("zix_edit", "id", this.e));
            this.h.setInputType(0);
             intent.getBooleanExtra("o_secure_of_disp", true);
            String str2;
            if (this.h != null && (str2 = intent.getStringExtra("o_text_of_hint")) != null)
                this.h.setHint(str2);
            Button button2;
            if ((button2 = (Button)findViewById(this.d.getIdentifier("zix_done", "id", this.e))) != null)
                button2.setOnClickListener(new h(this));
            Button button1;
            if ((button1 = (Button)findViewById(this.d.getIdentifier("zix_cancel", "id", this.e))) != null)
                button1.setOnClickListener(new i(this));
            this.g = (RelativeLayout)findViewById(this.d.getIdentifier("zix_keypad", "id", this.e));
            if (intent.getIntExtra("o_type_of_input", 0) == 2) {
                this.f = new IxKeypadNumView((Context)this, intent);
            } else {
                this.f = new IxKeypadQwertyView((Context)this, intent);
            }
            int j;
            if ((j = intent.getIntExtra("o_length_of_input", 0)) != 0)
                this.f.setMaxInputLength(j);
            this.a = a.a("SEED");
            String str1;
            byte[] arrayOfByte;
            if ((arrayOfByte = (byte[])(((str1 = intent.getStringExtra("o_key_of_crypto")) == null) ? null : a.a(str1))) != null) {
                this.c = arrayOfByte;
            } else {
                SecureRandom secureRandom = new SecureRandom();
                byte[] arrayOfByte1 = new byte[16];
                secureRandom.nextBytes(arrayOfByte1);
                this.c = arrayOfByte1;
            }
            this.f.a(this);
            this.f.setTargetEditText(this.h);
            this.g.addView((View)this.f);
        }
    }

    public final void a(byte[] paramArrayOfbyte) {
        this.b = new byte[paramArrayOfbyte.length];
        System.arraycopy(paramArrayOfbyte, 0, this.b, 0, paramArrayOfbyte.length);
        byte[] arrayOfByte1 = new byte[paramArrayOfbyte.length / 16];
        byte[] arrayOfByte2 = new byte[16];
        for (byte b1 = 0; b1 < paramArrayOfbyte.length / 16; b1++) {
            System.arraycopy(paramArrayOfbyte, b1 << 4, arrayOfByte2, 0, 16);
            byte[] arrayOfByte = this.a.b(arrayOfByte2, this.c);
            arrayOfByte1[b1] = arrayOfByte[0];
        }
    }

    public final byte[] a(String paramString) {
        return (this.a == null) ? null : this.a.a(paramString.getBytes(), this.c);
    }

    public static String getResult(EditText paramEditText, Intent paramIntent) {
        String str2 = paramIntent.getStringExtra("ins_data");
        String str1 = paramIntent.getStringExtra("ins_key");
        a a1 = a.a("SEED");
        byte[] arrayOfByte2 = a.a(str2);
        byte[] arrayOfByte1 = a.a(str1);
        int i;
        byte[] arrayOfByte3 = new byte[(i = arrayOfByte2.length / 16) << 4];
        byte[] arrayOfByte4 = new byte[16];
        int j = 0;
        for (byte b2 = 0; b2 < i; b2++) {
            System.arraycopy(arrayOfByte2, b2 << 4, arrayOfByte4, 0, 16);
            byte[] arrayOfByte;
            System.arraycopy(arrayOfByte = a1.b(arrayOfByte4, arrayOfByte1), 0, arrayOfByte3, j, arrayOfByte.length);
            j += arrayOfByte.length;
        }
        byte[] arrayOfByte5 = new byte[j];
        System.arraycopy(arrayOfByte3, 0, arrayOfByte5, 0, j);
        String str3 = "";
        for (byte b1 = 0; b1 < i; b1++)
            str3 = String.valueOf(str3) + "*";
        paramEditText.setText(str3);
        return new String(arrayOfByte5);
    }

    public static void disableSecureDisp(Intent paramIntent) {
        paramIntent.putExtra("o_secure_of_disp", false);
    }

    public static void setLengthOfInput(Intent paramIntent, int paramInt) {
        paramIntent.putExtra("o_length_of_input", paramInt);
    }

    public static void setTypeOfInput(Intent paramIntent, int paramInt) {
        paramIntent.putExtra("o_type_of_input", paramInt);
    }

    public static void setTypeInputQwerty(Intent paramIntent) {
        paramIntent.putExtra("o_type_of_input", 1);
    }

    public static void setTypeInputNumber(Intent paramIntent) {
        paramIntent.putExtra("o_type_of_input", 2);
    }

    public static void setTypeOfKeypad(Intent paramIntent, int paramInt) {
        paramIntent.putExtra("o_type_of_keypad", paramInt);
    }

    public static void setTextOfTitle(Intent paramIntent, String paramString) {
        paramIntent.putExtra("o_text_of_title", paramString);
    }

    public static void setTextOfHint(Intent paramIntent, String paramString) {
        paramIntent.putExtra("o_text_of_hint", paramString);
    }
}

*/
