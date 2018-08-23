package com.arkivit.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.arkivit.model.converters.DocumentConverter;


/**
 * This is used for closing libreOffice, where it checks first if libreOffice process is open or not and takes action.
 * You just need to create this class, where it takes in a DocumentConverter Class on the constructor as parameter,
 *  and call for the init method and the class handles the rest.
 * 
 * @author Saikat
 *
 */

public class CloseLibreOffice {
	
	private String TASKLIST = "tasklist", libreOfficeAppMac = "LibreOffice.app", libreOfficeAppWin = "soffice.bin";
	private DocumentConverter docCon;
	
	public CloseLibreOffice(DocumentConverter docCon) {
		
		this.docCon = docCon;
		
	}
		
	public void init()
	{
		
		if(isProcessRunning(libreOfficeAppMac,libreOfficeAppWin))
		{
			System.out.println("LibreOffice is opened.");
			closeLB(libreOfficeAppMac,libreOfficeAppWin);
			//docCon.getBsc().disconnect();
			init();
		
		}
		else {
			System.out.println("LibreOffice is closed.");
		}
	}
	
	/*public void init2()
	{
		String libreOfficeAppMac = "LibreOffice.app", libreOfficeAppWin = "soffice.bin";
		if(isProcessRunning(libreOfficeAppMac,libreOfficeAppWin))
		{
			System.out.println("LibreOffice is opened.");
			closeLB(libreOfficeAppMac,libreOfficeAppWin);
		}
		else {
			System.out.println("LibreOffice is closed.");
		}
	}*/
	public boolean isProcessRunning(String libreOfficeAppMac, String libreOfficeAppWin) {

		Process p;
		try {
			p = Runtime.getRuntime().exec(TASKLIST);
		
		 BufferedReader reader = new BufferedReader(new InputStreamReader(
		   p.getInputStream()));
		 String line;
		 while ((line = reader.readLine()) != null) {

		  //System.out.println(line);
		  if (line.contains(libreOfficeAppMac) || line.contains(libreOfficeAppWin)) {
		   return true;
		  }
		 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 return false;

		}
	
	public void closeLB(String libreOfficeAppMac, String libreOfficeAppWin) {

		Runtime rt = Runtime.getRuntime();

		//String libreOfficeApp = "LibreOffice.app", libreOfficeAppWin = "soffice.bin";
		String  osName;
		
		try 
		{
			osName = System.getProperty("os.name");
			//test(p.getInputStream());
			if(osName.contains("Windows"))
			{
				rt.exec("taskkill /F /IM " + libreOfficeAppWin);
			}
			else if(osName.contains("Mac") || osName.contains("Ubuntu") || osName.contains("Debian"))
			{
				rt.exec("pkill -f " + libreOfficeAppMac);
			}


		} 

		catch (IOException e) 
		{

			e.printStackTrace();

		} 


	}


}
