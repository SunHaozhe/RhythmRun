package com.telecom_paristech.pact25.rhythmrun.Client_serveur;

import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.telecom_paristech.pact25.rhythmrun.R;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;

//import it.sauronsoftware.ftp4j.FTPClient;
//import it.sauronsoftware.ftp4j.FTPDataTransferListener;
//import it.sauronsoftware.ftp4j.FTPFile;
import static com.facebook.internal.Utility.deleteDirectory;

/**
 * Traits the FTP server, upload and download
 */
public class FtpServeur extends AppCompatActivity {

    Button upload_btn,download_btn;
    static final int FTP_Port = 21;
    static final String nameOfHost = "ftp.sunjing.pe.hu";
    static final String uploadFileDirectory = "public_html";

    //work only for Dedicated IP
    static final String FTP_HOST = "93.188.160.186";

    //FTP USERNAME
    static final String FTP_USER = "u564168340";

    //FTP PASSWORD
    static final String FTP_PASS = "123456";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftpserveur);
        try {
            upload_btn= (Button) findViewById(R.id.button_send_ftp);
            upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadFTP();
                        }
                    }).start();
                }
            });
            download_btn = (Button) findViewById(R.id.button_download_ftp);
            download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            downloadFTP();
                        }
                    }).start();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadFTP(){
        org.apache.commons.net.ftp.FTPClient ftpClient = null;

        try
        {
            ftpClient = new org.apache.commons.net.ftp.FTPClient();
            ftpClient.connect(FTP_HOST);

            if (ftpClient.login(FTP_USER, FTP_PASS))
            {
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                String data = Environment.getExternalStorageDirectory()+File.separator +"201700081700980.jpg"; //TODO chemin vers le fichier à upload
                Log.i("FTP upload",data);
                File file = new File(data);
                if(!file.exists()) Log.e("FTP upload","File not Find");
                FileInputStream in = new FileInputStream(file);
                boolean result = ftpClient.storeFile("201700081700980.jpg", in);  //TODO   /name of the fichier
                in.close();
                if (result) Log.v("FTP upload result", "succeeded");
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void downloadFTP(){
        org.apache.commons.net.ftp.FTPClient ftpClient = null;

        try
        {
            ftpClient = new org.apache.commons.net.ftp.FTPClient();
            ftpClient.connect(FTP_HOST);

            if (ftpClient.login(FTP_USER, FTP_PASS))
            {
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                String data = "XXXX"; //TODO chemin vers le fichier à download
                Log.i("FTP download",data);
                File file = new File(data);
                if(!file.exists()) Log.e("FTP download","File not Find");
                FileOutputStream out = new FileOutputStream(file);
                boolean result = ftpClient.retrieveFile("XXXX", out);  //TODO   name of the fichier
                out.close();
                if (result) Log.v("FTP download result", "succeeded");
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

/*
    private void downloadStart() {
        FTPClient ftp = new FTPClient();
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            ftp.connect(FTP_HOST,FTP_Port);
            ftp.login(FTP_USER, FTP_PASS);

            //If change occurs in particular language then first delete folder from sdcard, also pass which language contains change.
            File dirStructure = new File("PATH"); //TODO
            deleteDirectory(dirStructure);

            //cross check folder deleted or not, if not exist then create same folder at same location,
            //So that entire new folder can be paste inside SWF_Content folder
            if(!dirStructure.isDirectory()){
                dirStructure.mkdirs();
            }

            //fetch file contents from ftp directory
            FTPFile[] arrEnglishContent = ftp.list("PATH"); //TODO ftp file path. Example : FTPFile[] arrEnglishContent = ftp.list("FTPFolder/Sample/Main");

            //as per requirement apply some cases to download single file at time
            String fileName = null;
            File fileDownload = null;

            for (int i = 0; i < arrEnglishContent.length; i++) {
                int indexOFEqual = arrEnglishContent[i].toString().indexOf("=");
                int indexOfSWF = arrEnglishContent[i].toString().indexOf(".txt");
                fileName = arrEnglishContent[i].toString().substring(indexOFEqual+1, indexOfSWF)+".txt";
                //create file inside folder
                fileDownload = new File(dirStructure+"/"+fileName);
                fileDownload.createNewFile();
                System.out.println(fileName);
                startActualFileDownload(ftp+fileName,ftp,fileDownload);
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                ftp.disconnect(true);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void startActualFileDownload(String ftpFileDonwnloadPath,FTPClient ftp, File sdcardFileDownloadPath){
        try {
            ftp.download(ftpFileDonwnloadPath, sdcardFileDownloadPath,
                    new FTPDataTransferListener() {

                        public void transferred(int arg0) {
                            download_btn.setVisibility(View.GONE);
                            //Log.v("log_tag", "This is for tranfer");
                            Toast.makeText(getBaseContext(), " transferred ..."+arg0 , Toast.LENGTH_SHORT).show();
                        }

                        public void started() {
                            // TODO Auto-generated method stub
                            Toast.makeText(getBaseContext(), " Upload Started ...", Toast.LENGTH_SHORT).show();
                            //Log.v("log_tag", "This is for started");
                        }

                        public void failed() {
                            download_btn.setVisibility(View.VISIBLE);
                            Toast.makeText(getBaseContext(), "  failed ...", Toast.LENGTH_SHORT).show();
                            System.out.println(" failed ..." );
                        }

                        public void completed() {
                            download_btn.setVisibility(View.VISIBLE);
                            Toast.makeText(getBaseContext(), " completed ...", Toast.LENGTH_SHORT).show();
                            //Log.v("log_tag", "This is for completed");

                        }public void aborted() {
                            download_btn.setVisibility(View.VISIBLE);
                            Toast.makeText(getBaseContext()," transfer aborted,please try again...", Toast.LENGTH_SHORT).show();
                            //Log.v("log_tag", "This is for aborted");

                        }
                    });
        } catch (Exception e) {
            e.toString();
        }
    }*/
}