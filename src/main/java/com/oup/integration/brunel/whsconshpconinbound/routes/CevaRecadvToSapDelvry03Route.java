package com.oup.integration.brunel.whsconshpconinbound.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("CevaRecadvToSapDelvry03Route")
public class CevaRecadvToSapDelvry03Route extends RouteBuilder {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void configure() throws Exception {

		onException(com.sap.conn.jco.JCoException.class)
			.handled(true)
			.log(LoggingLevel.ERROR, logger,
				"Failed to Connect to SAP from CevaRecadvToSapWhsconRoute :: ${exception.message} \n${exception.stacktrace}");

		onException(Exception.class)
			.handled(true)
			.log(LoggingLevel.ERROR, logger,
				"Error Occured in CevaRecadvToSapWhsconRoute :: ${exception.message} \n${exception.stacktrace}");

		from("{{ftp.ceva.sftpPathPlant1}}","{{ftp.ceva.sftpPathPlant2}}")
			.routeId(getClass().getSimpleName())
			.log(LoggingLevel.INFO, logger, 
				"File :: ${header.CamelFileName} collected from ${header.CamelFileParent}")
			.setHeader("InterChangeID", xpath("Recadv/InterchangeSection/InterChangeID/text()"))
			.setHeader("OrderID", xpath("Recadv/OrderHeader/OrderID/text()"))
			.setHeader("SubInventory", xpath("Recadv/OrderLine[1]/SubInventory/text()"))
			.log(LoggingLevel.INFO, logger, "File : ${header.CamelFileName} InterChange ID : ${header.InterChangeID}  Order ID: ${header.OrderID}")
			
			.setHeader(Exchange.FILE_NAME, simple("{{file.XML.name}}"))
			.wireTap("{{file.XML.backup}}").id("backupXML").end()

			.setHeader("idocType", simple("{{sap.connection.idocType}}"))
			.choice()
				.when()
					.simple("${header.SubInventory} == 'RET'")
					.log(LoggingLevel.INFO, logger, "SubInventory code for ${file:name} is RET. MessageType will be SHPCON")
					.setHeader("messageType", simple("SHPCON"))
				.when()
					.simple("${header.SubInventory} == 'STN'")
					.log(LoggingLevel.INFO, logger, "SubInventory code for ${file:name} is STN. MessageType will be WHSCON")
					.setHeader("messageType", simple("WHSCON"))
				.otherwise()
					.throwException(Exception.class, "Incorrect SubInventory code in file ${header.CamelFileName}")
			.end()
			.setHeader("r3name", simple("{{sap.connection.r3name}}"))
			.setHeader("recipientPort", simple("{{sap.connection.recipientPort}}"))
			.setHeader("recipientPartnerNumber", simple("{{sap.connection.recipientPartnerNumber}}"))
			.setHeader("recipientPartnerType", simple("LS"))
			.setHeader("senderPort", simple("{{sap.connection.senderPort}}"))
			.setHeader("senderPartnerNumber", simple("{{sap.connection.senderPartnerNumber}}"))
			.setHeader("senderPartnerType", simple("LS"))
			.setHeader("client", simple("{{sap.connection.client}}"))

			.to("xslt:XSLT/RECADV_DELVRY03.xslt?saxon=true")
			.log(LoggingLevel.INFO, logger, "Converted RECADV file to ${header.messageType} IDoc")

			.setHeader(Exchange.FILE_NAME, simple("{{file.IDOC.name}}"))
			.wireTap("{{file.IDOC.backup}}").id("backupIDOC").end()

			.to("{{sap.connection.endpoint}}").id("sendToSAP")
			.log(LoggingLevel.INFO, logger, "Sent ${header.messageType} IDoc to SAP");
	}

}
