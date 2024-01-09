package com.wilp.bits.config.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReadWriteProps {
	
	public ReadWriteProps()
	{
		
	}
	public String ReadPropsFile()
	{
		String accessKey = "";
		String secretKey = "";
		InputStream propertyStream=null;
		try
		{
		final String propertiesFileName="URLValidatorConfig.properties";
		System.out.println(propertiesFileName);
		ClassLoader loader = ReadWriteProps.class.getClassLoader(); 
		System.out.println(loader);
		 propertyStream= loader.getResourceAsStream(propertiesFileName);
		
		if(propertyStream!=null)
		{
		Properties props= new Properties();
		props.load(propertyStream);	
	
		accessKey= props.getProperty("accessKey");
		 secretKey= props.getProperty("secretKey");
		 
		
		
		}else
		{
			System.out.println("Properties file not found");
		}
		//inputStream.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}finally
		{
			try {
				propertyStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return accessKey+","+secretKey;	
	}
}
