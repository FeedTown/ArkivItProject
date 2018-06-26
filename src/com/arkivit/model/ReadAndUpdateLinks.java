package com.arkivit.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Saikat
 *
 */
public class ReadAndUpdateLinks {

	private String filePath;
	private JsoupParser tJsoup = new JsoupParser();

	public ReadAndUpdateLinks(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<String> readFileAndAddInfoToList()
	{
		List<String> list = new ArrayList<String>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath));) {

			String line = "";

			while((line = br.readLine()) != null)
			{
				list.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
	
	/**
	 * 
	 * @param searchWord
	 * @param updatedWord
	 * @param brList
	 * @param fileExt
	 * @throws IOException
	 */
	public void updateInfoInFile(String searchWord, String updatedWord, List<String> brList, String fileExt) throws IOException
	{
		
		String word = "";
		//String splittedValue ="";
		//String[] arr;
		for(int i = 0; i < brList.size(); i++)
		{
			//System.out.println("Line "+ i+1 + brList.get(i).toString());
			if(brList.get(i).contains(searchWord))
			{
				if(fileExt.equals("css"))
				{
					word = updateCssString(brList.get(i), searchWord, updatedWord);
				}
				else
				{
					word = tJsoup.jSoupExtractElementsFromHtmlFile(brList.get(i), searchWord, updatedWord);
					//htmlWordUpdater.updateWordInString(brList.get(i), searchWord, updatedWord);
				}
				
				brList.set(i, word);
				writeToFile(brList);

			}
		}
	}
	
	/**
	 * 
	 * @param currFile
	 * @throws IOException
	 */
	public void writeToFile(List<String> currFile) throws IOException
	{

		BufferedWriter w = new BufferedWriter(new FileWriter(filePath));

		for(String x : currFile)
		{
			w.write(x);
			w.newLine();
		}

		w.close();

	}
	
	/**
	 * 
	 * @param currentLine
	 * @param searchWord
	 * @param updatedWord
	 * @return
	 */
	public String updateCssString(String currentLine, String searchWord, String updatedWord)
	{
		currentLine = currentLine.replaceAll("\\b"+searchWord+"\\b", updatedWord);
		
		return currentLine;
	}

}

