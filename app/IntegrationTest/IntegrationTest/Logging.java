package IntegrationTest;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
public class Logging 
{
	
	Date actionDate;
	String s;
	
	public Logging(String s)
	{
		this.actionDate=new Date();
		this.s=s;
		saveLog(actionDate.toString()+" : "+s+"\n");
	}
	
	public void saveLog(String text)
	{
			
			Calendar now = Calendar.getInstance();
			
			try{
				FileWriter File = new FileWriter("data/log_"+now.get(Calendar.DATE)+"-"+now.get(Calendar.MONTH)+".txt",true);
				File.write(text);
				File.close();
			}
			catch(FileNotFoundException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			}
	}
	
}	

	