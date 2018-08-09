package Test.code.Main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//VERAPDF GREENFIELD IMORTS
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.core.EncryptedPdfException;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.ValidationException;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.results.ValidationResult;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.flavours.PDFAFlavour;

import com.arkivit.model.CharsetDetector;

//import groovy.util.CharsetToolkit;

public class TestCreateReaderFromFile {
	
	private String libreOfficePathWin = "C:/Program Files/LibreOffice/program/soffice.exe", path = "H:\\Skrivbord\\msOfficeFiles";
	
	
	private CharsetDetector checkDecoder = new CharsetDetector();
	
	private DocumentConverter docCon = new DocumentConverter(libreOfficePathWin);
	private List<File> fileList = new ArrayList<File>();
	
	public static void main (String[] args) throws java.io.IOException {

		TestCreateReaderFromFile t = new TestCreateReaderFromFile();
		
		//t.testMethod();
		//t.testMethod1();
		
		t.init();
		System.out.println("Done!");
		
		Runtime.getRuntime().exit(0);
		
		
	}
	
	public void init() throws IOException
	{
		listOfFilesAndDirectory(path);
		closeLibreOffice();
		deleteOfficeFiles();
		
		for(File f : fileList)
		{
			validatePdf1abFile(f);
		}
		
		System.out.println("Done!");
		
	}

	private File fileStatmentChecker(File currentFile) throws IOException 
	{

		currentFile = imgAndMsOfficeFileChecker(currentFile);
		
		return currentFile;
	}
	
	private File imgAndMsOfficeFileChecker(File currentFile) throws IOException
	{
		if(checkForMsOfficeFiles(currentFile))
		{
			currentFile = new File(docCon.traverseAndConvert1(currentFile).getAbsolutePath());
		}
		
		return currentFile;
	}
	
	private boolean checkForMsOfficeFiles(File currentFileOrDir) {

		if(currentFileOrDir.getName().endsWith(".doc") || currentFileOrDir.getName().endsWith(".docx") || 
				currentFileOrDir.getName().endsWith(".xls") || currentFileOrDir.getName().endsWith(".xlsx") ||
				currentFileOrDir.getName().endsWith(".ppt") || currentFileOrDir.getName().endsWith(".pptx")) 
		{
			return true;
		}

		return false;
	}
	
	public void closeLibreOffice() 
	{
		Runtime rt = Runtime.getRuntime();
		String libreOfficeApp = "LibreOffice.app";
		String  osName;

		try 

		{
			osName = System.getProperty("os.name");
			//test(p.getInputStream());
			if(osName.contains("Windows"))
			{
				rt.exec("taskkill /IM soffice.bin");
			}
			else if(osName.contains("Mac") || osName.contains("Ubuntu") || osName.contains("Debian"))
			{
				rt.exec("pkill -f " + libreOfficeApp);
			}
			

		} 

		catch (IOException e) 
		{

			e.printStackTrace();

		} 


	}
	
	public void deleteOfficeFiles() 
	{

		for(File f : docCon.getOriginalListFile()) 
		{
			docCon.removeMsOfficeFormatFile(f);
		}
		docCon.getOriginalListFile().clear();
	}
	
	private void listOfFilesAndDirectory(String inputFolder) throws IOException {
		File folder = new File(inputFolder);

		for(File currentFileOrDir : folder.listFiles())
		{

			if(currentFileOrDir.isFile())
			{
				currentFileOrDir = fileStatmentChecker(currentFileOrDir);

				fileList.add(currentFileOrDir);
				
			}
			else if(currentFileOrDir.isDirectory())	
			{

				System.out.println("Current Dir : "  + currentFileOrDir.getName());

				listOfFilesAndDirectory(currentFileOrDir.getAbsolutePath());
			}
			

		}

	}
		
	private void validatePdf1abFile(File file)
	{
		VeraGreenfieldFoundryProvider.initialise();
		
		try (PDFAParser parser = Foundries.defaultInstance().createParser(file)) {
		    PDFAValidator validator = Foundries.defaultInstance().createValidator(parser.getFlavour(), false);
		    ValidationResult result = validator.validate(parser);
		    if (result.isCompliant()) {
		      // File is a valid PDF/A 1b
		    	System.out.println("This PDF file, "+ file.getName() +" is a valid PDF/A 1b");
		    	
		    } else {
		    	System.out.println("This PDF file, "+ file.getName() +" is not a valid PDF/A 1b");
		    }
		} catch (ModelParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncryptedPdfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	//Old Functions
	private void testMethod1() {
		
		String path;
		
		//Charset test for docx file
		//path = "H:\\Skrivbord\\ArkivItMap\\msOfficeFiles\\Opponerings Frågor.docx";
		
		//Charset test for pptx file
		//path = "H:\\Skrivbord\\ArkivItMap\\msOfficeFiles\\Slutuppgift Java och databas.pptx";
		
		//Charset test for xlsx file
		//path = "H:\\Skrivbord\\ArkivItMap\\msOfficeFiles\\E-halsomyndigheten_webb_metadata (2).xlsx";
		
		//Charset test for html file
		//path =  "H:\\Skrivbord\\ArkivItMap\\msOfficeFiles\\Opponerings Frågor.docx";
		path = "H:\\Skrivbord\\Kladdkaka.html";
		
		File file = new File(path);
		
		/*try {
			/*CharsetToolkit toolKit = new CharsetToolkit(file);
			
			Charset charset = toolKit.getCharset();
			
			System.out.println(charset);*/
			
		/*} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}

	private void testMethod() {
		
		java.io.Reader reader = null;
		/*try {
			java.io.File file = new java.io.File("H:\\Skrivbord\\TestFolder\\bugFixFiles\\TestFilesWithHTML1\\TestFiles\\subfolder\\m.xls");
			reader = ReaderFactory.createBufferedReader(file);
			
			// Do whatever you want with the reader
			
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}*/
		File file = new File("H:\\Skrivbord\\Kladdkaka.html");
		File file1 = new File("H:\\Skrivbord\\ArkivItMap\\msOfficeFiles\\Opponerings Frågor.docx");
		//String encoding = UniversalDetector.detectCharset(file1);
		String[] charsetsToBeTested = {"UTF-8", "windows-1253", "ISO-8859-7"};
		Charset charsetForfile = checkDecoder.detectCharset(file, charsetsToBeTested);
		
		if (charsetForfile != null) {
			System.out.println("Detected encoding = " + charsetForfile);
		} else {
			System.out.println("No encoding detected.");
		}
	}
	
/*	private void validatePdf1abFile()
	{
		String path;
		path = "H:\\Skrivbord\\ArkivItMap\\TestFolder\\bugFixTestFiles\\msOfficeFiles\\Opponerings_Fraagor.pdf";
		File file = new File(path);
		
		ValidationResult result = null;

		try {
			PreflightParser parser = new PreflightParser(file);
			parser.parse();
			
			PreflightDocument doc = parser.getPreflightDocument();
			doc.validate();
			
			result = (ValidationResult) doc.getResult();
			doc.close();
			
			
		} catch (IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(((org.apache.pdfbox.preflight.ValidationResult) result).isValid())
		{
			System.out.println("The file " + file.getName() + " is a valid PDF/A-1b file");
		}
		else
		{
		    System.out.println("The file" + file.getName() + " is not valid, error(s) :");
		    for (ValidationError error : ((org.apache.pdfbox.preflight.ValidationResult) result).getErrorsList())
		    {
		        System.out.println(error.getErrorCode() + " : " + error.getDetails());
		    }
		}
	}
	*/

}