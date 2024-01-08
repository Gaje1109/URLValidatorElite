package com.wilp.bits.aws;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/*
 * This method is used to encrypt AWS Credentials, Read S3 bucket for fetching the encrypted file and decrypt the file.
 */
public class FileUtility {
	
		//Encrypted - Files
		String Input_encrypted_fileName = "lib/WILP_BITS_DESIGN_PROJECT_2023-2024.txt";
		String output_encrypted_fileName = "lib/WILP_BITS_DESIGNPROJECT2023-24.enc";
		
		//Fetching tokens from S3 bucket
		public String getTokens()
		{
			String  input_decrypted_fileName=readS3FileAndDecrypt("my-bits-wilp-jars", "WILP_BITS_DESIGNPROJECT2023-24.enc");
			System.out.println("Access Key and Secret Key fetched : "+input_decrypted_fileName);
			return input_decrypted_fileName;
		}
	
	
	// Encrypt Data
	private void encryptData() throws IOException {
		BufferedReader buffreader = null;
		BufferedWriter buffwriter = null;
		FileOutputStream fos = null;
		ObjectOutputStream ops = null;
		try {

				buffreader = new BufferedReader(new FileReader(Input_encrypted_fileName));
				buffwriter = new BufferedWriter(new FileWriter(output_encrypted_fileName));
				String stringvalues;
				while ((stringvalues = buffreader.readLine()) != null) {
					char[] word = stringvalues.toCharArray();
					for (int j = 0; j < word.length; j++) {
						word[j] += 3;

					}
					String encryptedvalue = new String(word);
					buffwriter.write(encryptedvalue);
					buffwriter.newLine();
				}
				System.out.println("Data encrypted successfully");
			
			
		} catch (FileNotFoundException e) {
			System.out.println("Exception occured in encryptData(): " + e);
		} catch (IOException e) {
			System.out.println("Exception occured in encryptData(): " + e);
		} finally {
			//
			 buffreader.close();
			 buffwriter.close();

		}
	}//encrypt files --close

	
	//Read S3 bucket and decrypt the contents
	public String readS3FileAndDecrypt(String bucket_name, String key_name)
	{
		String line="";
		String decryptedvalue ="";
		char[] word;
		try{
		AmazonS3 s3= AmazonS3ClientBuilder.defaultClient();
		com.amazonaws.services.s3.model.S3Object s3object= s3.getObject(new com.amazonaws.services.s3.model.GetObjectRequest(bucket_name,key_name));
		InputStream input= s3object.getObjectContent();
		BufferedReader br= new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
		
		while((line= br.readLine())!=null)
		{
			System.out.println("Before Decryption :" + line);
			word= line.toCharArray();

			for (int j = 0; j < word.length; j++) {
				word[j] -= 3;
			}

			decryptedvalue= new String(word);
			System.out.println(word);
			System.out.println("Data decrypted successfully");
		}
		
		}catch(Exception e)
		{
			System.out.println("Exception occured in readS3FileAndDecrypt() method : "+e);
		}
		return decryptedvalue;
	}
}

