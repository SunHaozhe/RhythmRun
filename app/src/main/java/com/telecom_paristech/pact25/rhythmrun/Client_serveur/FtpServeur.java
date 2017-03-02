package com.telecom_paristech.pact25.rhythmrun.Client_serveur;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.telecom_paristech.pact25.rhythmrun.R;
import android.view.View.OnClickListener;
import com.telecom_paristech.pact25.rhythmrun.Client_serveur.sauronsoftware.ftp4j.*;


/**
 * traits the FTP
 *
 *
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class FtpServeur extends AppCompatActivity implements OnClickListener {
    /*********
     * work only for Dedicated IP
     ***********/
    static final String FTP_HOST = "XX.XX.XX.XX";

    /*********
     * FTP USERNAME
     ***********/
    static final String FTP_USER = "XXXXXX";

    /*********
     * FTP PASSWORD
     ***********/
    static final String FTP_PASS = "XXXXXX";

    Button upload_btn, download_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftpserveur);
        try {
            upload_btn = (Button) findViewById(R.id.button_send_ftp);
            upload_btn.setOnClickListener(this);
            download_btn = (Button) findViewById(R.id.button_download_ftp);
            download_btn.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View v) {

    }
}