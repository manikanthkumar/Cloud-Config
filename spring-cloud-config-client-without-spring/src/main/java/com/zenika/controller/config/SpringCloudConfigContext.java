package com.zenika.controller.config;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class SpringCloudConfigContext extends XmlWebApplicationContext {
	@Override
	protected ConfigurableEnvironment createEnvironment() {
		System.out.println("##################### loaded my comfigurable context");
		return new CloudEnvironment();
	}
}
