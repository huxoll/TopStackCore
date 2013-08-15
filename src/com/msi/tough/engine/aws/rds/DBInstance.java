package com.msi.tough.engine.aws.rds;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.InstanceType;
import com.msi.tough.cf.ec2.VolumeType;
import com.msi.tough.cf.rds.DBInstanceType;
import com.msi.tough.core.CommaObject;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.core.MapUtil;
import com.msi.tough.engine.aws.ec2.Instance;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsEC2SecurityGroupBean;
import com.msi.tough.model.rds.RdsIPRangeBean;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.RDSUtil;
import com.msi.tough.utils.SecurityGroupUtils;
import com.msi.tough.utils.VolumeUtil;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.SecurityGroupEntity;

public class DBInstance extends BaseProvider {
	final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	public static String TYPE = "AWS::RDS::DBInstance";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		logger.debug("CallStruct Name: " + call.getName());
		final AccountType at = call.getAc();
		final String avZone = (String) call.getProperty("AvailabilityZone");
		final TemplateContext ctx = call.getCtx();
		final String desc = call.getDescription();
		final String name = call.getName();
		final int noWait = call.getNoWait();
		final String parentId = call.getParentId();
		final String physicalId = call.getPhysicalId();
		final Map<String, Object> properties = call.getProperties();
		final String stackId = call.getStackId();
		final String type = call.getType();

		final Session session = HibernateUtil.newSession();
		final AccountBean ab = AccountUtil.readAccount(session,
				at.getAccessKey());
		session.close();

		logger.debug("Details from the CallStruct:\n" + "AccountType: "
				+ at.getId() + "\n" + "Account AccessKey: " + at.getAccessKey()
				+ "\n" + "Account SecretKey: " + at.getSecretKey() + "\n"
				+ "Availability Zone: " + avZone + "\n" + "TemplateContext: "
				+ ctx + "\n" + "Description: " + desc + "\n" + "Name: " + name
				+ "\n" + "NoWait: " + noWait + "\n" + "Parent Id: " + parentId
				+ "\n" + "Physical Id: " + physicalId + "\n" + "Properties: "
				+ properties + "\n" + "Stack Id: " + stackId + "\n" + "Type: "
				+ type);

		final CFType dbInstanceType = new DBInstanceType();
		final Date date = new Date();
		dbInstanceType.setAcId(at.getId());
		dbInstanceType.setCreatedTime(date);
		dbInstanceType.setUpdatedTime(date);
		dbInstanceType.setName(name);
		// dbInstanceType.setDatabag(databag);
		dbInstanceType.setNoWait("" + noWait);
		dbInstanceType.setPhysicalId(physicalId);
		dbInstanceType.setStackId(stackId);
		dbInstanceType.setPostWaitUrl((String) ConfigurationUtil
				.getConfiguration(Arrays.asList(new String[] { "TRANSCEND_URL",
						avZone })));
		// dbInstanceType.setPostWaitUrl(postWaitUrl);
		final String dbSnapshotIdentifier = (String) call
				.getProperty("DBSnapshotIdentifier");
		final String allocatedStorage = (String) call
				.getRequiredProperty("AllocatedStorage");
		final String avz = (String) call.getProperty("AvailabilityZone");
		final String backupRetentionPeriod = (String) call
				.getProperty("BackupRetentionPeriod");
		final String dbInstanceClass = (String) call
				.getRequiredProperty("DBInstanceClass");
		final String dbName = (String) call.getProperty("DBName");
		final String dbParameterGroupName = (String) call
				.getProperty("DBParameterGroupName");
		final String securityGroups = (String) call
				.getProperty("DBSecurityGroups");
		final String engine = (String) call.getRequiredProperty("Engine");
		final String engineVersion = (String) call.getProperty("EngineVersion");
		final String licenseModel = (String) call.getProperty("LicenseModel");
		final String masterUsername = (String) call
				.getRequiredProperty("MasterUsername");
		final String masterUserPassword = (String) call
				.getRequiredProperty("MasterUserPassword");

		final String port = (String) call.getProperty("Port");
		final int port0 = Integer.valueOf(port);

		final String preferredBackupWindow = (String) call
				.getProperty("PreferredBackupWindow");
		final String preferredMaintenanceWindow = (String) call
				.getProperty("PreferredMaintenanceWindow");
		final String multiAZ = (String) call.getProperty("MultiAZ");
		final String chefRole = "Transcend_RDS_mysql";

		// create a new ec2 security group and use it to create the DBInstance
		final String secGrp = "rds-" + at.getId() + "-" + name + "-" + port;
		final String databag = "rds-" + at.getId() + "-" + name;

		final String secGrpId = SecurityGroupUtils.createSecurityGroup(at,
				null, name, stackId, at.getDefZone(), secGrp,
				"RDS Security Group");

