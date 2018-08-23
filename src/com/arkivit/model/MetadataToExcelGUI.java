package com.arkivit.model;


//Java imports
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


//VERAPDF IMPORTS
import org.apache.batik.transcoder.TranscoderException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.verapdf.core.EncryptedPdfException;
import org.verapdf.core.ModelParsingException;
import org.verapdf.core.ValidationException;
import org.verapdf.pdfa.Foundries;
import org.verapdf.pdfa.PDFAParser;
import org.verapdf.pdfa.PDFAValidator;
import org.verapdf.pdfa.PdfBoxFoundryProvider;
import org.verapdf.pdfa.results.ValidationResult;

//This programs class imports
import com.arkivit.model.converters.DocumentConverter;
import com.arkivit.model.converters.ImageFileConverter;
import com.arkivit.model.excel.ExcelFileCreator;
import com.arkivit.model.file.FileDuration;
import com.arkivit.model.file.FileExtension;
import com.arkivit.model.file.GeneralBean;
import com.arkivit.model.parser.ReadAndUpdateLinks;


/**
 * This class is handling the process of sending data and importing metadata from files
 * to two excel sheets.
 * 
 * @author Roberto Blanco, Saikat Takluder, Kevin Olofosson
 * @since 2018-07-24
 *
 */
public class MetadataToExcelGUI{

	private String excelFileName, folderName = "", confidentialChecked = "", personalDataChecked = "", libOfficePath;  
	private long fileSize;
	private int fileListeLength, counter = 1;
	private String sourceFolderPath, targetexcelFilepath, backupFilePath, fileExtension = "", fileNameWithOutExt = "";
	private ArrayList<String> fileNameList = new ArrayList<String>();
	private ArrayList<String> filePathList = new ArrayList<String>();
	private ArrayList<String> fileDecodeList = new ArrayList<String>(), 
							  validPdfAList = new ArrayList<String>(), 
							  unvalidPdfAList = new ArrayList<String>();
	private ArrayList<Long> sizeList = new ArrayList<Long>();
	private ArrayList<File> fileList = new ArrayList<File>();
	private ArrayList<File> mappedFiles = new ArrayList<File>(), mappedFolder = new ArrayList<File>();
	private ArrayList<String> illegalCharFiles = new ArrayList<String>(), illegarCharFolders = new ArrayList<String>();
	private ArrayList<File> convertedFiles = new ArrayList<File>(), unValidPdfAFiles = new ArrayList<File>();
	private int fileCount = 0;
	private int count = 0;
	private FileDuration  fileDuration = new FileDuration(); 
	private Tika fileType = new Tika();
	private String duration, fPath, currentFileName, tempString, tempPath, newFileString;
	private CharsetDetector checkDecoder = new CharsetDetector();
	private GeneralBean generalBean = new GeneralBean();
	private DocumentConverter docCon;
	private ImageFileConverter img = new ImageFileConverter();
	private FileExtension officeFileEx = new FileExtension();
	private boolean mapping = false;
	private boolean overwrite = false;
	private boolean isLibreOfficeOpen = false;
	private boolean isValid = false;
	private ArrayList<File> renamedPdfAFileList = new ArrayList<File>();
	private ArrayList<File> originalPdfAFileList = new ArrayList<File>();

	/**
	 * No args constructor
	 */
	public MetadataToExcelGUI()
	{

		//sourceFolderPath = "C:\\Users\\Kevin\\Desktop\\test";
		//sourceFolderPath = "F:\\Skola\\Svenska";
		//sourceFolderPath = "/Users/RobertoBlanco/Desktop/TestFiles";
		//init(mapping,overwrite);
	}

	/**
	 * Constructor with argument.
	 * @param excelFileName The name of the excel file
	 * @throws IOException 
	 */
	public MetadataToExcelGUI(String excelFileName)
	{   
		this.excelFileName = excelFileName + ".xlsx";
		//fileList = new ArrayList<File>();
		//testMeth();
	} 
	
