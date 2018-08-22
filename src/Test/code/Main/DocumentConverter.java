package Test.code.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.PDF;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.XCloseable;

import ooo.connector.BootstrapConnector;
import ooo.connector.BootstrapSocketConnector;

public class DocumentConverter {


	private XComponentLoader xCompLoader = null;
	private XDesktop xDesktop;
	/** Containing the given type to convert to
	 */
	private String sConvertType = "";
	/** Containing the given extension
	 */
	private String sExtension = "";
	/** Containing the current file or directory
	 */
	private String sIndent = "";
	/** Containing the directory where the converted files are saved
	 */
	private String sOutputDir = "";

	private String targetPath = "";

	private String sOutUrl;

	private File outdir;
	private File testFile;
	private String libOfficePath;
	private String removeBeginningOfPath, pathWithout_PDFA, TASKLIST = "tasklist";
	private static String libreOfficePathWin = "C:/Program Files/LibreOffice/program/";
	private BootstrapSocketConnector bsc;


	File fileDirectory;
	private ArrayList<File> originalListFile = new ArrayList<>();
	ArrayList<File> convertedFiles;
	ArrayList<File> fileList = new ArrayList<>(); 


	public DocumentConverter(String libOfficePath)
	{	
		//this.libOfficePath = libOfficePath;
		bsc = new BootstrapSocketConnector(libOfficePath);
		libreOfficeConnectionMethod();
	}


	public static void main(String[] args)
	{
		DocumentConverter docCon = new DocumentConverter(libreOfficePathWin);
		String path = "H:\\Skrivbord\\msOfficeFiles";
		File file = new File(path);
		int count = 0;
		String libreOfficeAppMac = "LibreOffice.app", libreOfficeAppWin = "soffice.bin";

		/*if(docCon.isProcessRunning(libreOfficeAppMac,libreOfficeAppWin))
		{
			docCon.closeLibreOffice();
		}*/
		
		for(File currentFileOrDir : file.listFiles())
		{
			if(currentFileOrDir.getName().endsWith(".doc") || currentFileOrDir.getName().endsWith(".docx") || 
					currentFileOrDir.getName().endsWith(".xls") || currentFileOrDir.getName().endsWith(".xlsx") ||
					currentFileOrDir.getName().endsWith(".ppt") || currentFileOrDir.getName().endsWith(".pptx")) 
			{
				docCon.traverseAndConvert1(currentFileOrDir);
				System.out.println("Hellö");
			}
		}
		docCon.dumpThreadDump();
		System.out.println("??");
		docCon.closeLibreOffice();
		//docCon.bsc.disconnect();


	}

	public void  dumpThreadDump() {
		ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
		for (ThreadInfo ti : threadMxBean.dumpAllThreads(true, true)) 
		{
			System.out.print(ti.toString());
		}
	}

	public void libreOfficeConnectionMethod() {


		//String libreOfficePath = "/C:/Program Files (x86)/LibreOffice/program/soffice.exe/";
		String libreOfficePathMac = "/Applications/LibreOffice.app/Contents/MacOS/";
		String libreOfficePathWin = "C:/Program Files/LibreOffice/program/soffice.exe";


		XComponentContext xContext = null;

		try {


			// get the remote office component context
			xContext = bsc.connect();
			//xContext = BootstrapSocketConnector.bootstrap(libreOfficePathWin);

			// get the remote office service manager
			XMultiComponentFactory xMCF =
					xContext.getServiceManager();


			Object oDesktop = xMCF.createInstanceWithContext(
					"com.sun.star.frame.Desktop", xContext);

			xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, oDesktop);

			xCompLoader = UnoRuntime.queryInterface(com.sun.star.frame.XComponentLoader.class,
					xDesktop);




			// Getting the given type to convert to
			sConvertType = "writer_pdf_Export";

			// Getting the given extension that should be appended to the
			// origin document
			sExtension = "pdf";

			//traverseAndConvert(targetPath);

		} 