		// create the database record
		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final AccountBean ac = AccountUtil.readAccount(s, at.getId());
				final RdsDbsecurityGroup newGrp = new RdsDbsecurityGroup();
				newGrp.setAccount(ac);
				// newGrp.setCounter(1);
				newGrp.setDbsecurityGroupDescription("Auto-generated DBSecurityGroup for internal purpose only!");
				newGrp.setDbsecurityGroupName(secGrp);
				newGrp.setDbsecurityGroupId(secGrpId);
				newGrp.setPort(port0);
				newGrp.setStatus("active");
				newGrp.setTranscendOnly(true);
				newGrp.setStackId(stackId);
				s.save(newGrp);

				logger.debug("DBSecurityGroups to apply: " + securityGroups);
				for (final String dbSecGrpName : new CommaObject(securityGroups)
						.toList()) {
					final RdsDbsecurityGroup dbSecGrp = RDSUtil
							.getSecurityGroup(s, dbSecGrpName, at.getId());
					final CommaObject cdb = new CommaObject(dbSecGrp
							.getInternals());
					cdb.add(secGrp);
					dbSecGrp.setInternals(cdb.toString());
					s.save(dbSecGrp);
					final CommaObject ci = new CommaObject(newGrp
							.getInternals());
					ci.add(dbSecGrpName);
					newGrp.setInternals(ci.toString());

					final AccountType daseinAt = new AccountType();
					daseinAt.setAccessKey(ac.getApiUsername());
					daseinAt.setSecretKey(ac.getApiPassword());
					daseinAt.setTenant(ac.getTenant());
					daseinAt.setId(ac.getId());
					daseinAt.setDefKeyName(ac.getDefKeyName());
					daseinAt.setDefZone(ac.getDefZone());

					for (final RdsEC2SecurityGroupBean ec2secGrp : dbSecGrp
							.getEC2SecGroupBean(s)) {
						final RdsEC2SecurityGroupBean tempEc2SecGrp = new RdsEC2SecurityGroupBean(
								newGrp.getId(), ec2secGrp.getName(), ec2secGrp
										.getEc2SecGroupId(), ec2secGrp
										.getOwnId());
						s.save(tempEc2SecGrp);

						SecurityGroupUtils.authorizeSecurityGroupIngress(
								daseinAt, secGrpId,
								ec2secGrp.getEc2SecGroupId(), stackId,
								ec2secGrp.getOwnId(), port0, name);
					}
					for (final RdsIPRangeBean ip : dbSecGrp.getIPRange(s)) {
						final RdsIPRangeBean tempCidrip = new RdsIPRangeBean(
								newGrp.getId(), ip.getCidrip());
						s.save(tempCidrip);

						SecurityGroupUtils.authorizeSecurityGroupIngress(
								daseinAt, secGrpId, port0, stackId,
								ip.getCidrip());
					}
				}
				s.save(newGrp);
				return null;
			}
		});

		final VolumeType volumeInfo = VolumeUtil.createVolume(at,
				name + "_vol", new TemplateContext(null), name, stackId, avz,
				Integer.parseInt(allocatedStorage));
		final String volId = volumeInfo.getVolumeId();

		final Map<String, Object> prop = MapUtil.create(
				SECURITYGROUPIDS,
				secGrpId,
				AVAILABILITYZONE,
				avz,
				CHEFROLES,
				chefRole,
				DATABAG,
				databag,
				IMAGEID,
				ConfigurationUtil.getConfiguration(Arrays.asList(new String[] {
						IMAGEID, avz, "RDS" })),
				INSTANCETYPE,
				ConfigurationUtil.getConfiguration(Arrays.asList(new String[] {
						dbInstanceClass, avz })),
				KERNELID,
				ConfigurationUtil.getConfiguration(Arrays.asList(new String[] {
						KERNELID, avz, "RDS" })),
				RAMDISKID,
				ConfigurationUtil.getConfiguration(Arrays.asList(new String[] {
						RAMDISKID, avz, "RDS" })));
		prop.put("UseDBInstanceUserdata", true);
		prop.put("DBInstanceIdentifier", name);
		prop.put(INSTANCETYPE, dbInstanceClass);
		prop.put(SERVICE, "rds");

		final InstanceType inst = Instance.createChefInstance(at, name
				+ "_instance", name, call, prop);

		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final RdsDbinstance dbinst = RDSUtil.getInstance(s, name,
						at.getId());
				if (dbinst != null) {
					logger.debug("Updating PublicIP and VolumeId for the new DBInstance with "
							+ inst.getPublicIp()
							+ " and "
							+ volId
							+ " respectively.");
					dbinst.setAddress(inst.getPublicIp());
					dbinst.setInstanceId(inst.getInstanceId());
					dbinst.setVolumeId(volId);
					dbinst.setSecurityGroup(secGrp);
					dbinst.setSecurityGroupId(secGrpId);
					s.save(dbinst);
				}
				return null;
			}
		});
		dbInstanceType.setNoWait("-1");
		return dbInstanceType;
	}

	/**
	 * @param Session
	 *            s This session will be closed before the exiting this method
	 * @param call
	 * @return
	 * @throws Exception
	 */
	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		final AccountType at = call.getAc();
		final String dbInstanceId = call.getPhysicalId();
		HibernateUtil.withNewSession(new Operation<Boolean>() {
			@Override
			public Boolean ex(final Session s, final Object... args)
					throws Exception {
				final RdsDbinstance inst = InstanceEntity.selectDBInstance(s,
						dbInstanceId, at.getId());
				if (inst == null) {
					throw RDSQueryFaults.DBInstanceNotFound();
				} else {
					logger.debug("Current DBInstance state: "
							+ inst.getDbinstanceStatus());
				}
				String sourceDBInstanceId = null;
				if (inst.getRead_only()) {
					sourceDBInstanceId = inst.getSourceDbinstanceId();
				}

				if (inst != null) {
					final String instanceSecGrp = "rds-" + at.getId() + "-"
							+ dbInstanceId + "-" + inst.getPort();
					final List<RdsDbsecurityGroup> secGrpList = SecurityGroupEntity
							.selectAllSecurityGroups(s, instanceSecGrp,
									at.getId(), null, 0);
					if (secGrpList != null && secGrpList.size() > 0) {
						final RdsDbsecurityGroup secGrp = secGrpList.get(0);
						for (final String dbgrp : new CommaObject(secGrp
								.getInternals()).toList()) {
							final RdsDbsecurityGroup dbg = SecurityGroupEntity
									.selectAllSecurityGroups(s, dbgrp,
											at.getId(), null, 0).get(0);
							final CommaObject c = new CommaObject(dbg
									.getInternals());
							c.remove(instanceSecGrp);
							dbg.setInternals(c.toString());
							s.save(dbg);
						}
						SecurityGroupEntity.deleteSecurityGroup(s, secGrp);
					}

					while (inst.getSecurityGroups().size() > 0) {
						inst.getSecurityGroups().remove(0);
						s.save(inst);
					}
					s.delete(inst);
				}

				// delete the databag
				ChefUtil.deleteDatabag("rds-" + at.getId() + "-"
						+ inst.getDbinstanceId());

				while (inst.getSecurityGroups().size() > 0) {
					inst.getSecurityGroups().remove(0);
					s.save(inst);
				}
				s.delete(inst);

				// modify the source DBInstance's read replica list
				if (sourceDBInstanceId != null) {
					final RdsDbinstance source = InstanceEntity
							.selectDBInstance(s, sourceDBInstanceId, at.getId());
					if (source != null) {
						final List<String> replicas = source.getReplicas();
						for (int i = 0; i < replicas.size(); ++i) {
							if (replicas.get(i).equals(dbInstanceId)) {
								replicas.remove(i);
								break;
							}
						}
					} else {
						logger.debug("Could not find the source DBInstance; it is possible that source DBInstance was deleted first.");
					}
				}
				return null;
			}
		});
		return null;
	}

	/*
	 * public Boolean dbInstanceBootstrapHelper(Session s, long acid,
	 * AccountType at, String dbId){ try{ RdsDbinstance inst =
	 * RDSUtil.getInstance(s, dbId, acid); String volId = inst.getVolumeId();
	 * String avz = inst.getAvailabilityZone();
	 * 
	 * Map<String, Object> prop = new HashMap<String, Object>();
	 * prop.put("VolumeId", volId); prop.put(Constants.AVAILABILITYZONE, avz);
	 * 
	 * Volume vol = new Volume(); CallStruct call = new CallStruct();
	 * call.setAc(at); call.setProperties(prop);
	 * 
	 * logger.debug("Calling describe volume...");
	 * 
	 * org.dasein.cloud.compute.Volume result = vol.describe(call); if(result ==
	 * null){ return false; } String instanceId =
	 * result.getProviderVirtualMachineId(); if(instanceId == null){ return
	 * false; } }catch(NullPointerException e){
	 * logger.warn("Dasein API is causing nullpointer exception; ignore!");
	 * return false; } catch (Exception e) {
	 * logger.warn("Dasein API is causing an exception; ignoring the exception: "
	 * + e.getMessage()); return false; } return true; }
	 */

	@Override
	protected String failHookClazz() {
		return DBInstanceFailHook.class.getName();
	}

	@Override
	protected String waitHookClazz() {
		return DBInstanceWaitHook.class.getName();
	}
}
