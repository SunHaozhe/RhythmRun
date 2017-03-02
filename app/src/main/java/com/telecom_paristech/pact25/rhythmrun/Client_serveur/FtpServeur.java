package com.telecom_paristech.pact25.rhythmrun.Client_serveur;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.telecom_paristech.pact25.rhythmrun.R;
import android.widget.Toast;
import java.io.File;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPFile;
import static com.facebook.internal.Utility.deleteDirectory;

/**
 * Traits the FTP server, for now, just download function has been implemented
 */
public class FtpServeur extends AppCompatActivity {

    Button upload_btn,download_btn;

    //work only for Dedicated IP
    static final String FTP_HOST = "93.188.160.186";

    //FTP USERNAME
    static final String FTP_USER = "u564168340";  //TODO

    //FTP PASSWORD
    static final String FTP_PASS = "123456";  //TODO

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftpserveur);
        try {
            upload_btn= (Button) findViewById(R.id.button_send_ftp);
            upload_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO
                    Toast.makeText(FtpServeur.this,"Sorry, upload function has not yet been implemented.",Toast.LENGTH_LONG).show();

                }
            });
            download_btn = (Button) findViewById(R.id.button_download_ftp);
            download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadStart();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void downloadStart() {
        FTPClient ftp = new FTPClient();
        try {
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy =
                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            ftp.connect(FTP_HOST,21);
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
    }
}