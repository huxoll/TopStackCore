package com.msi.tough.cf.ec2;

import java.util.List;

import com.msi.tough.cf.CFType;

public class VolumeType extends CFType {
	private String availabilityZone;
	private String size;
	private String snapshotId;
	private String volumeId;
	private List<EC2TagType> tags;

	@Override
	public Object getAtt(final String key) {
		return null;
	}

	public String getAvailabilityZone() {
		return availabilityZone;
	}

	public String getSize() {
		return size;
	}

	public String getSnapshotId() {
		return snapshotId;
	}

	public List<EC2TagType> getTags() {
		return tags;
	}

	public String getVolumeId() {
		return volumeId;
	}

	@Override
	public Object ref() {
		return volumeId;
	}

	public void setAvailabilityZone(final String availabilityZone) {
		this.availabilityZone = availabilityZone;
	}

	public void setSize(final String size) {
		this.size = size;
	}

	public void setSnapshotId(final String snapshotId) {
		this.snapshotId = snapshotId;
	}

	public void setTags(final List<EC2TagType> tags) {
		this.tags = tags;
	}

	public void setVolumeId(final String volumeId) {
		this.volumeId = volumeId;
	}

}
