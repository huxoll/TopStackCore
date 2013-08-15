/**
 *
 */
package com.msi.tough.model.monitor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dimension")
public class DimensionBean {

    public static final String DIMENSION_AUTO_SCALE_GROUP_NAME =
            "AutoScalingGroupName";
    public static final String DIMENSION_IMAGE_ID = "ImageId";
    public static final String DIMENSION_INSTANCE_ID = "InstanceId";
    public static final String DIMENSION_INSTANCE_TYPE = "InstanceType";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name")
	private String key;

	@Column(name = "value")
	private String value;

	public long getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
