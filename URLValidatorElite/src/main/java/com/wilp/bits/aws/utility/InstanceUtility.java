package com.wilp.bits.aws.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//Getting EC2 data
public class InstanceUtility {

	String methodsName="";
	
	//Getting the EC2 instance Id
	public String getInstanceId() {
		
		final String bucket_Name="my-bits-wilp-jars";
		final String file_Name="terraform_outputs.json";
		String instanceId = readBucketJsonFileGetInstanceDetails(bucket_Name,file_Name);
		return instanceId;
	}

	private String readBucketJsonFileGetInstanceDetails(String bucket_name, String fileName) {

		String instanceId = "";
		String line = "";
		AmazonS3 s3;
		S3Object s3object;
		InputStream input = null;
		BufferedReader br = null;
		ObjectMapper object;
		StringBuilder fetchcontent = new StringBuilder();

		try {
			System.out.println("Inside getInstanceId");
			s3 = AmazonS3ClientBuilder.defaultClient();
			s3object = s3.getObject(new GetObjectRequest(bucket_name, fileName));
			input = s3object.getObjectContent();
			br = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
			object = new ObjectMapper();

			while ((line = br.readLine()) != null) {
				fetchcontent.append(line);
				System.out.println(line);
			}
			JsonNode jsonNode = object.readTree(fetchcontent.toString());
			instanceId = jsonNode.path("Bits_wilp_DP_id").path("value").asText();

			System.out.println("Instance ID: " + instanceId);

		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e1)
		{
			System.out.println("Exception occured in readS3JSONFileGetEC2Details() method : " + e1);
		}finally
		{
			try
			{
				br.close();
				input.close();
				
			}catch(Exception e)
			{
				
			}
		}
		return instanceId;
	}

}
