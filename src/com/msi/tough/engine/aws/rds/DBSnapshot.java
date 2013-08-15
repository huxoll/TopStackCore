package com.msi.tough.engine.aws.rds;

import java.util.Date;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.ec2.VolumeType;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.VolumeUtil;
import com.msi.tough.utils.rds.InstanceEntity;

public class DBSnapshot extends BaseProvider {

	final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	public static String TYPE = "AWS::RDS::DBSnapshot";
	
	@Override
	protected String failHookClazz() {
		return DBSnapshotFailHook.class.getName();
	}
	
	@Override
	public CFType create0(final CallStruct call) throws Exception {
		final AccountType at = call.getAc();
		final String dbInstId = (String) call
				.getProperty(Constants.DBINSTANCEIDENTIFIER);
		final String snapshotId = (String) call
				.getProperty(Constants.DBSNAPSHOTIDENTIFIER);
		final RdsDbinstance inst = HibernateUtil
				.withNewSession(new Operation<RdsDbinstance>() {

					@Override
					public RdsDbinstance ex(final Session s,
							final Object... args) throws Exception {
						return InstanceEntity.selectDBInstance(s, dbInstId,
								at.getId());
					}
				});
		
		final RdsSnapshot snapshot = new RdsSnapshot();
		snapshot.setAllocatedStorage(inst.getAllocatedStorage());
		snapshot.setAvailabilityZone(inst.getAvailabilityZone());
		snapshot.setDbinstanceClass(inst.getDbinstanceClass());
		snapshot.setDbinstanceId(inst.getDbinstanceId());
		snapshot.setDbparameterGroup(inst.getDbParameterGroup());
		snapshot.setDbsnapshotId(snapshotId);
		snapshot.setEngine(inst.getEngine());
		snapshot.setEngineVersion(inst.getEngineVersion());
		snapshot.setInstanceCreatedTime(inst.getInstanceCreateTime());
		snapshot.setLicenseModel(inst.getLicenseModel());
		snapshot.setMasterPasswd(inst.getMasterUserPassword());
		snapshot.setMasterUsername(inst.getMasterUsername());
		snapshot.setPort(inst.getPort());
		snapshot.setSnapshotCreateTime(new Date());
		snapshot.setSnapshotType("manual");
		snapshot.setStatus("creating");
		snapshot.setUserId(at.getId());
		
		HibernateUtil
		.withNewSession(new Operation<Object>() {
			@Override
			public Object ex(final Session s,
					final Object... args) throws Exception {
				s.saveOrUpdate(snapshot);
				return null;
			}
		});
		
		
		createDBSnapshotHelper(at, dbInstId, snapshot, inst);

		final CFType result = new CFType();
		result.setAcId(at.getId());
		result.setName(snapshotId);
		result.setCreatedTime(new Date());
		result.setUpdatedTime(new Date());
		return result;
	}

	private void createDBSnapshotHelper(final AccountType at,
			final String dbIdentifier, final RdsSnapshot snapshot,
			final RdsDbinstance dbInstance) throws Exception {
		
		// create a new volume
		logger.debug("Creating a new volume to store the snapshot...");
		final String stackId = "rds." + at.getId() + "." + dbIdentifier
				+ ".snapshot";
		final VolumeType volType = VolumeUtil.createVolume(at, dbIdentifier
				+ "_snapshot", new TemplateContext(null), null, stackId,
				dbInstance.getAvailabilityZone(),
				dbInstance.getAllocatedStorage());
		logger.debug("Volume Type info: " + volType.toCFString());

		HibernateUtil
		.withNewSession(new Operation<Object>() {
			@Override
			public Object ex(final Session s,
					final Object... args) throws Exception {
				snapshot.setVolumeId(volType.getVolumeId());
				s.saveOrUpdate(snapshot);
				return null;
			}
		});
		
		// attach the volume
		/*logger.debug("Attaching the volume to the DBInstance...");
		final CallStruct attachCall = new CallStruct();
		attachCall.setAc(at);
		final HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("AvailabilityZone", dbInstance.getAvailabilityZone());
		properties.put("InstanceId", dbInstance.getInstanceId());
		properties.put("VolumeId", volType.getVolumeId());
		final String device = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "VirtualDisk",
						dbInstance.getAvailabilityZone() }));
		if(device == null){
			throw RDSQueryFaults.InternalFailure();
		}
		properties.put("Device", device);
		attachCall.setProperties(properties);
		new Volume().attach(attachCall);
		logger.debug("Finished attaching the volume to the DBInstance...");*/

		final String s1 = "{\"DBSnapshotIdentifier\":\"" + snapshot.getDbsnapshotId()
				+ "\"}";
		ChefUtil.createDatabagItem("rds-" + at.getId() + "-" + dbIdentifier,
				"DBSnapshot", s1);

	}

	@Override
	protected boolean isResource() {
		return true;
	}

}
