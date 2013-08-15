package com.msi.tough.utils;

/**
 * Common constants used by the elasticache service
 * 
 * @author Stephen Dean
 */
public interface Constants {
	public static final String ATTRIBUTE_XMLNS = "xmlns";
	public static final String NAMESPACE = "http://monitoring.amazonaws.com/doc/2010-08-01/";

	public static final String ENDPOINT_OPTIONS = "ENDPOINT_OPTIONS";
	public static final String USE_UUID = "UseUUID";

	public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	// configuration constants
	public static final String EC2URL = "EC2_URL";
	public static final String TRANSCENDURL = "TRANSCEND_URL";

	// common constants
	public static final String AVAILABILITYZONE = "AvailabilityZone";
	public static final String AVAILABILITYZONES = "AvailabilityZones";
	public static final String EC2SECURITYGROUPNAME = "EC2SecurityGroupName";
	public static final String EC2SECURITYGROUPOWNERID = "EC2SecurityGroupOwnerId";
	public static final String PORT = "Port";
	public static final String PRIVATEIP = "PrivateIp";
	public static final String PUBLICIP = "PublicIp";
	public static final String MARKER = "Marker";
	public static final String MAXRECORDS = "MaxRecords";
	public static final String SERVICE="Service";

	// CF Internal
	public static final String ACID = "__ACID__";
	public static final String CHEFROLES = "__CHEF_ROLES__";
	public static final String CHEFENV = "__CHEF_ENV__";
	public static final String DATABAG = "__DATABAG__";
	public static final String DEPENDSON = "DependsOn";
	public static final String HOSTNAME = "__HOSTNAME__";
	public static final String CONFIG = "config";

	// Instance
	public static final String MINCOUNT = "MinCount";
	public static final String MAXCOUNT = "MaxCount";
	public static final String IMAGEID = "ImageId";
	public static final String INSTANCETYPE = "InstanceType";
	public static final String INSTANCEIDS = "InstanceIds";
	public static final String INSTANCEID = "InstanceId";
	public static final String KERNELID = "KernelId";
	public static final String KEYNAME = "KeyName";
	public static final String RAMDISKID = "RamdiskId";
	public static final String USERDATA = "UserData";
	public static final String SECURITYGROUPIDS = "SecurityGroupIds";

	public static final String IPPROTOCOL = "IpProtocol";
	public static final String CIDRIP = "CidrIp";
	public static final String GROUPNAME = "GroupName";
	public static final String GROUPID = "GroupId";
	public static final String SOURCESECURITYGROUPNAME = "SourceSecurityGroupName";
	public static final String SOURCESECURITYGROUPOWNERID = "SourceSecurityGroupOwnerId";
	public static final String FROMPORT = "FromPort";
	public static final String TOPORT = "ToPort";

	// ElastiCache

	public static final String AUTOMINORVERSIONUPGRADE = "AutoMinorVersionUpgrade";
	public static final String CACHECLUSTERID = "CacheClusterId";
	public static final String CACHENODETYPE = "CacheNodeType";
	public static final String CACHEPARAMETERGROUPNAME = "CacheParameterGroupName";
	public static final String CACHEPARAMETERGROUPFAMILY = "CacheParameterGroupFamily";
	public static final String CACHESECURITYGROUPNAME = "CacheSecurityGroupName";
	public static final String CACHESECURITYGROUPNAMES = "CacheSecurityGroupNames";
	public static final String ENGINE = "Engine";
	public static final String ENGINEVERSION = "EngineVersion";
	public static final String NOTIFICATIONTOPICARN = "NotificationTopicArn";
	public static final String NUMCACHENODES = "NumCacheNodes";
	public static final String PREFERREDAVAILABILITYZONE = "PreferredAvailabilityZone";
	public static final String PREFERREDMAINENANCEWINDOW = "PreferredMainenanceWindow";
	public static final String SOURCE = "Source";
	public static final String MAXSIMULTANEOUSCONNECTIONS = "max_simultaneous_connections";
	public static final String MAXCACHEMEMORY = "max_cache_memory";

	public static final String CACHENODETYPESPECIFICPARAMETERS = "CacheNodeTypeSpecificParameters";
	public static final String CACHENODETYPESPECIFICPARAMETER = "CacheNodeTypeSpecificParameter";
	public static final String CACHENODETYPESPECIFICVALUES = "CacheNodeTypeSpecificValues";
	public static final String CACHENODETYPESPECIFICVALUE = "CacheNodeTypeSpecificValue";
	public static final String RESETALLPARAMETERS = "ResetAllParameters";
	public static final String SECURITYGROUPINGRESSTYPE = "AWS::ElastiCache::SecurityGroupIngress";
	public static final String CACHESECURITYGROUPINGRESS = "CacheSecurityGroupIngress";

	// AutoScaling
	public static final String CHANGEINCAPACITY = "ChangeInCapacity";
	public static final String EXACTCAPACITY = "ExactCapacity";
	public static final String PERCENTCHANGEINCAPACITY = "PercentChangeInCapacity";

	// Cloud Watch
	// alarm identifier
	public final static String STATE_INSUFFICIENT_DATA = "INSUFFICIENT_DATA";
	public final static String STATE_OK = "OK";
	public final static String STATE_ALARM = "ALARM";
	public final static String COMPARE_GREATER_OR_EQUAL = "GreaterThanOrEqualToThreshold";
	public final static String COMPARE_GREATER = "GreaterThanThreshold";
	public final static String COMPARE_LESS = "LessThanThreshold";
	public final static String COMPARE_LESS_OR_EQUAL = "LessThanOrEqualToThreshold";

	// Relational Database Service
	public static final String DBSECURITYGROUPNAME = "DBSecurityGroupName";
	public static final String DBSNAPSHOTIDENTIFIER = "DBSnapshotIdentifier";
	public static final String RDS_CIDRIP = "CIDRIP";
	public static final int DEFAULT_RDS_PORT = 3306;
	public static final String DEFAULT_RDS_PROTOCOL = "tcp";
	public static final String DBINSTANCECLASS = "DBInstanceClass";
	public static final String DBINSTANCEIDENTIFIER = "DBInstanceIdentifier";
	public static final String SOURCEDBINSTANCEIDENTIFIER = "SourceDBInstanceIdentifier";
	public static final String USER = "user";

	// Private
	public static final String NODE = "Node";
	public static final String CHEFNOTIFICATIONRESPONSE = "ChefNotificationResponse";
	public static final String CHEFNOTIFICATIONRESULT = "ChefNotificationResult";
	public static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
}
