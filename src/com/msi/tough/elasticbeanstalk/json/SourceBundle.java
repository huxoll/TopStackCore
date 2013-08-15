package com.msi.tough.elasticbeanstalk.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class SourceBundle {
	@JsonProperty("S3Bucket")
	private String S3Bucket;
	@JsonProperty("S3Key")
	private String S3Key;
	
	public SourceBundle(String bucket, String key){
		this.S3Bucket = bucket;
		this.S3Key = key;
	}
}
