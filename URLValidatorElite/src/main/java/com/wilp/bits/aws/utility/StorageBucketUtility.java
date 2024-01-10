package com.wilp.bits.aws.utility;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

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
import com.wilp.bits.config.utility.ReadWriteProps;
import com.wilp.bits.url.URLValidator;

// Amazon S3 Bucket Utility
public class StorageBucketUtility {
	String methodsName = "";
	private static final Logger storageBucketUtility = Logger.getLogger(StorageBucketUtility.class.getName());
	ReadWriteProps props = new ReadWriteProps();
	String[] keys = props.ReadPropsFile().split(",");
	String accesskey = keys[0];
	String secretkey = keys[1];
	BasicAWSCredentials awsCreds = new BasicAWSCredentials(accesskey, secretkey);
	AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("ap-south-1")
			.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

	public String getFiles(String bucketName, String fileName) throws IOException {
		methodsName = "getFiles()";
		String downloadedfilename = "";

		try {
			storageBucketUtility.info("Inside " + methodsName + "-- Start");

			ObjectListing list = s3Client.listObjects(bucketName);

			for (S3ObjectSummary object : list.getObjectSummaries()) {
				storageBucketUtility.info("File: " + object.getKey() + " Size: " + object.getSize());
				fileName = object.getKey();
			}
			downloadedfilename = downloadFile(s3Client, bucketName, fileName);

		} catch (Exception e) {
			storageBucketUtility.info("Exception occured in " + methodsName + " : " + e);
		}
		storageBucketUtility.info("Inside " + methodsName + "-- End");
		return downloadedfilename;
	}

	public String downloadFile(AmazonS3 s3client, String bucketName, String fileKey) {
		methodsName = "downloadFile()";
		storageBucketUtility.info("Inside " + methodsName + "-- starts");
		String filepath = "";
		try {
			TransferManager transferManager = TransferManagerBuilder.standard().withS3Client((AmazonS3) s3client)
					.build();
			// Set the destination file path
			filepath = "/tmp/" + fileKey;
			File destinationFile = new File(filepath);

			// Initiate the download
			Download download = transferManager.download(bucketName, fileKey, destinationFile);

			// Block and wait for the download to complete
			download.waitForCompletion();

			storageBucketUtility.info("File downloaded to: " + destinationFile.getAbsolutePath());

		} catch (Exception e) {
			storageBucketUtility.info("Exception occured in " + methodsName + " : " + e);
		}
		storageBucketUtility.info("Inside " + methodsName + "-- End");
		return filepath;
	}

	public void writeFileToBucket(File createdfile) {
		methodsName = "writeFileToS3Bucket()";
		final String bucketName = "my-bits-wilp-jars";
		String folderName = "MAH";
		storageBucketUtility.info("Inside " + methodsName + "-- Start");
		try {
			String fileNameInS3 = createdfile.getName();
			storageBucketUtility.info(createdfile.getName());
			storageBucketUtility.info(fileNameInS3);
			String key = folderName + "/" +fileNameInS3;

			PutObjectRequest request = new PutObjectRequest(bucketName, key, createdfile);
			s3Client.putObject(request);
			storageBucketUtility.info("---File Placed in S3 Bucket---");
		} catch (Exception e) {
			storageBucketUtility.info("Exception occured in " + methodsName + " : " + e);
		}

		storageBucketUtility.info("Inside " + methodsName + "-- ends");
	}
}
