package Test.code;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 
 * @author Saikat
 *
 */
public class InputStreamReaderDecoder {

	FileInputStream fileInputStream = null;
	InputStreamReader inputStreamReader = null;
	ArrayList<String> utfList = new ArrayList<>();
	String utfString;
	
	/**
	 * 
	 * @param sourcePath
	 * @param destinationPath
	 */
	public void fileEncoder(String sourcePath, String destinationPath)  {
		try {
			// new input stream reader is created 
			fileInputStream = new FileInputStream(sourcePath + "/" + destinationPath);
			inputStreamReader = new InputStreamReader(fileInputStream);
			//inputStreamReader.
			// the name of the character encoding returned
			utfString = inputStreamReader.getEncoding();
			utfList.add(utfString);
			//System.out.print("Character Encoding: " + utfString +  " ");

		} catch (Exception e) {
			e.printStackTrace();
			// print error
			//System.out.print("The stream is already closed");
		} finally {

			//Closes the stream and releases resources associated
			try {
				if(fileInputStream!=null)
					fileInputStream.close();
				if(inputStreamReader!=null)
					inputStreamReader.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}   
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getUtfList() {
		return utfList;
	}
	
	/**
	 * 
	 * @param utfList
	 */
	public void setUtfList(ArrayList<String> utfList) {
		this.utfList = utfList;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUtfString() {
		return utfString;
	}
	
	/**
	 * 
	 * @param utfString
	 */
	public void setUtfString(String utfString) {
		this.utfString = utfString;
	}

	

}
