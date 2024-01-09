package com.wilp.bits.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

	public String handleRequest(String input, Context context) {
		InstanceUtility ec2Util = new InstanceUtility();
		// AWS Credentials integrated
		ReadWriteProps props = new ReadWriteProps();
		String[] keys = props.ReadPropsFile().split(",");
		String accesskey = keys[0];
		String secretkey = keys[1];
		SsmClient ssmClient = SsmClient.builder().region(Region.AP_SOUTH_1)
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accesskey, secretkey)))
				.build();

		String instanceId = ec2Util.getInstanceId();
		System.out.println("Instance Id from Lambda: " + instanceId);
		List<String> scripts = Arrays.asList("echo 'AWS-Lambda executing AWS EC2 through SSM'", "sudo -i",
				"yum install java-1.8.0",
				"wget 'https://my-bits-wilp-jars.s3.ap-south-1.amazonaws.com/URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar'",
				"ls", "#java -jar 'URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar'",
				"aws ssm send-command --instance-ids i-056bb4b85c0a0b245 --document-name 'AWS-RunShellScript' --parameters '{\"commands\":[\"java -jar URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar\"]}' --region ap-south-1",
				"#rm URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar", "#echo 'Jar removed'",
				"#wget https://my-bits-wilp-jars.s3.ap-south-1.amazonaws.com/URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
				"#java -jar URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar", "echo 'Success'");

		// Execute each script
		for (int i = 0; i < scripts.size(); i++) {
			SendCommandRequest sendrequest = SendCommandRequest.builder().instanceIds(instanceId)
					.documentName("AWS-RunShellScript")
					.parameters(Collections.singletonMap("commands", Arrays.asList(scripts.get(i)))).build();

			SendCommandResponse response = ssmClient.sendCommand(sendrequest);
			String commandId = response.command().commandId();

			System.out.println("Script " + (i + 1) + " execution triggerd. Command Id: " + commandId);

		}
		ssmClient.close();
		return "Success";
	}

}
