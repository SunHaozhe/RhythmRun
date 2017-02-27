package com.telecom_paristech.pact25.rhythmrun.Client_serveur;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.net.ftp.FTPClient; 

/**
 * traits the FTP 
 *
 *
 */
public class FtpServeur {
	
	/**
	 * FTP uploads a single file
	 * @throws IOException 
	 */
	public static void upload() throws IOException{
		FTPClient ftpClient = new FTPClient(); 
        FileInputStream fis = null; 
        
        try { 
            ftpClient.connect(""); //TODO enter TP address
            ftpClient.login("", ""); //TODO "enter login", "enter password"

            String fileName = ""; //TODO enter the name of the file
            File srcFile = new File(""); //TODO enter the path of the file
            fis = new FileInputStream(srcFile); 
            
            //Setup of the upload directory
            ftpClient.changeWorkingDirectory(""); //TODO enter the path of the working directory
            ftpClient.setBufferSize(1024); 
            ftpClient.setControlEncoding("GBK"); 
            
            //Sets up file type to binary
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
            ftpClient.storeFile(fileName, fis);
            
        } catch (IOException e) { 
            e.printStackTrace(); 
            throw new RuntimeException("FTP client has an error", e); 
        } finally { 
            fis.close(); 
            try { 
                ftpClient.disconnect(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
                throw new RuntimeException("An exception occured while we disconnect the FTP", e); 
            } 
        } 
	}
	
	/**
	 * FTP downloads a single file
	 * @throws IOException 
	 */
	public static void download() throws IOException{
		FTPClient ftpClient = new FTPClient(); 
        FileOutputStream fos = null; 
        
        try { 
            ftpClient.connect(""); //TODO enter TP address
            ftpClient.login("", ""); //TODO "enter login", "enter password"

            String remoteFileName = ""; //TODO enter the remote file name with the path of directory
            fos = new FileOutputStream(""); //TODO enter the name of the file with its path 
            ftpClient.setBufferSize(1024); 
            
            //Sets up the file type to binary
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
            ftpClient.retrieveFile(remoteFileName, fos); 
            
        } catch (IOException e) { 
            e.printStackTrace(); 
            throw new RuntimeException("FTP client has an error", e); 
        } finally { 
            fos.close(); 
            try { 
                ftpClient.disconnect(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
                throw new RuntimeException("An exception occured when we disconnect the FTP", e); 
            } 
        } 
		
	}

}
