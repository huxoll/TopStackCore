package com.msi.tough.utils;

import java.util.List;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class FTLGetConfiguration implements TemplateMethodModel {

	@Override
	public Object exec(List args) throws TemplateModelException {
		return ConfigurationUtil.getConfiguration(args);
	}
}
