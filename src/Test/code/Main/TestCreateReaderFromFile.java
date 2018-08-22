package Test.code.Main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;


//VERAPDF GREENFIELD IMORTS
import org.verapdf.core.EncryptedPdfException;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.ValidationException;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.results.ValidationResult;
import org.xml.sax.SAXException;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.PdfBoxFoundryProvider;
import org.verapdf.pdfa.VeraFoundryProvider;
import org.verapdf.pdfa.VeraGreenfieldFoundryProvider;
import org.verapdf.pdfa.flavours.PDFAFlavour;

import com.arkivit.model.CharsetDetector;

//import groovy.util.CharsetToolkit;

public class TestCreateReaderFromFile {

	private String libreOfficePathWin = "C:/Program Files/LibreOffice/program/", path = "H:\\Skrivbord\\msOfficeFiles";
	private String pdfFilePath = "H:\\Skrivbord\\Min_Aktivitetsrapport_201710-47051.pdf", path2 = "H:\\Skrivbord\\New_folder\\";
	private String libreOfficeAppMac = "LibreOffice.app", libreOfficeAppWin = "soffice.bin";
	private String pathx86 = "C:/Program Files (x86)/LibreOffice/program/", path3 = "F:\\My_map\\New_folder";


	private CharsetDetector checkDecoder = new CharsetDetector();

	private DocumentConverter docCon;

	private List<File> fileList = new ArrayList<File>();

	public static void main (String[] args){

		TestCreateReaderFromFile t = new TestCreateReaderFromFile();

		//t.testMethod();
		//t.testMethod1();

		t.init();
		//t.delete();

	}

	public void delete()
	{
		File file = new File("H:\\Skrivbord\\New_folder\\Min_Aktivitetsrapport_201710-47051.pdf");
		file.delete();
	}

	public void init() 
	{
		
		docCon = new DocumentConverter(pathx86);
		CloseLibreOffice cLO = new CloseLibreOffice(docCon);
		try {
			listOfFilesAndDirectory(path3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		closeLibreOffice();
		deleteOfficeFiles();

		//File newFile = new File(path2);



		for(File f : fileList)
		{
			if(f.getName().endsWith(".pdf"))
			{
				if(!validatePdf1abFile(f))
				{
					convertPDFToPDFA(f,cLO);
				}
			}

			//System.out.println("Filename : " + f.getName());
		}

		System.out.println("Done!");

	}

	public void convertPDFToPDFA(File pdfFile, CloseLibreOffice cLO)
	{

		File tmpFile = pdfFile;
		System.out.println(tmpFile.getAbsolutePath());
		
		pdfFile = docCon.traverseAndConvert1(pdfFile);
		//docCon.getBsc().disconnect();
		cLO.init();
		
		System.out.println(pdfFile.getAbsolutePath());

		File pdfAFile = new File(tmpFile.getAbsolutePath());

		docCon.removeMsOfficeFormatFile(tmpFile);
		docCon.getOriginalListFile().clear();

		System.out.println("Renamed file works? " + pdfAFile.getAbsolutePath());
		System.out.println("Original file works? " + pdfFile.getAbsolutePath());

		boolean isRenamed = pdfFile.renameTo(pdfAFile);

		System.out.println("Temp file :" + tmpFile);

		if(isRenamed)
		{
			System.out.println("Success!");
			//docCon.removeMsOfficeFormatFile(pdfFile);
		}else {
			System.out.println("Fail!");
		}

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

		if(/*currentFileOrDir.getName().endsWith(".doc") || currentFileOrDir.getName().endsWith(".docx") || */
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

	private boolean validatePdf1abFile(File file)
	{
		PdfBoxFoundryProvider.initialise();
		try {
			PDFAParser parser = Foundries.defaultInstance().createParser(file);
			PDFAValidator validator = Foundries.defaultInstance().createValidator(parser.getFlavour(), false);
			ValidationResult result = validator.validate(parser);
			if (result.isCompliant()) {
				// File is a valid PDF/A
				System.out.println("This PDF file, "+ file.getName() +" is a valid PDF/A");
				return true;

			} else {
				System.out.println("This PDF file, "+ file.getName() +" is not a valid PDF/A");
			}
			
			validator.close();
			parser.close();
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

		return false;
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

	/*private void testMethod() {

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
	/*	File file = new File("H:\\Skrivbord\\Kladdkaka.html");
		File file1 = new File("H:\\Skrivbord\\ArkivItMap\\msOfficeFiles\\Opponerings Frågor.docx");
		//String encoding = UniversalDetector.detectCharset(file1);
		String[] charsetsToBeTested = {"UTF-8", "windows-1253", "ISO-8859-7"};
		Charset charsetForfile = checkDecoder.detectCharset(file, charsetsToBeTested);

		if (charsetForfile != null) {
			System.out.println("Detected encoding = " + charsetForfile);
		} else {
			System.out.println("No encoding detected.");
		}
	}*/

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