	/**
	 * Name of source folder instantiated and
	 * if mapping = true the method copyFolder gets called.
	 * listOfFilesAndDirectory and getAndAddFileDataToList get called.
	 * @param mapp A boolean variable, false by default
	 * @param overW A boolean variable, false by default
	 * @throws IOException
	 */
	public void init(boolean mapp, boolean overW) throws IOException{

		this.mapping = mapp;
		this.overwrite = overW;
		//
		docCon = new DocumentConverter(libOfficePath);
		CloseLibreOffice closeLib = new CloseLibreOffice(docCon);
		
		folderName = new File(sourceFolderPath).getName();

		if(mapping && !overwrite) 
		{
			copyFolder();
		}

		listOfFilesAndDirectory(sourceFolderPath);
		
		deleteOfficeFiles();
		
		for(File f : fileList)
		{
			if(f.getName().endsWith(".pdf"))
			{
				if(!validatePdf1abFile(f))
				{
					f = convertPDFToPDFA(f);
				}
				
			}
		}	
		
		closeLib.init();
		
		getAndAddFileDataToList();
		

	}
	
	
	/**
	 * This class converts normal pdf file to pdf/a file. File pdfFile is a normal pdf. 
	 * 
	 * @param pdfFile is File object
	 * @return
	 */
	public File convertPDFToPDFA(File pdfFile)
	{

		File tmpFile = pdfFile;
		System.out.println(tmpFile.getAbsolutePath());
		
		pdfFile = docCon.traverseAndConvert1(pdfFile);
		
		System.out.println(pdfFile.getAbsolutePath());

		//File pdfAFile = new File(tmpFile.getAbsolutePath());

		docCon.removeMsOfficeFormatFile(tmpFile);
		docCon.getOriginalListFile().clear();

		System.out.println("Renamed file works? " + tmpFile.getAbsolutePath());
		System.out.println("Original file works? " + pdfFile.getAbsolutePath());

		boolean isRenamed = pdfFile.renameTo(tmpFile);

		System.out.println("Temp file :" + tmpFile);

		if(isRenamed)
		{
			System.out.println("Success!");
			//docCon.removeMsOfficeFormatFile(pdfFile);
		}else {
			System.out.println("Fail!");
		}
		
		return tmpFile;

	}
	
	
	private void renamePdfAFileToOriginalName() {
		
		for(File f : fileList)
		{
			if(f.getName().endsWith(".pdf"))
			{
				if(!validatePdf1abFile(f))
				{
					f = docCon.traverseAndConvert1(f);
					
					closeAndDeleteFilesFromLibreOfficeClass();
			
					File pdfAFile = new File(f.getAbsolutePath().replaceAll("_pdfA", ""));
					
					if(f.renameTo(pdfAFile))
					{
						System.out.println("Success!");
					}
					else
					{
						System.out.println("Failed!");
					}
					
					f = pdfAFile;
					
					System.out.println("File renamed? " + f.getName());
				}
			}
		}	
		
	}

	private void closeAndDeleteFilesFromLibreOfficeClass() {
		closeLibreOffice();
		deleteOfficeFiles();	
	}

	/**
	 * 
	 * @param officePath
	 */
	public void deleteOfficeFiles() 
	{

		for(File f : docCon.getOriginalListFile()) 
		{
			docCon.removeMsOfficeFormatFile(f);
		}
		docCon.getOriginalListFile().clear();
	}

	/**
	 * 
	 * @param imagePath
	 */
	public void deleteIllegalImageFiles(String imagePath) 
	{

		ArrayList<File> deletedImageFilesList = new ArrayList<>();

		for(File f : img.getOrignalImageFileList()) {

			if(f.getName().endsWith(".gif") || f.getName().endsWith(".GIF") || 
					f.getName().endsWith(".jpg") || f.getName().endsWith(".JPG") ||
					f.getName().endsWith(".bmp") || f.getName().endsWith(".BMP") || 
					f.getName().endsWith(".wbmp") || f.getName().endsWith("WBMP") ||
					f.getName().endsWith(".ico") || f.getName().endsWith(".ICO") ||
					f.getName().endsWith(".svg") || f.getName().endsWith(".SVG")) {

				deletedImageFilesList.remove(f);
				f.delete();
			}

		} 

	} 


