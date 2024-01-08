package com.wilp.bits.aws;



import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;



public class SecretManagerUtility {

	
	private String getSecretsFromAWS()
	{
		String secretName="bits/wilp/designproject/2023-24";
		Region region= Region.AP_SOUTH_1;
		String accessKeyId="AKIATWVMFH3A36SY6FTD";
		String secretAccessKey="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";
		AwsBasicCredentials awsCred= AwsBasicCredentials.create(accessKeyId, secretAccessKey);
		
		SecretsManagerClient secretClient;
		GetSecretValueRequest getSecretValueRequest;
		GetSecretValueResponse getSecretValueResponse;
		
		try{
			secretClient = SecretsManagerClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(awsCred)).build();
			getSecretValueRequest= GetSecretValueRequest.builder().secretId(secretName).build();
			 getSecretValueResponse = secretClient.getSecretValue(getSecretValueRequest);
			 String secret = getSecretValueResponse.secretString();
			 if(getSecretValueResponse.secretString()!=null)
			 {
			  System.out.println("Secret String for " + secretName + ": " + secret);
			  return getSecretValueResponse.secretString();
			 }
	   
		}catch(SecretsManagerException e)
		{
			System.err.println("Error reading secrets from AWS Secrets Manager: " + e.getMessage());
		}catch(Exception e1)
		{
			System.err.println("Error reading secrets from AWS Secrets Manager: " + e1.getMessage());
		}finally{
			//secretsmanager.close();
		}
		return "";
	}
}
