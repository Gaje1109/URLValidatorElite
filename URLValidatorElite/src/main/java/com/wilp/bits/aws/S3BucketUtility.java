package com.wilp.bits.aws;

import java.io.File;
import java.io.IOException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;


//AWS Utilities
public class S3BucketUtility {
	String methodsName="";
	
	//AWS Credentials integrated
	FileUtility fileutil= new FileUtility();
	String[] keys=fileutil.getTokens().split(",");
	String accesskey= keys[0];
	String secretkey=keys[1];
	
	BasicAWSCredentials awsCreds = new BasicAWSCredentials(accesskey,secretkey);
	AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("ap-south-1").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
	
	

	public String getS3Files(String bucket_name, String fileName) throws IOException
	   {
		methodsName="AWSUtilites.getS3Files()";
		System.out.println("Inside "+methodsName+ "-- starts");
		//String fileName= "";
		String downloadedfilename="";
		try{
		  // String bucket_name="bits-wilp-ap-south-1";
		   ObjectListing list= s3Client.listObjects(bucket_name);
			 
			for(S3ObjectSummary object : list.getObjectSummaries())
			{
				  System.out.println("File: " + object.getKey() + " Size: " + object.getSize());
				  fileName= object.getKey(); 
			}
			// readS3Files(fileName);
			downloadedfilename= downloadFile(s3Client,bucket_name,fileName);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("Inside "+methodsName+ "-- ends");
		return downloadedfilename;
	   }

		public String downloadFile(AmazonS3 s3client, String bucketName, String fileKey)
		{
			methodsName="AWSUtilites.downloadFile()";
			System.out.println("Inside "+methodsName+ "-- starts");
			String filepath="";
			try
			{
				TransferManager transferManager = TransferManagerBuilder.standard()
		                .withS3Client((AmazonS3) s3client)
		                .build();
				// Set the destination file path
				filepath= "/tmp/"+fileKey;
	            File destinationFile = new File(filepath);

	            // Initiate the download
	            Download download = transferManager.download(bucketName, fileKey, destinationFile);

	            // Block and wait for the download to complete
	            download.waitForCompletion();

	            System.out.println("File downloaded to: " + destinationFile.getAbsolutePath());
	       
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			return filepath;
		}
	
	
	
		public void writeFileToS3Bucket(String createdfile)
		   {	
			methodsName="AWSUtilites.writeFileToS3Bucket()";
			System.out.println("Inside "+methodsName+ "-- starts");
			String bucketName = "my-bits-wilp-jars";
			String folderName = "folder1";
			String fileNameInS3 = createdfile;
			String fileNameInLocalPC = createdfile;
			String key= folderName + "/" + fileNameInS3;

			PutObjectRequest request = new PutObjectRequest(bucketName, key, new File(fileNameInLocalPC));
			s3Client.putObject(request);
			System.out.println("--Uploading file done"); 
			
			System.out.println("Inside "+methodsName+ "-- ends");
		   }
}
