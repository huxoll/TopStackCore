package com.msi.tough.model.gi;

import javax.persistence.*;
import java.util.Set;

import com.msi.tough.model.gi.enums.*;


@Entity
@Table( name = "gi_image_metadata")
public class ImageMetadataBean {

	@Id
	@Column( name = "long_id")
	@GeneratedValue (strategy = GenerationType.IDENTITY)	
	private long id;
	
	@Column( name = "image_id")	
	private String image_id;
	
	@Column( name = "type")
	@Enumerated(EnumType.STRING)	
	private ImageType type;
	
	@Column( name = "platform")
	private String platform;
	
	@Column( name = "os")	
	private String os;
	
	@Column( name = "architecture")
	private String architecture;
	
	@ElementCollection
	@Column( name = "associations")	
	private Set<String> associations;
	
	@Column( name = "GoldenStatus")
	@Enumerated(EnumType.STRING)
	private GoldenStatus status;

	@Column( name = "cloud")
	private String cloud;
	
	public ImageMetadataBean(){
		
	}
	
	public ImageMetadataBean(String image, String givenOS, String givenArch){
		image_id = image;
		if(image_id.contains("emi"))
			type = ImageType.machine;
		if(image_id.contains("eki"))
			type = ImageType.kernel;
		if(image_id.contains("eri"))
			type = ImageType.ramdisk;
		os = givenOS;
		architecture = givenArch;
	}

	public ImageMetadataBean(String image, String givenOS, Set<String> assoc, GoldenStatus stamp){
		image_id = image;
		if(image_id.contains("emi"))
			type = ImageType.machine;
		if(image_id.contains("eki"))
			type = ImageType.kernel;
		if(image_id.contains("eri"))
			type = ImageType.ramdisk;
		os = givenOS;
		associations = assoc;
		status = stamp;
	}
	
	public long getId(){
		return id;
	}
	
	public void setId(long givenId){
		id = givenId;
	}
	
	public String getImage_id(){
		return image_id;
	}
	
	public void setImage_id(String img){
		image_id = img;
	}
	
	public ImageType getType(){
		return type;
	}
	
	public void setType(ImageType givenType){
		type = givenType;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	public String getOs(){
		return os;
	}
	
	public void setOs(String os_name){
		os = os_name;
	}


	public String getArchitecture() {
		return architecture;
	}

	public void setArchitecture(String givenArch) {
		architecture = givenArch;
	}	
	
	public Set<String> getAssociations(){
		return associations;
	}
	
	public void setAssociations(Set<String> assoc){
		associations = assoc;
	}
	
	public GoldenStatus getStatus(){
		return status;
	}
	
	public void setStatus(GoldenStatus newStatus){
		status = newStatus;
	}
	
	public boolean isGolden(){
	   return status == GoldenStatus.accepted;	
	}
	
	public void addAssociation(String newAssoc){
		associations.add(newAssoc);
	}
	
	public void deleteAssociation(String delAssoc){
	    associations.remove(delAssoc);
	}

	public String getCloud() {
		return cloud;
	}

	public void setCloud(String cloud) {
		this.cloud = cloud;
	}

}