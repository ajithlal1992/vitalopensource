<?xml version="1.0" encoding="UTF-8"?>
<!-- Stylesheet for NHSiS Schemas - David Brazier 17/11/00
     Work in progress, free for use as-is.
     (c) Copyright Common Services Agency 2000 -->
<xsl:stylesheet version="1.0"
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xsd="http://www.w3.org/2000/10/XMLSchema">
	<xsl:strip-space elements="*"/>
	<xsl:template match="xsd:schema">
		<html>
			<head>
				<title>Schema: <xsl:value-of select="@targetNamespace"/>
				</title>
			</head>
			<style>
   table { border-collapse: collapse }
   td { vertical-align: top }
   </style>
			<!-- for testing:
   td { border: 1px solid blue; vertical-align: top }
-->
			<body>
				<h1>Schema: <xsl:value-of select="@targetNamespace"/>
				</h1>
				<p>Version: <xsl:value-of select="@version"/>
				</p>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="xsd:import">
		<hr/>
		<i>Import Schema <xsl:value-of select="@namespace"/> from 
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="@schemaLocation"/>
			</xsl:attribute>
			<xsl:value-of select="@schemaLocation"/>
		</a>
		</i>
	</xsl:template>
	<xsl:template match="xsd:element">
		<xsl:if test="..=/">
			<hr/>
		</xsl:if>
		<table>
			<tr>
				<td colspan="2">
					<b>
						<xsl:value-of select="@name"/>
					</b>
					&#32;
					<a>
						<xsl:variable name="prefix" select="substring-before(@type,':')"/>
						<xsl:variable name="ns" select="namespace::*[substring-after(name(.),':')=$prefix]"/>
						<xsl:choose>
							<xsl:when test="$ns=/xsd:schema/@targetNamespace">
								<xsl:attribute name="href">#<xsl:value-of select="substring-after(@type,':')"/></xsl:attribute>
							</xsl:when>
							<xsl:when test="/xsd:schema/xsd:import[@namespace=$ns]">
								<xsl:attribute name="href"><xsl:value-of select="/xsd:schema/xsd:import[@namespace=$ns]/@schemaLocation"/>#<xsl:value-of select="substring-after(@type,':')"/></xsl:attribute>
							</xsl:when>
						</xsl:choose>
						<xsl:value-of select="substring-after(@type,':')"/>
					</a>
					(<xsl:choose>
						<xsl:when test="@minOccurs"><xsl:value-of select="@minOccurs"/></xsl:when>
						<xsl:otherwise>1</xsl:otherwise>
					</xsl:choose>-<xsl:choose>
						<xsl:when test="@maxOccurs"><xsl:value-of select="@maxOccurs"/></xsl:when>
						<xsl:otherwise>1</xsl:otherwise>
					</xsl:choose>)
				</td>
			</tr>
			<tr>
				<td width="25"/>
				<td>
					<xsl:apply-templates select="*"/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="xsd:simpleType">
		<xsl:if test="..=/">
			<hr/>
		</xsl:if>
		<table>
			<xsl:attribute name="id">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<tr>
				<td colspan="2">
					<b>
						<xsl:value-of select="@name"/>
					</b>
					&#32;<xsl:value-of select="@type"/>
				</td>
			</tr>
			<tr>
				<td width="25"/>
				<td>
					<xsl:apply-templates select="*"/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="xsd:complexType">
		<xsl:if test="..=/">
			<hr/>
		</xsl:if>
		<table>
			<xsl:attribute name="id">
				<xsl:value-of select="@name"/>
			</xsl:attribute>
			<tr>
				<td colspan="2">
					<b>
						<xsl:value-of select="@name"/>
					</b>
					&#32;<xsl:value-of select="@type"/>
				</td>
			</tr>
			<tr>
				<td width="25"/>
				<td>
					<xsl:apply-templates select="*"/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="xsd:sequence">
		<xsl:if test="..=/">
			<hr/>
		</xsl:if>
		<table>
			<tr>
				<td width="25" style="border-right: 1px solid black"/>
				<td>
					<xsl:apply-templates select="*"/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="xsd:choice">
		<xsl:if test="..=/">
			<hr/>
		</xsl:if>
		<table>
			<tr>
				<td width="25" style="vertical-align: middle; border-right: double black">
				choice
				</td>
				<td>
					<xsl:apply-templates select="*"/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="xsd:restriction">
		<xsl:value-of select="substring-after(@base,':')"/>&#32;
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="xsd:minLength">
		<xsl:value-of select="@value"/> ..
	</xsl:template>
	<xsl:template match="xsd:maxLength">
		<xsl:value-of select="@value"/>
	</xsl:template>
	<xsl:template match="xsd:enumeration">
		"<xsl:value-of select="@value"/>"
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="xsd:pattern">
		<xsl:value-of select="@value"/>
		<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="xsd:annotation">
		<xsl:for-each select="xsd:documentation">
			<small>
				<xsl:value-of select="."/>
			</small>
			<br/>
		</xsl:for-each>
		<xsl:for-each select="xsd:appinfo">
			<small>
				<i>
					<xsl:value-of select="."/>
				</i>
			</small>
			<br/>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
