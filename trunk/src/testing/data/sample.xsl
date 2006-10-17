<?xml version="1.0"?> 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:strip-space elements="*"/>
	
<xsl:output method="html" encoding="utf-8" indent="yes"/>

  <xsl:template name="element">
    <xsl:param name="indent" select="0"/>
    <xsl:text>
</xsl:text>
    <xsl:choose>
      <xsl:when test="self::text()">
        <div class="text" style="margin-left:{$indent * 16}px;">
          <code><xsl:value-of select="normalize-space(.)"/></code>
        </div>
      </xsl:when>
      <xsl:when test="self::*">
        <div class="element" style="margin-left:{$indent * 16}px;">
          <code>
				<xsl:value-of select="concat('&lt;',name(.))"/>
				<xsl:for-each select="./@*"><xsl:text> </xsl:text><xsl:value-of select="name(.)"/>="<xsl:value-of select="."/>"</xsl:for-each>
			 	<xsl:value-of select="concat('','&gt;')"/>
			 </code>
        </div>
        <xsl:for-each select="node()">
          <xsl:call-template name="element">
            <xsl:with-param name="indent">
              <xsl:value-of select="$indent + 1"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:for-each>              
        <xsl:text>
</xsl:text>
        <div class="element" style="margin-left:{$indent * 16}px;">
				<code><xsl:value-of select="concat('&lt;/',name(.),'&gt;')"/></code>
        </div>
      </xsl:when>
    </xsl:choose>      
  </xsl:template>

  <xsl:template match="/">
		<div id="xml_view">
          <xsl:for-each select="node()">
            <xsl:call-template name="element">
              <xsl:with-param name="indent">
                0
              </xsl:with-param>
            </xsl:call-template>
          </xsl:for-each>              
	</div>
  </xsl:template>

</xsl:stylesheet>

