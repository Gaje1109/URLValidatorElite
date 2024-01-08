package com.wilp.bits.aws;

import com.amazonaws.util.EC2MetadataUtils;

//Getting EC2 data
public class EC2Utilities {	
	
	public String getEC2InstanceData()
	{
		String instanceId= EC2MetadataUtils.getInstanceId();
		return instanceId;
	}
}
