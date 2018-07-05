package Test.code;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.tika.Tika;

public class TestMain {
	
	
	public Tika fileType = new Tika();

	public static void main(String[] args) throws IOException {
	  
		File tmpFile = new File("H:\\Skrivbord\\TestFolder\\bugFixFiles\\TestFilesWithHTML1\\TestFiles\\subfolder\\m.xls");
		
		/*String uri = tmpFile.toPath().getParent().toUri().toString();
		String url = tmpFile.getParentFile().getAbsolutePath();
		
		System.out.println(uri);
		System.out.println(url);
		
		File f = new File(uri);
		
		String test = f.toURI().toURL().getFile().toString();
		
		System.out.println(test);*/
		
		//System.out.println(new TestMain().checkForImageFile(tmpFile));
		
		
		System.out.println(new TestMain().fileType.detect(tmpFile));
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
