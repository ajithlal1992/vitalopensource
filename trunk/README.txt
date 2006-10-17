VTLS Fedora RSS Service - Version 0.2 (Open Source)

Please be sure you have read the LICENSE.txt and agree to the terms.

VTLS Inc. (http://www.vtls.com), in partnership with ARROW (http://arrow.edu.au)
, has developed software and services on top of Fedora(tm)
(http://www.fedora.info).

The ARROW Project is sponsored as part of the Commonwealth Government's Backing Australia's Ability.

Introduction
------------

RSS is an XML syndication format used to provide structured information for
headlines, notices and content for a wide variety of uses. [1]

The packaged source code and .war (vtls.war) is intended to serve as a base
implementation of the RSS standard and could very well be enhanced further.

[1] - http://rss.softwaregarden.com/aboutrss.html

Audience
--------
Any Fedora(tm) repository administrator/developer looking to provide a basic
RSS feed for changes to the contents of their repository.  

Dependencies
------------
* Fedora(tm) 2.0 or greater (http://www.fedora.info)

  This servlet uses the Fedora ResourceIndex to obtain the object data used
  by the service. The ResourceIndex must be enabled for the RSS feed to work
  correctly.

Installation/Configuration
--------------------------
1) Copy dist/vtls.war file to your WEBAPPS directory of application
   server (e.g. /usr/local/apache-tomcat-5.5.15/webapps).  Depending
   on the server configuration you may have to  unpack the war file
   manually. (jar xvf vtls.war)
3) Edit the WEBAPPS/vtls/WEB-INF/web.xml file and change the included
   parameters as appropriate for your Fedora(tm) installation.

Usage
-----

Connect to the servlet URL to access this service.

Example:

http://[HOSTNAME]:[PORT]/vtls/rss

If you use the Tomcat instance included with Fedora, the URL would be:

http://[HOSTNAME]:8080/vtls/rss

Release Notes
-------------
0.2 - Added text extraction packages. (com.vtls.opensource.text)
0.1 - Initial release.