<?xml version="1.0" encoding="UTF-8"?>
<!--
	
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 $Id: test_definition_xhtml.xsl,v 1.8 2006/03/25 05:51:01 jacekrad Exp $
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
 Purpose: this stylesheet is used to render HTML output from a UTF-X definition
          file.
	
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 Copyright(C) 2004-2005 USQ and others
	
 You may redistribute and/or modify this file under the terms of the
 GNU General Public License v2.
	
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:utfx="http://utfx.org/test-definition" version="1.0">
  <xsl:output method="html"/>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- root template -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="/">
    <html>
      <head>
        <xsl:apply-templates select="//utfx:css"/>
        <style type="text/css">
.utfx-sect {
  background-color:#ABC;
  color:black;
  font-family: Arial;
}
.utfx-test {
  color:black;
  font-family: Arial;
  style="background-color:#CAB";
}
.utfx-source-sect {
  background-color:#ABC;
  color:black;
  width:60%;
  padding-left:5%;
  padding-right:5%;
}
.utfx-expected-sect {
  background-color:#BCA;
  color:black;
  width:60%;
  padding-left:5%;
  padding-right:5%;
  padding-bottom:5%;
}
.utfx-h1 {
	font-family: Arial;
	font-size: 30pt;
	color: black;
}
.utfx-h2 {
  font-family: Arial;
  font-size: 24pt;
  color: black;
}
.utfx-h3 {
  font-family: Arial;
  font-size: 20pt;
  color: black;
}
.utfx-h4 {
  font-family: Arial;
  font-size: 12pt;
  color: black;
}
        </style>
      </head>
      <body bgcolor="#FFFFFF">
        <table cellpadding="5">
          <tbody>
            <tr>
              <th align="left">UTF-X unit tests definition for: </th>
              <td align="left">
                <code>
                  <xsl:value-of select="//utfx:stylesheet/@src"/>
                </code>
              </td>
            </tr>
            <tr>
              <td>
                <a href="http://utf-x.sourceforge.net">UTF-X.SourceForge.net</a>
              </td>
            </tr>
          </tbody>
        </table>
        <hr/>
        <!-- generate a simple table of contents -->
        <xsl:call-template name="toc"/>
        <!-- here we render each test element -->
        <xsl:for-each select="//utfx:test">
          <div class="utfx-test">
            <a>
              <xsl:attribute name="name">
                <xsl:text>test</xsl:text>
                <xsl:number value="position()" format="1"/>
              </xsl:attribute>
            </a>
            <h1 class="utfx-h1">
              <xsl:number value="position()" format="1"/>
              <xsl:text>. </xsl:text>
              <xsl:value-of select="utfx:name"/>
            </h1>
            <xsl:apply-templates/>
          </div>
        </xsl:for-each>
      </body>
    </html>
  </xsl:template>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- utfx:css -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="utfx:css">
    <link rel="stylesheet" type="text/css">
      <xsl:attribute name="href">
        <xsl:value-of select="./@uri"/>
      </xsl:attribute>
    </link>
  </xsl:template>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- test template -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="utfx:test">
    <xsl:apply-templates/>
  </xsl:template>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- name template -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="utfx:name"/>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- assert equals template -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="utfx:assert-equal">
    <xsl:apply-templates/>
  </xsl:template>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- source template -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="utfx:source">
    <div class="utfx-source-sect">
      <h2 class="utfx-h2">Source:</h2>
      <xsl:if test="./@validate='yes' and (//utfx:source-validation != '')">
        <h4 class="utfx-h4">
          <xsl:text>this source fragment will be validated using </xsl:text>
          <code>PUBLIC <xsl:value-of select="//utfx:source-validation/utfx:dtd/@public"/>
          </code>
          <code> "<xsl:value-of select="//utfx:source-validation/utfx:dtd/@system"/>"</code>
        </h4>
      </xsl:if>
      <pre style="background-color:#FFF">
        <code>
          <xsl:apply-templates/>
        </code>
      </pre>
    </div>
  </xsl:template>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- expected template -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="utfx:expected">
    <div class="utfx-expected-sect">
      <h2 class="utfx-h2">Expected:</h2>
      <xsl:if test="./@validate='yes' and (//utfx:expected-validation != '')">
        <h4 class="utfx-h4">
          <xsl:text>this expected fragment will be validated using </xsl:text>
          <code>PUBLIC <xsl:value-of select="//utfx:expected-validation/utfx:dtd/@public"/>
          </code>
          <code> "<xsl:value-of select="//utfx:expected-validation/utfx:dtd/@system"/>"</code>
        </h4>
      </xsl:if>
    </div>
    <xsl:copy-of select="."/>
  </xsl:template>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- message template -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="utfx:message">
    <xsl:text>Message when failed: </xsl:text>
    <font color="red">
      <xsl:value-of select="."/>
    </font>
  </xsl:template>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- named template to create a simple table of contents -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template name="toc">
    <div class="utfx-sect">
    <xsl:if test="//utfx:source-validation != ''">
      <h2 class="utfx-h2">Source fragment validation</h2>
      <code>PUBLIC: <xsl:value-of select="//utfx:source-validation/utfx:dtd/@public"/>
      </code>
      <br/>
      <code>SYSTEM: <xsl:value-of select="//utfx:source-validation/utfx:dtd/@system"/>
      </code>
    </xsl:if>
    <xsl:if test="//utfx:expected-validation != ''">
      <h2 class="utfx-h2">Expected fragment validation</h2>
      <code>PUBLIC: <xsl:value-of select="//utfx:expected-validation/utfx:dtd/@public"/>
      </code>
      <br/>
      <code>SYSTEM: <xsl:value-of select="//utfx:expected-validation/utfx:dtd/@system"/>
      </code>
    </xsl:if>
    <h2 class="utfx-h2">List of tests</h2>
    <table border="1" cellpadding="2">
      <tbody>
        <!--table header -->
        <tr>
          <th>Num.</th>
          <th>Test name</th>
          <th>Validate source</th>
          <th>Validate expected</th>
        </tr>
        <xsl:for-each select="//utfx:test">
          <tr>
            <!-- test number -->
            <th align="right">
              <!-- create a link to each test -->
              <a>
                <xsl:attribute name="href">
                  <xsl:text>#test</xsl:text>
                  <xsl:number value="position()" format="1"/>
                </xsl:attribute>
                <xsl:number value="position()" format="1"/>
              </a>
            </th>
            <!-- test name -->
            <td align="left">
              <xsl:value-of select="./utfx:name"/>
            </td>
            <!-- is source validated -->
            <td>
              <xsl:choose>
                <xsl:when test=".//utfx:source/@validate = 'no'">
                  <font color="red">NO</font>
                </xsl:when>
                <xsl:otherwise>
                  <font color="green">YES</font>
                </xsl:otherwise>
              </xsl:choose>
            </td>
            <!-- which expected fragments are validated -->
            <td>
              <xsl:choose>
                <xsl:when test=".//utfx:expected/@validate = 'no'">
                  <font color="red">NO</font>
                </xsl:when>
                <xsl:otherwise>
                  <font color="green">YES</font>
                </xsl:otherwise>
              </xsl:choose>
            </td>
          </tr>
        </xsl:for-each>
      </tbody>
    </table>
    </div>
  </xsl:template>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- all other elements i.e utfx:source and utfx:expected content -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="*">
    <font color="#1111BB">&lt;<xsl:value-of select="local-name()"/>
    </font>
    <xsl:choose>
      <xsl:when test="count(attribute::*) = 0">
        <font color="blue">&gt;</font>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="attribute::*">
          <xsl:text> </xsl:text>
          <font color="#CC6633">
            <xsl:value-of select="local-name()"/>
          </font>
          <xsl:text>='</xsl:text>
          <font color="#881111">
            <xsl:value-of select="."/>
          </font>
          <xsl:text>'</xsl:text>
        </xsl:for-each>
        <font color="blue">&gt;</font>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates/>
    <font color="1111BB">&lt;/<xsl:value-of select="local-name()"/>&gt;</font>
  </xsl:template>
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <!-- text template -->
  <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>
</xsl:stylesheet>
