package com.telecom_paristech.pact25.rhythmrun.Client_serveur;


import android.os.Bundle;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;

import com.telecom_paristech.pact25.rhythmrun.R;

public class FtpTestActivity extends AppCompatActivity {
    FtpServeur ftpServeur = new FtpServeur();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftp_test);
        Log.d("FtpTestActivity", "created FtpTestActivity");

        //sets up the SEND FTP MESSAGE button
        Button button_send_ftp = (Button)findViewById(R.id.button_send_ftp);
        button_send_ftp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               try{
                   ftpServeur.upload();
                   Toast.makeText(FtpTestActivity.this,"You clicked on UPLOAD",Toast.LENGTH_SHORT).show();
               } catch (IOException e) {
                   e.printStackTrace();
               }

            }
        });

        //sets up the DOWNLOAD FTP button
        Button button_download_ftp = (Button)findViewById(R.id.button_download_ftp);
        button_download_ftp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    ftpServeur.download();
                    Toast.makeText(FtpTestActivity.this,"You clicked on DOWNLOAD",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
