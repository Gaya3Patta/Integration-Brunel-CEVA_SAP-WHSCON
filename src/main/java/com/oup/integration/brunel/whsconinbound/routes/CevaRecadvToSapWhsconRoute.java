package com.oup.integration.brunel.whsconinbound.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component("CevaRecadvToSapWhsconRoute")
public class CevaRecadvToSapWhsconRoute extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		from("timer:timerName?repeatCount=1")
		.routeId(getClass().getSimpleName())
		.pollEnrich("file:inbox?noop=true&idempotent=false")
		
		.setHeader("idocType", constant("DELVRY03"))
		.setHeader("messageType", constant("WHSCON"))
		.setHeader("r3name", constant("R3Q"))
		.setHeader("recipientPort", constant("SAPR3Q"))
		.setHeader("recipientPartnerNumber", constant("CEVA_INDEL"))
		.setHeader("recipientPartnerType", constant("LS"))
		.setHeader("senderPort", constant("CEVA_INB"))
		.setHeader("senderPartnerNumber", constant("DISPATCHER"))
		.setHeader("senderPartnerType", constant("LS"))
		.setHeader("client", constant("10"))
		
		.log("${body}")
		.to("xslt:XSLT/RECADV_WHSCON.xslt?saxon=true")
//		.to("file:outbox")
		.to("{{sap.connection.endpoint}}")
		.log("${body}");
	}

}
