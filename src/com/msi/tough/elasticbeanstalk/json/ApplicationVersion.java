package com.msi.tough.elasticbeanstalk.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class ApplicationVersion {
	@JsonProperty("VersionLabel")
	private String versionLabel;
	@JsonProperty("Description")
	private String description;
	@JsonProperty("SourceBundle")
	private SourceBundle sourceBundle;
	
	public ApplicationVersion(String label, String desc, SourceBundle s){
		this.versionLabel = label;
		this.sourceBundle = s;
		if(desc == null){
			this.description = "";
		}
	}
	
	public ApplicationVersion(String label, String desc, String S3Bucket, String S3Key){
		this.versionLabel = label;
		this.sourceBundle = new SourceBundle(S3Bucket, S3Key);
		if(desc == null){
			this.description = "";
		}
	}
}
