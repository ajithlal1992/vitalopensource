<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:marc="http://www.loc.gov/MARC21/slim">
  <xsl:output encoding="utf-8" method="xml"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="/">
    <oai_dc:dc xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/">
      <xsl:apply-templates select="marc:collection/marc:record"/>
      <xsl:apply-templates select="marc:record"/>
    </oai_dc:dc>
  </xsl:template>
  
  <xsl:template match="marc:record">
    <!-- Title -->
    <xsl:choose>
      <xsl:when test="marc:datafield[@tag='245']/marc:subfield[@code='a'] != ''">
        <xsl:for-each select="marc:datafield[@tag='245' and marc:subfield[@code='a']][1]">
          <dc:title>
            <xsl:apply-templates select="marc:subfield[@code='a']"/>
          </dc:title>
        </xsl:for-each>
      </xsl:when>
      <!--<xsl:otherwise>				<dc:title>No Title.<dc:title>
		</xsl:otherwise>-->
    </xsl:choose>

    <!-- Subject -->
    <xsl:choose>
      <xsl:when test="marc:datafield[@tag='650']/marc:subfield[@code='a']">        <xsl:for-each select="marc:datafield[@tag='650' and marc:subfield[@code='a']]">
            <dc:subject>
	          <xsl:apply-templates select="marc:subfield[@code='a']"/>
            </dc:subject>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>

    <!-- Author -->
    <xsl:choose>
      <xsl:when test="marc:datafield[@tag='100']/marc:subfield[@code='a']">        <xsl:for-each select="marc:datafield[@tag='100' and marc:subfield[@code='a']]">
            <dc:creator>
		        <xsl:apply-templates select="marc:subfield[@code='a']"/>
				<xsl:if test="marc:subfield/@code='d'">
					<xsl:apply-templates select="marc:subfield[@code='d']"/>
				</xsl:if>
            </dc:creator>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>

    <!-- Description -->
    <xsl:choose>
      <xsl:when test="marc:datafield[@tag='500']/marc:subfield[@code='a']">        <xsl:for-each select="marc:datafield[@tag='500' and marc:subfield[@code='a']]">
            <dc:description>
	          <xsl:apply-templates select="marc:subfield[@code='a']"/>
            </dc:description>
        </xsl:for-each>
      </xsl:when>
    </xsl:choose>
    
    <!-- Material Type -->
    <xsl:if test="substring(marc:leader, 7, 1)!=' '">
      <dc:type>
        <xsl:choose>
          <xsl:when test="substring(marc:leader, 7, 1)='a'">Printed material</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='c'">Printed music</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='d'">Manuscript music</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='e'">Printed map</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='f'">Manuscript map</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='g'">Motion picture, videorecording, filmstrip, slide, or transparency</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='i'">Non-musical recording</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='j'">Musical recording</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='k'">2-D Non-projected Material</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='m'">Computer media</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='o'">Kit</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='p'">Mixed materials</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='r'">Artifact or naturally occurring object</xsl:when>
          <xsl:when test="substring(marc:leader, 7, 1)='t'">Manuscript</xsl:when>
          <xsl:otherwise>Unknown</xsl:otherwise>
        </xsl:choose>
      </dc:type>
    </xsl:if>

  </xsl:template>
</xsl:stylesheet>
