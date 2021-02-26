package com.oup.integration.brunel.whsconinbound.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("CevaRecadvToSapWhsconRoute")
public class CevaRecadvToSapWhsconRoute extends RouteBuilder {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void configure() throws Exception {

		onException(com.sap.conn.jco.JCoException.class)
			.log(LoggingLevel.ERROR, logger,
				"Failed to Connect to SAP from CevaRecadvToSapWhsconRoute :: ${exception.message}");

		onException(Exception.class)
			.log(LoggingLevel.ERROR, logger,
				"Error Occured in CevaRecadvToSapWhsconRoute :: ${exception.message}");

		from("{{ftp.ceva.sftpPathPlant1}}","{{ftp.ceva.sftpPathPlant2}}")
			.routeId(getClass().getSimpleName())
			.log(LoggingLevel.INFO, logger, 
				"File :: ${header.CamelFileName} collected from ${header.CamelFileParent}")
			.setHeader("InterChangeID", xpath("Recadv/InterchangeSection/InterChangeID/text()"))
			.setHeader("OrderID", xpath("Recadv/OrderHeader/OrderID/text()"))
			
			.setHeader(Exchange.FILE_NAME, simple("{{file.XML.name}}"))
			.wireTap("{{file.XML.backup}}").id("backupXML").end()

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

			.to("xslt:XSLT/RECADV_WHSCON.xslt?saxon=true")
			.log(LoggingLevel.INFO, logger, "Converted RECADV file to WHSCON IDoc")

			.setHeader(Exchange.FILE_NAME, simple("{{file.IDOC.name}}"))
			.wireTap("{{file.IDOC.backup}}").id("backupIDOC").end()

			.to("{{sap.connection.endpoint}}").id("sendToSAP")
			.log(LoggingLevel.INFO, logger, "Sent IDoc to SAP");
	}

}
