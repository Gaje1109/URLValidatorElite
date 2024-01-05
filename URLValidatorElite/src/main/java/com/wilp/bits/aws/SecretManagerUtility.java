package com.wilp.bits.aws;



import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;



public class SecretManagerUtility {

	
	public void getSecretsFromAWS()
	{
		String secretName="bits/wilp/designproject/2023-24";
		Region region= Region.AP_SOUTH_1;
		SecretsManagerClient secretsmanager;
		GetSecretValueResponse getSecretValueResponse;
		
		try{
			  secretsmanager = SecretsManagerClient.builder().credentialsProvider(EnvironmentVariableCredentialsProvider.create()).region(region).build();
		
			GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
		            .secretId(secretName)
		            .build();
			 getSecretValueResponse = secretsmanager.getSecretValue(getSecretValueRequest);
			 String secret = getSecretValueResponse.secretString();
			  System.out.println("Secret String for " + secretName + ": " + secret);
	   
		}catch(SecretsManagerException e)
		{
			System.err.println("Error reading secrets from AWS Secrets Manager: " + e.getMessage());
		}catch(Exception e1)
		{
			System.err.println("Error reading secrets from AWS Secrets Manager: " + e1.getMessage());
		}finally{
			//secretsmanager.close();
		}
	}
}
