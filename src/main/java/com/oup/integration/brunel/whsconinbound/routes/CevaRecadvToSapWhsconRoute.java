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
		
		.setHeader("idocType", simple("{{sap.connection.idocType}}"))
		.setHeader("messageType", simple("{{sap.connection.messageType}}"))
		.setHeader("r3name", simple("{{sap.connection.r3name}}"))
		.setHeader("recipientPort", simple("{{sap.connection.recipientPort}}"))
		.setHeader("recipientPartnerNumber", simple("{{sap.connection.recipientPartnerNumber}}"))
		.setHeader("recipientPartnerType", simple("LS"))
		.setHeader("senderPort", simple("{{sap.connection.senderPort}}"))
		.setHeader("senderPartnerNumber", simple("{{sap.connection.senderPartnerNumber}}"))
		.setHeader("senderPartnerType", simple("LS"))
		.setHeader("client", simple("{{sap.connection.client}}"))
		
		.log("${body}")
		.to("xslt:XSLT/RECADV_WHSCON.xslt?saxon=true")
//		.to("file:outbox")
		.to("{{sap.connection.endpoint}}")
		.log("${body}");
	}

}
