package com.wilp.bits.lambda;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.IOUtils;
import com.wilp.bits.aws.utility.InstanceUtility;
import com.wilp.bits.config.utility.ReadWriteProps;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;
import software.amazon.awssdk.services.ssm.model.SendCommandResponse;
/*
 * Amazon Lambda Function
 * 
 * This Lambda function is used to trigger AWS SSM which in-turn runs the bash commands on AWS EC2
 */
public class ConnectEC2UsingSSM implements RequestHandler<S3Event, String> {
	String methodsName="";
	ReadWriteProps props = new ReadWriteProps();
	String[] keys = props.ReadPropsFile().split(",");
	String accesskey = keys[0];
	String secretkey = keys[1];
	BasicAWSCredentials awsCreds = new BasicAWSCredentials(accesskey, secretkey);
	private final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion("ap-south-1").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
	
	private static final Logger connectEc2UsingSsm = Logger.getLogger(ConnectEC2UsingSSM.class.getName());
	
	public String handleRequest(S3Event events, Context context) {
		methodsName="handleRequest()";
		connectEc2UsingSsm.info("Inside "+methodsName+" -- Start");
		try{
			readS3MetaData(events);
			rmPreviousJars();
			executeScriptInSSM();
		}catch(Exception e)
		{
			connectEc2UsingSsm.info("Exception occured in " + methodsName + " : " + e);
			executeScriptInSSM();
		}
		connectEc2UsingSsm.info("Inside "+methodsName+" -- End");
		return "ConnectEC2UsingSSM  Completed with Success";
	}

	private String readS3MetaData(S3Event events)
	{
		S3EventNotificationRecord notify;
		String srcBucket="";
		String srcKey="";
		methodsName="handleRequest()";
	
		try {
				connectEc2UsingSsm.info("Inside "+methodsName+" -- Start");
                srcBucket=events.getRecords().get(0).getS3().getBucket().getName();
                srcKey=events.getRecords().get(0).getS3().getObject().getKey();
                InputStream input=s3Client.getObject(srcBucket, srcKey).getObjectContent();
                String content= IOUtils.toString(input);
                connectEc2UsingSsm.info("Bucket Name : "+srcBucket+"  File Name: "+srcKey);
                
         } catch (Exception e) {
        	 connectEc2UsingSsm.info("Exception occured in " + methodsName + " : " + e);
        }
		connectEc2UsingSsm.info("Inside "+methodsName+" -- End");
		
		return "Successfullly Read from S3Bucket";
    }

	private void rmPreviousJars()
	{
		try
		{	connectEc2UsingSsm.info("Inside "+methodsName+" -- Start");
			InstanceUtility ec2Util = new InstanceUtility();
			// AWS Credentials integrated
			

			
			SsmClient ssmClient = SsmClient.builder().region(Region.AP_SOUTH_1)
					.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accesskey, secretkey)))
					.build();
		
			String instanceId = ec2Util.getInstanceId();
			connectEc2UsingSsm.info("Instance Id from Lambda: " + instanceId);
			
			//Bash Scripts
			List<String> scripts = Arrays.asList(
				    "echo 'AWS-Lambda removing bash scripts through SSM'",
				    "sudo -i",
				    "rm *URLValidatorElite*.jar",
				    "echo 'Success'"
				);

			// Execute each script
			for (int i = 0; i < scripts.size(); i++) {
				SendCommandRequest sendrequest = SendCommandRequest.builder().instanceIds(instanceId)
						.documentName("AWS-RunShellScript")
						.parameters(Collections.singletonMap("commands", Arrays.asList(scripts.get(i)))).build();

				SendCommandResponse response = ssmClient.sendCommand(sendrequest);
				String commandId = response.command().commandId();

				connectEc2UsingSsm.info("Script " + (i + 1) + " execution triggerd. Command Id: " + commandId);
			}
			}catch(Exception e)
			{
				connectEc2UsingSsm.info("Exception occured in " + methodsName + " : " + e);
			}
	}
   private void executeScriptInSSM()
	{
		methodsName="executeScriptInSSM()";
		
		try
		{	connectEc2UsingSsm.info("Inside "+methodsName+" -- Start");
			InstanceUtility ec2Util = new InstanceUtility();
			// AWS Credentials integrated
			

			
			SsmClient ssmClient = SsmClient.builder().region(Region.AP_SOUTH_1)
					.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accesskey, secretkey)))
					.build();
		
			String instanceId = ec2Util.getInstanceId();
			connectEc2UsingSsm.info("Instance Id from Lambda: " + instanceId);
			
			//Bash Scripts
			List<String> scripts = Arrays.asList(
				    "echo 'AWS-Lambda executing bash scripts on AWS EC2 through SSM'",
				    "sudo -i",
				    "yum install java-1.8.0",
				    "#rm URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar",  
				    "wget 'https://my-bits-wilp-jars.s3.ap-south-1.amazonaws.com/URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar'",
				    "ls",
				    "java -jar URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
				   // String.format("aws ssm send-command --instance-ids %s --document-name 'AWS-RunShellScript' --parameters '{\"commands\":[\"java -jar URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar\"]}' --region ap-south-1", instanceId),
				    "echo 'Success'"
				);

			// Execute each script
			for (int i = 0; i < scripts.size(); i++) {
				SendCommandRequest sendrequest = SendCommandRequest.builder().instanceIds(instanceId)
						.documentName("AWS-RunShellScript")
						.parameters(Collections.singletonMap("commands", Arrays.asList(scripts.get(i)))).build();

				SendCommandResponse response = ssmClient.sendCommand(sendrequest);
				String commandId = response.command().commandId();

				connectEc2UsingSsm.info("Script " + (i + 1) + " execution triggerd. Command Id: " + commandId);
			}
			}catch(Exception e)
			{
				connectEc2UsingSsm.info("Exception occured in " + methodsName + " : " + e);
			}
		connectEc2UsingSsm.info("Inside "+methodsName+" -- Ends");
	}
}
