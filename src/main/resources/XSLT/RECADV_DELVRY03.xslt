<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xs xsl" version="2.0">
	<xsl:output omit-xml-declaration="no" indent="yes" />
	<xsl:param name="idocType" />
	<xsl:param name="messageType" />
	<xsl:param name="r3name" />
	<xsl:param name="recipientPort" />
	<xsl:param name="recipientPartnerNumber" />
	<xsl:param name="recipientPartnerType" />
	<xsl:param name="senderPort" />
	<xsl:param name="senderPartnerNumber" />
	<xsl:param name="senderPartnerType" />
	<xsl:param name="client" />
	<xsl:variable name="type"
		select="concat('http://sap.fusesource.org/idoc/', $r3name, '/', $idocType, '///')" />
	<xsl:template match="Recadv">
		<idoc:Document
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xmlns:idoc="http://sap.fusesource.org/idoc" client="{$client}"
			creationDate="{current-dateTime()}"
			creationTime="{current-dateTime()}" iDocType="{$idocType}"
			iDocTypeExtension="" messageType="{$messageType}"
			recipientPartnerNumber="{$recipientPartnerNumber}"
			recipientPartnerType="{$recipientPartnerType}"
			recipientPort="{$recipientPort}"
			senderPartnerNumber="{$senderPartnerNumber}"
			senderPartnerType="{$senderPartnerType}" senderPort="{$senderPort}">
			<xsl:namespace name="{$idocType}---">
				<xsl:value-of select="$type" />
			</xsl:namespace>
			<rootSegment xsi:type="{$idocType}---:ROOT" document="/">
				<segmentChildren parent="//@rootSegment">
					<E1EDL20 parent="//@rootSegment" document="/"
						VBELN="{OrderHeader/OrderID}" BOLNR="R33836">
						<segmentChildren
							parent="//@rootSegment/@segmentChildren/@E1EDL20.0">
							<xsl:if test="$messageType='WHSCON'">
								<E1EDL18
									parent="//@rootSegment/@segmentChildren/@E1EDL20.0" document="/"
									QUALF="PIC" />
							</xsl:if>
							<E1EDL18
								parent="//@rootSegment/@segmentChildren/@E1EDL20.0" document="/"
								QUALF="PGI" />
							<E1EDT13
								parent="//@rootSegment/@segmentChildren/@E1EDL20.0" document="/"
								QUALF="010" NTANF="{current-dateTime()}"
								NTANZ="{current-dateTime()}" />
							<xsl:for-each select="OrderLine">
								<E1EDL24
									parent="//@rootSegment/@segmentChildren/@E1EDL20.0"
									document="/" POSNR="{LineId}" MATNR="{ProductId}"
									WERKS="{LocationID}" LGORT="{OrganizationReference}"
									LFIMG="{QuantityReceived}" VRKME="{UOM}"
									VGBEL="{../OrderHeader/OriginalOrderReference}"
									VGPOS="{OriginalLineId}">
									<xsl:if test="QuantityExpected">
										<xsl:attribute name="ORMNG"
											select="QuantityExpected" />
									</xsl:if>
								</E1EDL24>
							</xsl:for-each>
						</segmentChildren>
					</E1EDL20>
				</segmentChildren>
			</rootSegment>
		</idoc:Document>
	</xsl:template>
</xsl:stylesheet>