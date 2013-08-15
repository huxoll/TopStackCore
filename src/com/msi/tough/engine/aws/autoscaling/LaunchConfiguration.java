package com.msi.tough.engine.aws.autoscaling;

import java.util.Date;

import org.hibernate.Session;

import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.CFType;
import com.msi.tough.cf.autoscaling.LaunchConfiguartionType;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.HibernateUtil.Operation;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.resource.Resource;
import com.msi.tough.model.LaunchConfigBean;
import com.msi.tough.utils.ASUtil;

public class LaunchConfiguration extends BaseProvider {

	public static String TYPE = "AWS::AutoScaling::LaunchConfiguration";

	@Override
	public CFType create0(final CallStruct call) throws Exception {
		return HibernateUtil.withNewSession(new Operation<CFType>() {

			@Override
			public CFType ex(final Session s, final Object... args)
					throws Exception {
				final AccountType ac = call.getAc();
				final String name = call.getName();
				final String blockDeviceMappings = (String) call
						.getProperty("BlockDeviceMappings");
				final String imageId = (String) call.getProperty("ImageId");
				final String instanceType = (String) call
						.getProperty("InstanceType");
				final String kernelId = (String) call.getProperty("KernelId");
				final String keyName = (String) call.getProperty("KeyName");
				final String ramDiskId = (String) call.getProperty("RamDiskId");
				final String securityGroups = (String) call
						.getProperty("SecurityGroups");
				final String userData = (String) call.getProperty("UserData");
				final String chefRoles = (String) call.getProperty("ChefRoles");
				final String databag = (String) call.getProperty("Databag");
				final String waitHookClass = (String) call
						.getProperty("WaitHookClass");

				final LaunchConfigBean launch = new LaunchConfigBean();
				launch.setUserId(ac.getId());
				launch.setCreatedTime(new Date());
				launch.setImageId(imageId);
				launch.setInstType(instanceType);
				launch.setKernel(kernelId);
				launch.setKey(keyName);
				launch.setName(name);
				launch.setRamdisk(ramDiskId);
				launch.setSecGrps(securityGroups);
				launch.setUserData(userData);
				launch.setChefRoles(chefRoles);
				launch.setDatabag(databag);
				launch.setWaitHookClass(waitHookClass);
				s.save(launch);

				final LaunchConfiguartionType ret = new LaunchConfiguartionType();
				ret.setBlockDeviceMappings(blockDeviceMappings);
				ret.setImageId(imageId);
				ret.setCreatedTime(new Date());
				ret.setInstanceType(instanceType);
				ret.setKernelId(kernelId);
				ret.setKeyName(keyName);
				ret.setName(name);
				ret.setRamDiskId(ramDiskId);
				ret.setSecurityGroups(securityGroups);
				ret.setUpdatedTime(new Date());
				ret.setUserData(userData);
				return ret;
			}
		});
	}

	@Override
	public Resource delete0(final CallStruct call) throws Exception {
		HibernateUtil.withNewSession(new Operation<Object>() {

			@Override
			public Object ex(final Session s, final Object... args)
					throws Exception {
				final AccountType ac = call.getAc();
				final String name = call.getPhysicalId();
				final LaunchConfigBean lcb = ASUtil.readLaunchConfig(s,
						ac.getId(), name);
				s.delete(lcb);
				return null;
			}
		});
		return null;
	}
}
