package com.oup.integration.brunel.whsconinbound.configurations;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.fusesource.camel.component.sap.CurrentProcessorDefinitionInterceptStrategy;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.ServerData;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan
public class AppConfig {
	
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	@Value("${sap.connection.client}")
	private String client;
	@Value("${sap.connection.user}")
	private String user;
	@Value("${sap.connection.passwd}")
	private String passwd;
	@Value("${sap.connection.lang}")
	private String lang;
	@Value("${sap.connection.group}")
	private String group;
	@Value("${sap.connection.mshost}")
	private String mshost;
	@Value("${sap.connection.msserv}")
	private String msserv;
	@Value("${sap.connection.r3name}")
	private String r3name;
	@Value("${sap.connection.gwhost}")
	private String gwhost;
	@Value("${sap.connection.gwserv}")
	private String gwserv;
	@Value("${sap.connection.progid}")
	private String progid;
	@Value("${sap.connection.connectionCount}")
	private String connectionCount;
	
	@Bean
	CamelContextConfiguration contextConfiguration() {
		return new CamelContextConfiguration() {
			@Override
			public void beforeApplicationStart(CamelContext context) {
				//beforeApplicationStart
			}

			@Override
			public void afterApplicationStart(CamelContext camelContext) {
				//afterApplicationStart
			}
		};
	}

	
	@Bean
	public CurrentProcessorDefinitionInterceptStrategy getCurrentProcessorDefinitionInterceptor()
	{
		return new CurrentProcessorDefinitionInterceptStrategy();
	}
	
	//  Configures an Inbound SAP Connection
	@Bean(name= "sapInDestination")
	public DestinationDataImpl quickstartDestConfig() {
		DestinationDataImpl impl = new DestinationDataImpl();
		impl.setClient(client);
		impl.setUser(user);
		impl.setPasswd(passwd);
		impl.setLang(lang);
		impl.setGroup(group);
		impl.setMshost(mshost);
		impl.setMsserv(msserv);
		impl.setR3name(r3name);
		return impl;
	}
	
	//  Configures an Outbound SAP Connection
	@Bean(name = "sapukServer")
	public ServerDataImpl quickstartServerDataImplConfig() {
		ServerDataImpl impl = new ServerDataImpl();
		impl.setGwhost(gwhost);
		impl.setGwserv(gwserv);
		impl.setProgid(progid);
		impl.setConnectionCount(connectionCount);
		impl.setRepositoryDestination("quickstartDest");
		return impl;
	}
	
	@Bean(name = "sap-configuration")
	public org.fusesource.camel.component.sap.SapConnectionConfiguration camelSapConnectionConfiguration(@Qualifier("sapInDestination") DestinationDataImpl ddi, @Qualifier("sapukServer") ServerDataImpl sdsi) {
		org.fusesource.camel.component.sap.SapConnectionConfiguration scc = new org.fusesource.camel.component.sap.SapConnectionConfiguration();
		Map<String, DestinationData> destinationData = new HashMap<>();
		destinationData.put("sapInDestination", ddi);
		scc.setDestinationDataStore(destinationData);
		Map<String, ServerData> destinationServer = new HashMap<>();
		destinationServer.put("sapukServer", sdsi);
		scc.setServerDataStore(destinationServer);
		return scc;
	}


}
