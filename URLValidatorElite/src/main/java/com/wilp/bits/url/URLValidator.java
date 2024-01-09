package com.wilp.bits.url;

import java.io.File;
import java.io.FileInputStream
;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wilp.bits.aws.utility.StorageBucketUtility;
import com.wilp.bits.config.utility.ReadWriteProps;
//import com.wilp.bits.aws.AWSUtilites;
import com.wilp.bits.email.EmailManagement;
	public class URLValidator{
	String methodsName="";
	  
    public static void main( String[] args ) throws IOException
    {
      URLValidator url = new URLValidator();  
      //EC2Utilities util= new EC2Utilities();
      //Read and Write Excel
      ArrayList<String> columnvalues =url.readXlFile();
      url.writeXlSheet(columnvalues);

    }        
      
    private String createXlSheet()
    {
    	File tempFile = null;
    	try
    	{
    		tempFile =File.createTempFile("Mah_20231206_1", ".xlsx");
    		FileWriter writer= new FileWriter(tempFile);
    		System.out.println("File created at: " + tempFile.getAbsolutePath());
    	
    	}catch(FileNotFoundException e){
    		e.printStackTrace();
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return tempFile.getAbsolutePath();
    }
    
    //Create Empty XL file 
//    private String createXLSheet()
//    {
//    	methodsName="URLValidator.createXLSheet()";
//    	String fileName="Mah_20231206_1.xlsx";
//    	FileOutputStream fileoutput = null;
//    	try {
//    		fileoutput= new FileOutputStream(fileName);
//    		System.out.println("File Created");
//			
//			
//		} catch (FileNotFoundException e) {
//			System.out.println("Exception occured in "+methodsName+ " : "+e);
//		}finally
//    	{
//			try {
//				fileoutput.close();
//			} catch (IOException e) {
//				System.out.println("Exception occured in "+methodsName+ " : "+e);
//			}
//    	}
//    	return fileName;
//    }
//    
    
    
    //Read Request XL File
    private ArrayList<String> readXlFile() throws IOException
    {
    	methodsName="URLValidator.readXLFile()";
    	String columnValues="";
    	ArrayList<String> columnval = null;
    //	String inputfile="C:/Users/DELL/Documents/MAH/Mah.xlsx";
    	StorageBucketUtility amazonutils= new StorageBucketUtility();
    	  
    	String inputfile= amazonutils.getFiles("bits-wilp-ap-south-1","");
    	try
    	{
    	FileInputStream fis= new FileInputStream(inputfile);
    	XSSFWorkbook wb= new XSSFWorkbook(fis);
    	XSSFSheet sheetvalue= wb.getSheet("Base");
    	Iterator<Row> fileitr= sheetvalue.iterator();
    	columnval= new ArrayList<String>();
    	//iterating over excel file
    	while(fileitr.hasNext())
    	{
    		Row row= fileitr.next(); 
    		//Skipping the first row as it is header
    		if(row.getRowNum()==0)
    		{
    			continue;
    		}
    		//Iterating over each column
    		Iterator<Cell> columnitr = row.cellIterator();
    		while(columnitr.hasNext())
    		{
    			Cell cell = columnitr.next();
    			 columnValues= cell.getStringCellValue();
    			//System.out.println(columnValues +"\t\t\t");
    			columnval.add(columnValues);    			
    		}
    	}

    	}catch(FileNotFoundException e)
    	{
    		System.out.println("Exception occured in "+methodsName+ " : "+e);
    	} catch (IOException e) {
    		System.out.println("Exception occured in "+methodsName+ " : "+e);
		}
    	return columnval;
    }
    
   //Write to XL file
    private void writeXlSheet(ArrayList<String> columnbasevalues)
    {
    	methodsName="URLValidator.writeXLSheet()";
    	String createdfile="";
    	FileInputStream input=null;
    	FileOutputStream output= null;
    	XSSFWorkbook workbook;
    	XSSFSheet sheet;
    	Row header;
    	Cell column=null;
    	String columnValues="";
    	StorageBucketUtility util = new StorageBucketUtility();
    	try {
    		//Fetching the newly created XL File
    		 createdfile= createXlSheet();
			 input= new FileInputStream(createdfile);
			 workbook = new XSSFWorkbook();
			 sheet= workbook.createSheet("MAH URLs");
			 header= sheet.createRow(0);
			 //converting arraylist to string
			 columnValues= String.join(",", columnbasevalues);
			 String[] words= columnValues.split(",");
			  
			for(String s : words){	
					for(int i=1; i<words.length; i++)
					{	
						header= sheet.createRow(i);
						column= header.createCell(0); 
						column.setCellValue(words[i]);					
					}
					//System.out.println(s);
			 }
			output = new FileOutputStream(createdfile);
			workbook.write(output);
			output.flush();		
			System.out.println("Request URLs successfully written");
			
			validateURL(createdfile);
			sendMail(createdfile);
			util.writeFileToBucket(createdfile);
		
		} catch (FileNotFoundException e) {
			System.out.println("Exception occured in "+methodsName+ " : "+e);
		} catch (IOException e) {
			System.out.println("Exception occured in "+methodsName+ " : "+e);
		}catch(Exception e)
    	{
			EmailManagement failed_email =new EmailManagement();
    	}
    	
    }
    
    private void validateURL(String createdfile)
    {
    	ReadWriteURLSSL ssl = new ReadWriteURLSSL();
    	try {
			ssl.excelReadAndCheck(createdfile);
			
			ssl.showCertInfo(createdfile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
   private void sendMail(String createdfile)
   {
	   EmailManagement email =new EmailManagement();
	   email.emailConfigurations(createdfile);
	   
	   System.out.println("Message sent");
   }

   
   




    
    
    
    
}
