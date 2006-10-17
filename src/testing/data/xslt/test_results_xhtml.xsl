<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <!-- root template -->
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <!-- root template -->
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <xsl:template match="regression-test">
        <html>
            <head>
                <title>UTF-X Test Results</title>
                <style type="text/css">
.suite-name {
  color: purple;
  font-family: Courier;
}
.test-name {
  color: cyan;
  font-family: Courier;
}
.failure {
  color: red;
}
.error {
  color: yellow;
}
.same-actual {
    color: green;
    font-family: Courier;
}
.same-expected {
    color: yellow;
    font-family: Courier;
}
.diff {
    color: red;
    font-family: Courier;
}

</style>
            </head>
            <body bgcolor="#AAAAAA">
                <div style="font-family: arial">
                    <xsl:apply-templates select="./test-summary"/>
                    <xsl:apply-templates select="./test-suite"/>
                </div>
            </body>
        </html>
    </xsl:template>
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <!-- root template -->
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <xsl:template match="test-suite">
        <table border="0" width="100%">
            <tbody>
                <tr>
                    <td>&#160;</td>
                    <td>
                        <font color="purple">Suite '<xsl:value-of select="./suite-name"/>' has
                                <xsl:value-of select="./test-count"/> tests/suites</font>
                    </td>
                </tr>
                <tr>
                    <td>&#160;</td>
                    <td>
                        <xsl:apply-templates/>
                    </td>
                </tr>
            </tbody>
        </table>
    </xsl:template>
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <!-- root template -->
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <xsl:template match="test">
        <table border="0">
            <tbody>
                <tr>
                    <td>&#160;</td>
                    <td>
                        <font color="purple">
                            <xsl:value-of select="../suite-name"/>.</font>
                        <font color="cyan">
                            <xsl:value-of select="./test-name"/>
                        </font>
                    </td>
                    <td>
                        <xsl:choose>
                            <xsl:when test="count(error) != 0">
                                <font color="yellow">[<xsl:value-of select="./error"/>]</font>
                            </xsl:when>
                            <xsl:when test="count(failure) != 0">
                                <font color="red">[<xsl:value-of select="./failure"/>]</font>
                            </xsl:when>
                            <xsl:otherwise>
                                <font color="green">[OK]</font>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
            </tbody>
        </table>
    </xsl:template>
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <!-- root template -->
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <xsl:template match="test-summary">
        <table>
            <tbody>
                <tr>
                    <th>Elapsed time:</th>
                    <td>
                        <xsl:value-of select=".//run-time"/> ms</td>
                </tr>
                <tr>
                    <th>Test count:</th>
                    <td>
                        <xsl:value-of select=".//run-count"/>
                    </td>
                </tr>
            </tbody>
        </table>
        <div class="failer">There were <xsl:value-of select="./test-summary-header/failure-count"/>
            failures.</div>

        <xsl:apply-templates select=".//test-failure"/>

        <div class="error"> There were <xsl:value-of select="./test-summary-header/error-count"/>
            errors.</div>

        <xsl:apply-templates select=".//test-error"/>

    </xsl:template>
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <!-- test-failure template -->
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <xsl:template match="test-failure">
        <table>
            <tbody>
                <tr>
                    <td class="failure">[<xsl:value-of select="@count"/>]</td>
                    <td class="test-name">
                        <xsl:value-of select="./test-name"/>
                    </td>
                    <td class="failure">
                        <xsl:value-of select="./message"/>
                    </td>
                </tr>
            </tbody>
        </table>
        <xsl:if test="count(string-comparison-failure) != 0">
            <table>
                <tbody>
                    <tr>
                        <td>actual:</td>
                    </tr>
                    <tr>
                        <td class="same-actual">
                            <xsl:value-of select=".//same-content"/>
                        </td>
                        <td class="diff">
                            <xsl:value-of select=".//diff-actual"/>
                        </td>
                    </tr>
                </tbody>
            </table>
            <table>
                <tbody>
                    <tr>
                        <td>expected:</td>
                    </tr>
                    <tr>
                        <td class="same-expected">
                            <xsl:value-of select=".//same-content"/>
                        </td>
                        <td class="diff">
                            <xsl:value-of select=".//diff-expected"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </xsl:if>
        <p/>
    </xsl:template>
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <!-- test-error template -->
    <!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
    <xsl:template match="test-error">
        <table>
            <tbody>
                <tr>
                    <td class="error">[<xsl:value-of select="@count"/>]</td>
                    <td class="test-name">
                        <xsl:value-of select="./test-name"/>
                    </td>
                    <td class="error">
                        <xsl:value-of select="./message"/>
                    </td>
                </tr>
            </tbody>
        </table>
        <code>
            <pre>
        <xsl:value-of select="stack-trace"/>
        </pre>
        </code>
        <p/>

    </xsl:template>

    <xsl:template match="text()">
        <!-- ignore -->
    </xsl:template>
</xsl:stylesheet>
