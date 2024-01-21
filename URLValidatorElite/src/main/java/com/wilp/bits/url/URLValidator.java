package com.wilp.bits.url;

import java.io.File;
import java.io.FileInputStream
;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.wilp.bits.aws.utility.StorageBucketUtility;

import com.wilp.bits.email.EmailManagement;
	public class URLValidator{
	private static final Logger urlvalidator= Logger.getLogger(URLValidator.class.getName());
	String methodsName="";
	String xlfileName= "URLVALIDATOR_&date&_1.xlsx";
	  
    public static void main( String[] args ) throws IOException
    {
      URLValidator url = new URLValidator();  
      urlvalidator.info("-----------URL VALIDATOR ELITE------------");
      //Read and Write Excel
      ArrayList<String> columnvalues =url.readXlFile();
      url.writeXlSheet(columnvalues);

    }        
    
    // Create New File
    private File createXlSheet()
    {
    	methodsName="createXlSheet()";
    	File tempFile = null;
    	try
    	{	
    		urlvalidator.info("Inside "+methodsName+" -- Start");
    		String current_date_time= getExactDateAndTime();
    		String home= System.getProperty("user.home");
    		File directory= new File(home,"/tmp");
    		directory.mkdir();
    		urlvalidator.info("Home : "+home+ " Directory is: "+directory);
    		tempFile=new File(directory,current_date_time);
    		tempFile.createNewFile();
    		urlvalidator.info("New File Created : "+tempFile);
    	} catch (Exception e) {
			urlvalidator.log(Level.SEVERE,"Exception occured in "+methodsName+" : " +e);
		}
    		urlvalidator.info("Inside "+methodsName+" -- End ");
    	return tempFile;
    }
    
    //Name the file with Current date and time
    private String getExactDateAndTime(){
    	String dateTimeFileName="";
    	methodsName="getExactDateAndTime()";
    	try
    	{
    		urlvalidator.info("Inside "+methodsName+" -- Start");
    		SimpleDateFormat dt1= new SimpleDateFormat("yyyyMMdd_HHmmss");
    		dateTimeFileName= dt1.format(new Date());
    		urlvalidator.info("Generated date file: "+dateTimeFileName);
    		if(dateTimeFileName!=null && dateTimeFileName.length()>0)
    		{
    			if(xlfileName!=null)
    			{
    				xlfileName= xlfileName.replace("&date&", dateTimeFileName);
    			}
    		}

    	}catch(Exception e)
    	{
			urlvalidator.info("Exception occured in "+methodsName+" : " +e);
    	}
    	urlvalidator.info("Inside "+methodsName+" -- End ");
    	return xlfileName;
    }
    
    //Read Request XL File
    private ArrayList<String> readXlFile() throws IOException
    {
    	methodsName="readXlFile()";
    	urlvalidator.info("Inside "+methodsName+" -- Start");
    	String columnValues="";
    	final String bucketName="bits-wilp-ap-south-1";
    	ArrayList<String> columnval = null;
    	StorageBucketUtility amazonutils= new StorageBucketUtility();
	
    	try
    	{
    	//Getting files from S3 bucket
    	String inputfile= amazonutils.getFiles(bucketName,"");
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
    			//urlvalidator.info(columnValues +"\t\t\t");
    			columnval.add(columnValues);    			
    		}
    	}

    	}catch(FileNotFoundException e)
    	{
    		urlvalidator.info("Exception occured in "+methodsName+ " : "+e);
    	} catch (IOException e) {
    		urlvalidator.info("Exception occured in "+methodsName+ " : "+e);
		}
    	urlvalidator.info("Inside "+methodsName+" -- End ");
    	return columnval;
    }
    
   //Write to XL file
    private void writeXlSheet(ArrayList<String> columnbasevalues)
    {
    	methodsName="writeXLSheet()";
    	File createdfile;
    	FileInputStream input=null;
    	FileOutputStream output= null;
    	XSSFWorkbook workbook;
    	XSSFSheet sheet;
    	Row header;
    	Cell column=null;
    	String columnValues="";
    	StorageBucketUtility amazonutils= new StorageBucketUtility();
    	try {
    	   	urlvalidator.info("Inside "+methodsName+" -- Start ");
    		//Fetching the newly created XL File
    	  
    		 createdfile= createXlSheet();
    		 	System.out.println(createdfile);
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
					//urlvalidator.info(s);
			 }
			output = new FileOutputStream(createdfile);
			workbook.write(output);
			output.flush();		
			urlvalidator.info("Request URLs successfully written");
			
			validateURL(createdfile);
			sendMail(createdfile);
			amazonutils.writeFileToBucket(createdfile);
		
		} catch (FileNotFoundException e) {
			urlvalidator.info("Exception occured in "+methodsName+ " : "+e);
		} catch (IOException e) {
			urlvalidator.info("Exception occured in "+methodsName+ " : "+e);
		}catch(Exception e)
    	{
			urlvalidator.info("Exception occured in "+methodsName+ " : "+e);
    	}
    	finally
    	{
			try {
				output.close();
			} catch (IOException e) {
				urlvalidator.info("Exception occured in "+methodsName+ " : "+e);
			}
    	}
       	urlvalidator.info("Inside "+methodsName+" -- End ");
    }
    
    private void validateURL(File createdfile)
    {
    	methodsName="validateURL()";
    	urlvalidator.info("Inside "+methodsName+" -- Start ");
    	ReadWriteURLSSL ssl = new ReadWriteURLSSL();
    	try {
			ssl.excelReadAndCheck(createdfile);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception occured in "+methodsName+ " : "+e);
		}
    	urlvalidator.info("Inside "+methodsName+" -- End ");
    }
    
   private void sendMail(File createdfile)
   {
	   methodsName="sendMail()";
	   try
	   {
   		urlvalidator.info("Inside "+methodsName+" -- Start ");
	   EmailManagement email =new EmailManagement();
	   email.emailConfigurations(createdfile);
	   
	   urlvalidator.info("Message sent");
	   }catch(Exception e)
	   {
		   urlvalidator.info("Exception occured in "+methodsName+ " : "+e);
	   }
	   urlvalidator.info("Inside "+methodsName+" -- End ");
   }

    
}