		catch( Exception e ) 
		{
			e.printStackTrace(System.err);
			//System.exit(1);
		}

	}

	public File traverseAndConvert1(File f)
	{
		
		// Converting the document to the favored type
		try 
		{
			
			// Composing the URL by replacing all backslashes
			//String testUrl = "file:///" + f.getParentFile().getAbsolutePath().replace("\\", "/");
			String testUrl = f.toPath().getParent().toUri().toString();
			System.out.println(testUrl);
			//String sUrl = "file:///" + f.getAbsolutePath().replace( '\\', '/' );
			String sUrl = f.toPath().toUri().toString();
			System.out.println(sUrl);



			/*if(f.getName().endsWith(".doc") || f.getName().endsWith(".docx") || 
					f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx") ||
					f.getName().endsWith(".ppt") || f.getName().endsWith(".pptx")) 
			{ 
				System.out.println("Original Files: "+  f.getName());*/


			// Loading the wanted document
			PropertyValue propertyValues[] = new PropertyValue[1];
			propertyValues[0] = new PropertyValue();
			propertyValues[0].Name = "Hidden";
			propertyValues[0].Value = Boolean.TRUE;

			Object oDocToStore = xCompLoader.loadComponentFromURL(
					sUrl, "_blank", 0, propertyValues);

			// Getting an object that will offer a simple way to store
			// a document to a URL.			

			XStorable xStorable =
					UnoRuntime.queryInterface(XStorable.class, oDocToStore );

			// Preparing properties for converting the document
			propertyValues = new PropertyValue[3];

			// Setting the flag for overwriting
			propertyValues[0] = new PropertyValue();
			propertyValues[0].Name = "Overwrite";
			propertyValues[0].Value = Boolean.TRUE;
			// Setting the filter name
			propertyValues[1] = new PropertyValue();
			propertyValues[1].Name = "FilterName";
			propertyValues[1].Value = sConvertType;

			propertyValues[2] = new PropertyValue();
			propertyValues[2].Name = "PDFViewSelection";
			propertyValues[2].Value = 2;

			//Appending the favored extension to the origin document name



			String tmp = FilenameUtils.removeExtension(f.getName());

			String sStoreUrl = "";

			if(f.getName().endsWith(".pdf"))
			{
				sStoreUrl = testUrl + "/" + tmp + "_pdfA"+ "." + sExtension;  
			}
			else
			{
				sStoreUrl = testUrl +  "/" + tmp + "." + sExtension;
			}
			

			System.out.println(sStoreUrl);

			xStorable.storeToURL(sStoreUrl, propertyValues);

			removeBeginningOfPath = sStoreUrl.replace("file:///", "");

			System.out.println(removeBeginningOfPath);

			// Closing the converted document. Use XCloseable.close if the
			// interface is supported, otherwise use XComponent.dispose
			XCloseable xCloseable = UnoRuntime.queryInterface(XCloseable.class, xStorable);

			if ( xCloseable != null ) 
			{
				xCloseable.close(false);

				System.out.println("Closed?");
			} 

			else 
			{
				XComponent xComp = UnoRuntime.queryInterface(XComponent.class, xStorable);

				xComp.dispose();

			}
			
		}

		catch( Exception e ) 
		{
			e.printStackTrace(System.err);
		}

		originalListFile.add(f);

		return new File(removeBeginningOfPath);
	}

	public boolean isProcessRunning(String libreOfficeAppMac, String libreOfficeAppWin) {

		Process p;
		try {
			p = Runtime.getRuntime().exec(TASKLIST);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) 
			{

				// System.out.println(line);
				if (line.contains(libreOfficeAppMac) || line.contains(libreOfficeAppWin)) 
				{
					return true;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	public void closeLibreOffice() {

		Runtime rt = Runtime.getRuntime();

		String libreOfficeAppMac = "LibreOffice.app", libreOfficeAppWin = "soffice.bin";

		String  osName;

		try 

		{
			osName = System.getProperty("os.name");
			//test(p.getInputStream());
			if(osName.contains("Windows"))
			{
				bsc.disconnect();
				rt.exec("taskkill /F /IM " + libreOfficeAppWin);
			}
			else if(osName.contains("Mac") || osName.contains("Ubuntu") || osName.contains("Debian"))
			{
				bsc.disconnect();
				rt.exec("pkill -f " + libreOfficeAppMac);
			}


		} 

		catch (IOException e) 
		{

			e.printStackTrace();

		} 


	}

	public void removeMsOfficeFormatFile(File tempFile) 
	{
		System.out.println("File that is being deleted : "  + tempFile.getAbsolutePath());
		
		boolean isFileDeleted = tempFile.delete();
		
		if(isFileDeleted)
		{
			System.out.println("File is deleted.");
		}
		else
		{
			System.out.println("File is not deleted.");
		}
	}

	public void traverseAndConvert(String targetPath) {

		fileDirectory = new File(targetPath);
		sOutUrl = "file:///" + fileDirectory.getAbsolutePath().replace( '\\', '/' );


		for(File f : fileDirectory.listFiles())
		{

			if(f.isFile())
			{
				//Stores the original files for the html/css/js parser
				originalListFile.add(f);

				// Converting the document to the favoured type
				try 
				{
					// Composing the URL by replacing all backslashes
					String testUrl = "file:///" + f.getParentFile().getAbsolutePath().replace("\\", "/");
					String sUrl = "file:///"
							+ f.getAbsolutePath().replace( '\\', '/' ); 


					if(f.getName().endsWith(".doc") || f.getName().endsWith(".docx") || 
							f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx") ||
							f.getName().endsWith(".ppt") || f.getName().endsWith(".pptx")) 
					{ 
						System.out.println("Original Files: "+  f.getName());
						// Loading the wanted document
						PropertyValue propertyValues[] = new PropertyValue[1];
						propertyValues[0] = new PropertyValue();
						propertyValues[0].Name = "Hidden";
						propertyValues[0].Value = Boolean.TRUE;

						Object oDocToStore = xCompLoader.loadComponentFromURL(
								sUrl, "_blank", 0, propertyValues);

						// Getting an object that will offer a simple way to store
						// a document to a URL.			

						XStorable xStorable =
								UnoRuntime.queryInterface(XStorable.class, oDocToStore );

						// Preparing properties for converting the document
						propertyValues = new PropertyValue[3];
						// Setting the flag for overwriting
						propertyValues[0] = new PropertyValue();
						propertyValues[0].Name = "Overwrite";
						propertyValues[0].Value = Boolean.TRUE;
						// Setting the filter name
						propertyValues[1] = new PropertyValue();
						propertyValues[1].Name = "FilterName";
						propertyValues[1].Value = sConvertType;

						propertyValues[2] = new PropertyValue();
						propertyValues[2].Name = "PDFViewSelection";
						propertyValues[2].Value = 2;

						// Appending the favoured extension to the origin document name

						String tmp = FilenameUtils.removeExtension(f.getName());

						String sStoreUrl = testUrl+ "/" + tmp + "." + sExtension;  
						xStorable.storeToURL(sStoreUrl, propertyValues);

						String removeBeginningOfPath = sStoreUrl.replace("file:///", "");
						testFile = new File(removeBeginningOfPath);

						fileList.add(testFile);
						System.out.println("Converted Files " + testFile.getName());

						//removeFile(fileDirectory);
						// Closing the converted document. Use XCloseable.close if the
						// interface is supported, otherwise use XComponent.dispose
						XCloseable xCloseable =
								UnoRuntime.queryInterface(XCloseable.class, xStorable);


						if ( xCloseable != null ) 
						{
							xCloseable.close(false);
						} 

						else 
						{
							XComponent xComp =
									UnoRuntime.queryInterface(XComponent.class, xStorable);

							xComp.dispose();
						}
					}

					else 
					{
						System.out.println("NOT CONVERTED : " + f.getName());

					} 
				}

				catch( Exception e ) 
				{
					e.printStackTrace(System.err);
				}
			}
			else if (f.isDirectory()) 
			{
				traverseAndConvert(f.getAbsolutePath());
			}
		}


	}

	public  File getOutdir() {
		return outdir;
	}

	public  void setOutdir(File outdir) {
		this.outdir = outdir;
	}

	public String getsOutUrl() {
		return sOutUrl;
	}

	public void setsOutUrl(String sOutUrl) {
		this.sOutUrl = sOutUrl;
	}

	public ArrayList<File> getOriginalListFile() {
		return originalListFile;
	}

	public String getLibOfficePath() {
		return libOfficePath;
	}

	public void setLibOfficePath(String libOfficePath) {
		this.libOfficePath = libOfficePath;
	}

	public String getRemoveBeginningOfPath() {
		return removeBeginningOfPath;
	}

	public String getPathWithout_PDFA() {
		return pathWithout_PDFA;
	}


	public BootstrapSocketConnector getBsc() {
		return bsc;
	}

	public void setBsc(BootstrapSocketConnector bsc) {
		this.bsc = bsc;
	}
	
	


}
