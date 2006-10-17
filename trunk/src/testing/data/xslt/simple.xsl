<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
	xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
	xmlns:http="http://schemas.xmlsoap.org/wsdl/http/"
	xmlns:mime="http://schemas.xmlsoap.org/wsdl/mime/"
	exclude-result-prefixes="wsdl soap http mime">

<xsl:output method="xml" indent="yes"/>
<xsl:param name="methodName" select="''"/>
<xsl:strip-space elements="*" />

<xsl:template match="/">
<result><xsl:value-of select="/root/nodes/node[2]"/></result>
</xsl:template>

</xsl:stylesheet>
