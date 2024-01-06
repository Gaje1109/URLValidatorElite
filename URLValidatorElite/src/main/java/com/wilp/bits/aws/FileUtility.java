package com.wilp.bits.aws;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public class FileUtility {
	
		//Encrypted - Files
		String Input_encrypted_fileName = "lib/WILP_BITS_DESIGN_PROJECT_2023-2024.txt";
		String output_encrypted_fileName = "lib/WILP_BITS_DESIGNPROJECT2023-24.enc";
		//Decrypted - Files
		String  input_decrypted_fileName="lib/WILP_BITS_DESIGNPROJECT2023-24.enc";
		
		
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

	// Decrypt Data
	public String decryptData() {
		BufferedReader buffreader = null;
		String decryptedvalue ="";
		String stringvalues;

		try {
			buffreader = new BufferedReader(new FileReader(input_decrypted_fileName));
			while ((stringvalues = buffreader.readLine()) != null) {
				System.out.println("Before Decryption :" + stringvalues);
				char[] word = stringvalues.toCharArray();

				for (int j = 0; j < word.length; j++) {
					word[j] -= 3;
				}

				decryptedvalue= new String(word);
				System.out.println("Data decrypted successfully");
			}

		} catch (Exception e) {
			System.out.println("Exception occured in decryptData() method: " + e);
		} finally {
			try {
				buffreader.close();
			} catch (IOException e) {
				System.out.println("Exception occured in decryptData() method: " + e);
			}
			
		}
		return decryptedvalue;
	}
}