	/**
	 * 
	 */
	//Copying folder to outside of the root folder
	private void copyFolder() {
		File selectedFolder = new File(sourceFolderPath);
		try {

			FileUtils.copyDirectoryToDirectory(selectedFolder, new File(backupFilePath + "/" + folderName + "_backup"));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//Clear ArrayList(s) if they aren't empty
	public void clearArrayList() 
	{
		fileList.clear();
		fileNameList.clear();
		sizeList.clear();
		filePathList.clear();
		fileDuration.getAudioVideoList().clear();
		illegalCharFiles.clear();
		mappedFiles.clear();

	}
	/**
	 * 
	 * @param file
	 */
	private boolean validatePdf1abFile(File file)
	{
		isValid = false;
		
		PdfBoxFoundryProvider.initialise();
		
		try (PDFAParser parser = Foundries.defaultInstance().createParser(file)) {
		    PDFAValidator validator = Foundries.defaultInstance().createValidator(parser.getFlavour(), false);
		    ValidationResult result = validator.validate(parser);
		    if (result.isCompliant()) 
		    {
		    	// File is a valid PDF/A
		    	String validPdfAFile = "This PDF file, "+ file.getName() +" is a valid PDF/A.";	    	
		    	validPdfAList.add(file.getName());
		    	System.out.println(validPdfAFile);
		    	
		    	isValid = true;
		    	
		    } 
		    else 
		    {
		    	//File is a unvalid PDF/A
		    	String unvalidPdfAFile = "This PDF file, "+ file.getName() +" is not a valid PDF/A.";	    	
		    	unvalidPdfAList.add(file.getName());
		    	System.out.println(unvalidPdfAFile);
		    	isValid = false;
		    	
		    }
		} catch (ModelParsingException e) {
			e.printStackTrace();
		} catch (EncryptedPdfException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		
		return isValid;
		
	}

	/**
	 * listOfFilesAndDirectory method goes throw files in a folder and sub-folder and adds those files to a file arraylist. 
	 * @param inputFolder
	 * @throws IOException
	 */
	/* Goes through folder and sub-folders and adding files to an ArrayList.
	 * If mapping = true All files with illegal characters are renamed.
	 * If file is a directory the path will be retrieved.a
	 */
	private void listOfFilesAndDirectory(String inputFolder) throws IOException 
	{
		File folder = new File(inputFolder);

		for(File currentFileOrDir : folder.listFiles())
		{

			if(currentFileOrDir.isFile())
			{
				//tempFile = new File(currentFileOrDir.getAbsolutePath());

				currentFileOrDir = fileStatmentChecker(currentFileOrDir);

				fileList.add(currentFileOrDir);
				System.out.println("Current File : "  + currentFileOrDir.getName());
				System.out.println("Nr " + fileCount + " : " + currentFileOrDir.getName());
				fileCount++;

			}
			else if(currentFileOrDir.isDirectory())	
			{
				//tempFile = new File(currentFileOrDir.getAbsolutePath());

				if(mapping)
				{
					currentFileOrDir = doMapping(currentFileOrDir,true);
				}

				System.out.println("Current Dir : "  + currentFileOrDir.getName());

				listOfFilesAndDirectory(currentFileOrDir.getAbsolutePath());
			}
			count++;

		}

	}

	/**
	 * 
	 * @param currentFileOrDir
	 * @return
	 * @throws IOException
	 * 
	 */
	private File fileStatmentChecker(File currentFile) throws IOException 
	{

		if(mapping)
		{
			currentFile = doMapping(currentFile,false);
		}
	
		currentFile = imgAndMsOfficeFileChecker(currentFile);
		

		return currentFile;
	}
	
	private File imgAndMsOfficeFileChecker(File currentFile) throws IOException
	{
		if(checkForImageFile(currentFile))
		{
			currentFile = new File(img.convertImage1(currentFile).getAbsolutePath());	
		}
		else if(checkForMsOfficeFiles(currentFile))
		{
			currentFile = new File(docCon.traverseAndConvert1(currentFile).getAbsolutePath());
		}
		
		/*else if(currentFile.getName().endsWith(".pdf"))
		{
			validatePdf1abFile(currentFile);
			if(!isValid)
			{
				File _currentFile = new File(docCon.traverseAndConvert1(currentFile).getAbsolutePath());
				currentFile = new File(_currentFile.getAbsolutePath().replace("_pdfA", ""));
				_currentFile.renameTo(currentFile);
			}
		}*/
		
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

	/**
	 * 
	 * @param currFileOrDir
	 * @param isDir
	 * @return
	 * @throws IOException 
	 */
	public File doMapping(File currFileOrDir, boolean isDir) throws IOException {

		File tempFile = null;
		String currFile = "";//replaceIllegalChars(currFileOrDir.getName());


		if(checkIfCurrentFileOrDirContainsIllegalChars(currFileOrDir))
		{
			if(isDir)
			{
				illegarCharFolders.add(currFileOrDir.getName());
			}
			else
			{	
				illegalCharFiles.add(currFileOrDir.getName());
				
				currFileOrDir = imgAndMsOfficeFileChecker(currFileOrDir);
			}
			
			
			currFile = replaceIllegalChars(currFileOrDir.getName());
			
			tempFile = new File(currFileOrDir.getParentFile().getAbsolutePath(), currFile);
			
			checkForFileOrDirAndSeparateWithExt(isDir,tempFile);

			if(tempFile.exists()) 
			{

				tempFile = renameFile(tempFile,isDir,currFileOrDir);
			}

			if(isDir)
			{
				mappedFolder.add(tempFile);
			}
			else
			{	
				mappedFiles.add(tempFile);
			}
		}
		else
		{
			currFileOrDir = imgAndMsOfficeFileChecker(currFileOrDir);
			
			tempFile = currFileOrDir;
		}

		currFileOrDir.renameTo(tempFile);

		return tempFile;

	}

	/**
	 * This method takes a string as parameter and then the string changes inside this method <br> 
	 * 
	 * @param currentString
	 * @return
	 */
	//If String contains illegal characters they will be replaced and returned.
	private String replaceIllegalChars(String currentString) {

		return currentString = StringUtils.replaceEach(currentString, 
				new String[] { "å",  "ä",  "ö",  "ü", "Å",  "Ä",  "Ö", "Ü", " "}, 
				new String[] {"aa", "ae", "oe", "ue","AA", "AE", "OE", "UE", "_"});

		 //currentString;
	}

	/**
	 * 
	 * @param isDir
	 * @param tempFile
	 */
	private void checkForFileOrDirAndSeparateWithExt(boolean isDir, File tempFile) {

		if(!isDir)
		{
			fileExtension = FilenameUtils.getExtension(tempFile.getName());
			fileNameWithOutExt = FilenameUtils.removeExtension(tempFile.getName());
		}
		else
		{
			fileNameWithOutExt = tempFile.getName();
		}
	}
	
	/**
	 * 
	 * @param tempFile
	 * @param isDir
	 * @param currFileOrDir
	 * @return
	 */
	private File renameFile(File tempFile, boolean isDir, File currFileOrDir) {
		if(!isDir)
		{
			tempFile = new File(currFileOrDir.getParentFile().getAbsolutePath(), fileNameWithOutExt + "_" + counter + "." + fileExtension);
		}
		else
		{
			tempFile = new File(currFileOrDir.getParentFile().getAbsolutePath(), fileNameWithOutExt + "_" + counter);
		}

		return tempFile;
	}

	/**
	 * This boolean method checks if a name of file/folder contains illegal character outside of English characters and return true or false depending
	 * on if illegal character contains on name or not.   
	 * 
	 * @param currFileOrDir Is a File object represent current file in the system from selected folder.
	 * @return Returns true if illegal character found. <br> Returns false if illegal character not found.  
	 */
	private boolean checkIfCurrentFileOrDirContainsIllegalChars(File currFileOrDir) {
		if(currFileOrDir.getName().contains("å") || currFileOrDir.getName().contains("ä") || currFileOrDir.getName().contains("ö")
				|| currFileOrDir.getName().contains("ü") || currFileOrDir.getName().contains("Å") || currFileOrDir.getName().contains("Ä") 
				|| currFileOrDir.getName().contains("Ö") || currFileOrDir.getName().contains("Ü"))
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	/*
	 * If fileList is not empty:  
	 * 
	 * 1.It will check for certain extensions and call
	 * getFileDecoder() method.
	 * 
	 * 2. Call checkForAudioVideoDuration() method.
	 * 
	 * 3. If getDecoding = null then fileDecodeList adds an empty String.
	 * Else getDecoding().name will be added to fileDecodeList.
	 * 
	 * 4. Adds columns to ArrayLists
	 * 
	 * 5. Calling createExcelFile() method.
	 */
	private void getAndAddFileDataToList() 
	{
		Charset getDecoding;

		sortFileList();
		String fullPathforCurrentFile = "";

		try {
			if(!fileList.isEmpty())
			{
				for(File file : fileList)
				{

					fullPathforCurrentFile = file.getAbsolutePath();
					getDecoding = null;
					if(file.getName().endsWith(".html") || file.getName().endsWith(".xhtml") || file.getName().endsWith(".xml")
							|| file.getName().endsWith(".css") || file.getName().endsWith(".xsd") || file.getName().endsWith(".dtd") 
							|| file.getName().endsWith(".xsl") || file.getName().endsWith(".txt") || file.getName().endsWith(".js")) 
					{
						getDecoding = getFileDecoder(fullPathforCurrentFile);
					}


					if(mapping)
					{
						changeLinkInFile(file);
					}

					checkForAudioVideoDuration(file);

					fileSize = file.length();
					fPath = file.getParentFile().getAbsolutePath();
					fPath = fPath.replace(sourceFolderPath, folderName);

					if(getDecoding == null)
					{
						fileDecodeList.add("");
					}
					else
					{
						fileDecodeList.add(getDecoding.name());
					}

					fileNameList.add(currentFileName);			
					sizeList.add(fileSize);
					filePathList.add(fPath);
					fileDuration.getAudioVideoList().add(duration);

				}

			}


		} catch (Exception e) {
			e.printStackTrace();
		}

		fileListeLength = fileNameList.size();

		System.out.println("File name list length : " + fileListeLength);

		//System.out.println("Last list check....:" + fileNameList);
		try {

			System.out.println("Creating workbook......");
			ExcelFileCreator createExcelF = new ExcelFileCreator(fileDuration, fileNameList , filePathList,
					fileDecodeList, sizeList, fileList,  generalBean,targetexcelFilepath, excelFileName, confidentialChecked,personalDataChecked);
			createExcelF.createWorkbook();
			//hibSession();
			System.out.println("Workbook created!");

		} catch (IOException e) {
			e.printStackTrace();
		} 

	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void changeLinkInFile(File file) throws IOException {

		if(file.getName().endsWith(".html") || file.getName().endsWith(".css")/* || file.getName().endsWith(".js")*/)
		{

			String fileExt = FilenameUtils.getExtension(file.getName());
			List<String> list = new ArrayList<String>();
			FileExtension ext;
			ReadAndUpdateLinks br = new ReadAndUpdateLinks(file.getAbsolutePath());
			list = br.readFileAndAddInfoToList(); 
			int counter = 0;

			for(File s : mappedFiles) 
			{
				ext = new FileExtension(s.getName());

				if(ext.getHtmlCssFileExtension()) 
				{
					br.updateInfoInFile(illegalCharFiles.get(counter), s.getName(), list, fileExt) ;
				}

				if(ext.getJsImgFileExtension())
				{
					br.updateInfoInFile(illegalCharFiles.get(counter), s.getName(), list, fileExt);

				}

				counter++;

			}
			list.clear();
		}

	}

	/**
	 * 
	 * @param fullPathforCurrentFile
	 * @return
	 */
	//Checking what kind of charset the file has
	private Charset getFileDecoder(String fullPathforCurrentFile) {
		File currentFile = new File(fullPathforCurrentFile);

		String[] charsetsToBeTested = {"UTF-8", "windows-1253", "ISO-8859-7"};
		Charset charsetForfile = checkDecoder.detectCharset(currentFile, charsetsToBeTested);

		return charsetForfile;
	}

	private boolean checkForImageFile(File f)
	{

		/*String currfilePath = f.getParentFile().getAbsolutePath() + "/"+ f.getName();
		String fileType = checkVideoAudioFiles(currfilePath).replaceAll("/.*", "");*/

		//System.out.println("Filetype : " + fileType);

		/*if(fileType.equals("image"))
		{
			return true;
		}*/

		if(f.getName().endsWith(".gif") || f.getName().endsWith(".GIF") || 
				f.getName().endsWith(".jpg") || f.getName().endsWith(".JPG") ||
				f.getName().endsWith(".bmp") || f.getName().endsWith(".BMP") || 
				f.getName().endsWith(".wbmp") || f.getName().endsWith("WBMP") ||
				f.getName().endsWith(".ico") || f.getName().endsWith(".ICO") ||
				f.getName().endsWith(".svg") || f.getName().endsWith(".SVG")) {

			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param currentfile
	 */
	/*Checks the duration of a video or an audio file ONLY if
	 * the file is detected as a "video/" or an "audio/" file.
	 */
	private void checkForAudioVideoDuration(File currentfile) {
		duration = "";
		currentFileName = currentfile.getName();
		tempPath = currentfile.getParentFile().getAbsolutePath() + "/"+ currentFileName;
		tempString = checkVideoAudioFiles(tempPath);
		newFileString = tempString.replaceAll(".*/", "");

		if(tempString.equals("video/"+newFileString) || tempString.equals("audio/"+newFileString))
		{

			fileDuration.getDuration(currentfile.getParentFile().getAbsolutePath()
					+ "/" + currentFileName); 

			duration = fileDuration.getAudioVideoDuration();

		} 

	}

	private void sortFileList() {
		//fileList.sort((o1,o2) -> o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
		fileList.sort(new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				String s1 = o1.getName().toLowerCase();
				String s2 = o2.getName().toLowerCase();
				final int s1Dot = s1.lastIndexOf('.');
				final int s2Dot = s2.lastIndexOf('.');
				// 
				if ((s1Dot == -1) == (s2Dot == -1)) { // both or neither
					s1 = s1.substring(s1Dot + 1);
					s2 = s2.substring(s2Dot + 1);
					return s1.compareTo(s2);
				} else if (s1Dot == -1) { // only s2 has an extension, so s1 goes first
					return -1;
				} else { // only s1 has an extension, so s1 goes second
					return 1;
				}

			}});

	}

	/**
	 * 
	 * @param fileType
	 * @return
	 */
	//Checks what type of file it is and returns the type.
	private String checkVideoAudioFiles(String fileType) {
		return this.fileType.detect(fileType);
	}


	/**
	 * 
	 * @param stringList
	 * @return
	 */
	@SuppressWarnings("unused")
	private int getLargestString(List<String> stringList) {

		int largestString = stringList.get(0).length();
		int index = 0;

		for(int i = 0; i < stringList.size(); i++)
		{
			if(stringList.get(i).length() > largestString)
			{
				largestString = stringList.get(i).length();
				index = i;
			}
		}

		return index;
	}

	/**
	 * 
	 */
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

	public ArrayList<String> getValidPdfAList() {
		return validPdfAList;
	}

	public ArrayList<String> getUnvalidPdfAList() {
		return unvalidPdfAList;
	}

	public String getFolderName() {
		return folderName;
	}

	public int getFileListeLength() {
		return fileListeLength;
	}

	public ArrayList<String> getFileNameList() {
		return fileNameList;
	}

	public String getExcelFileName() {
		return excelFileName;
	}

	public void setExcelFileName(String excelFileName) {
		this.excelFileName = excelFileName;
	}

	public String getTargetexcelFilepath() {
		return targetexcelFilepath;
	}

	public void setTargetexcelFilepath(String targetexcelFilepath) {
		this.targetexcelFilepath = targetexcelFilepath;
	}

	public String getSourceFolderPath() {
		return sourceFolderPath;
	}

	public void setSourceFolderPath(String sourceFolderPath) {
		this.sourceFolderPath = sourceFolderPath;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public String getBackupFilePath() {
		return backupFilePath;
	}

	public void setBackupFilePath(String backupFilePath) {
		this.backupFilePath = backupFilePath;
	}

	public String getConfidentialChecked() {
		return confidentialChecked;
	}

	public void setConfidentialChecked(String confidentialChecked) {
		this.confidentialChecked = confidentialChecked;
	}

	public String getPersonalDataChecked() {
		return personalDataChecked;
	}

	public void setPersonalDataChecked(String personalDataChecked) {
		this.personalDataChecked = personalDataChecked;
	}

	public ArrayList<File> getConvertedFiles(){
		return convertedFiles;
	}

	public void setConvertedFiles(ArrayList<File> convertedFiles) {
		this.convertedFiles = convertedFiles;
	}

	public ArrayList<File> getMappedFiles() {
		return mappedFiles;
	}

	public void setMappedFiles(ArrayList<File> mappedFiles) {
		this.mappedFiles = mappedFiles;
	} 

	public ArrayList<String> getIllegalCharFiles() {
		return illegalCharFiles;
	}

	public void setIllegalCharFiles(ArrayList<String> illegalCharFiles) {
		this.illegalCharFiles = illegalCharFiles;
	}


	public ArrayList<File> getMappedFolder() {
		return mappedFolder;
	}

	public ArrayList<String> getIllegarCharFolders() {
		return illegarCharFolders;
	}

	public GeneralBean getGeneralBean() {
		return generalBean;
	}

	public ArrayList<File> getFileList() {
		return fileList;
	}
	
	public String getLibOfficePath() {
		return libOfficePath;
	}

	public void setLibOfficePath(String libOfficePath) {
		this.libOfficePath = libOfficePath;
	}

	public boolean isLibreOfficeOpen() {
		return isLibreOfficeOpen;
	}

}