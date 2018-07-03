package Test.code;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.tika.Tika;

public class TestMain {
	
	
	private Tika fileType = new Tika();

	public static void main(String[] args) throws IOException {
	    /*FileOutputStream inMemoryOut = new FileOutputStream(new File("inMemoryWorkbook.xlsx"));
	    XSSFWorkbook workbook = new XSSFWorkbook();
	    WorkbookExample example = new WorkbookExample(workbook, inMemoryOut);
	    example.export();

	    FileOutputStream streamOut = new FileOutputStream(new File("streamWorkbook.xlsx"));
	    SXSSFWorkbook streamWorkbook = new SXSSFWorkbook();
	    WorkbookExample streamExample = new WorkbookExample(streamWorkbook, streamOut);
	    streamExample.export();*/
		
		//MetadataToExcelGUI data = new MetadataToExcelGUI();
		//MappingLog mapping = new MappingLog(data);
		/*boolean doMapping = false;
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Do you want to mapp everthing in selected folder?\n Yes(y)/No(n)? ");
		String answer = scan.nextLine();
		
		if(answer.equals("y") || answer.equals("Y")) {
			
			doMapping = true;
		}
		else if(answer.equals("n") || answer.equals("N"))
		{
			doMapping = false;
		}
			
		
		//new testMapping(doMapping).init();
		
		scan.close();*/
		
		File tmpFile = new File("H:\\Skrivbord\\TestFolder\\bugFixFiles\\TestFilesWithHTML1\\TestFiles\\subfolder\\m.xls");
		
		String uri = tmpFile.toPath().getParent().toUri().toString();
		String url = tmpFile.getParentFile().getAbsolutePath();
		
		
		
		System.out.println(uri);
		System.out.println(url);
		
		File f = new File(uri);
		
		String test = f.toURI().toURL().getFile().toString();
		
		System.out.println(test);
		
		//System.out.println(new TestMain().checkForImageFile(tmpFile));
	}
	
	
	private String checkForImageFile(File currentfile)
	{
		
		String currfilePath = currentfile.getParentFile().getAbsolutePath() + "/"+ currentfile.getName();
		String fileType = checkVideoAudioFiles(currfilePath).replaceAll("/.*", "");
		
		return fileType;
	}
	
	private String checkVideoAudioFiles(String fileType) {
		return this.fileType.detect(fileType);
	}
}
