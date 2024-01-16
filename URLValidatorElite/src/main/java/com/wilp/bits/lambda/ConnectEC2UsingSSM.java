package com.wilp.bits.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
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
public class ConnectEC2UsingSSM implements RequestHandler<String, String> {
	String methodsName="";
	
	private static final Logger connectEc2UsingSsm = Logger.getLogger(ConnectEC2UsingSSM.class.getName());

	public String handleRequest(String input, Context context) {
		methodsName="handleRequest";
		connectEc2UsingSsm.info("Inside "+methodsName+" -- Start");
		try{
		InstanceUtility ec2Util = new InstanceUtility();
		// AWS Credentials integrated
		ReadWriteProps props = new ReadWriteProps();
		input="ConnectEC2UsingSSM  Completed with Success";
		String[] keys = props.ReadPropsFile().split(",");
		String accesskey = keys[0];
		String secretkey = keys[1];
		SsmClient ssmClient = SsmClient.builder().region(Region.AP_SOUTH_1)
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accesskey, secretkey)))
				.build();

		String instanceId = ec2Util.getInstanceId();
		connectEc2UsingSsm.info("Instance Id from Lambda: " + instanceId);
		List<String> scripts = Arrays.asList(
			    "echo 'AWS-Lambda executing AWS EC2 through SSM'",
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
		//ssmClient.close();
		connectEc2UsingSsm.info("Inside "+methodsName+" -- End");
		return input;
	}

}
