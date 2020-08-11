package org.silentsoft.oss;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.silentsoft.oss.license.ApacheLicense2_0;
import org.silentsoft.oss.license.BCLLicense;
import org.silentsoft.oss.license.BSD2ClauseLicense;
import org.silentsoft.oss.license.BSD3ClauseLicense;
import org.silentsoft.oss.license.EPL1_0License;
import org.silentsoft.oss.license.JSONLicense;
import org.silentsoft.oss.license.LGPL3_0License;
import org.silentsoft.oss.license.MITLicense;

public class NoticeFileTest {

	private static final License APACHE_LICENSE_2_0 = new ApacheLicense2_0();
	private static final License BSD_2_CLAUSE = new BSD2ClauseLicense();
	private static final License BSD_3_CLAUSE = new BSD3ClauseLicense();
	private static final License JSON_LICENSE = new JSONLicense();
	private static final License EPL_1_0 = new EPL1_0License();
	private static final License LGPL_3_0 = new LGPL3_0License();
	private static final License MIT_LICENSE = new MITLicense();
	private static final License BCL_LICENSE = new BCLLicense();
	
	@Test
	public void noticeFileTest() throws Exception {
		String markdown = generateActlistNoticeMarkdown();
		
		System.out.println("--------START OF THE NOTICE FILE--------");
		System.out.println(markdown);
		System.out.println("---------END OF THE NOTICE FILE---------");
		
		Assert.assertEquals(markdown, readFile());
	}
	
	private String generateActlistNoticeMarkdown() {
		return NoticeFileGenerator.newInstance("Actlist", "silentsoft.org")
			.addText("This product includes software developed by silentsoft.org.")
			.addText("This product includes software developed by The Apache Software Foundation (http://www.apache.org/).")
			.addLibrary("material icons", "https://material.io/icons/", APACHE_LICENSE_2_0)
			.addLibrary("actlist-plugin 2.1.0", "https://github.com/silentsoft/actlist-plugin", APACHE_LICENSE_2_0)
			.addLibrary("centerdevice-nsmenufx 2.1.5", "https://github.com/codecentric/NSMenuFX", BSD_3_CLAUSE)
			.addLibrary("commonmark 0.13.1", "https://github.com/atlassian/commonmark-java", BSD_2_CLAUSE)
			.addLibrary("commons-codec 1.11", "https://github.com/apache/commons-codec", APACHE_LICENSE_2_0)
			.addLibrary("commons-daemon 1.0.10", "https://github.com/apache/commons-daemon", APACHE_LICENSE_2_0)
			.addLibrary("commons-io 2.4", "https://github.com/apache/commons-io", APACHE_LICENSE_2_0)
			.addLibrary("commons-lang 2.6", "https://github.com/apache/commons-lang", APACHE_LICENSE_2_0)
			.addLibrary("commons-logging 1.2", "https://github.com/apache/commons-logging", APACHE_LICENSE_2_0)
			.addLibrary("commons-net 3.3", "https://github.com/apache/commons-net", APACHE_LICENSE_2_0)
			.addLibrary("controlsfx 8.40.16", "https://github.com/controlsfx/controlsfx", BSD_3_CLAUSE)
			.addLibrary("github-markdown-css 4.0.0", "https://github.com/sindresorhus/github-markdown-css", MIT_LICENSE)
			.addLibrary("httpclient 4.5.12", "https://github.com/apache/httpcomponents-client", APACHE_LICENSE_2_0)
			.addLibrary("httpcore 4.4.13", "https://github.com/apache/httpcomponents-core", APACHE_LICENSE_2_0)
			.addLibrary("httpmime 4.5.12", "", APACHE_LICENSE_2_0)
			.addLibrary("ini4j 0.5.4", "https://github.com/michaelPf85/ini4j", APACHE_LICENSE_2_0)
			.addLibrary("jackson-annotations 2.10.3", "https://github.com/FasterXML/jackson-annotations", APACHE_LICENSE_2_0)
			.addLibrary("jackson-core 2.10.3", "https://github.com/FasterXML/jackson-core", APACHE_LICENSE_2_0)
			.addLibrary("jackson-databind 2.10.3", "https://github.com/FasterXML/jackson-databind", APACHE_LICENSE_2_0)
			.addLibrary("javassist 3.26.0-GA", "https://github.com/jboss-javassist/javassist", APACHE_LICENSE_2_0)
			.addLibrary("jfoenix 8.0.8", "https://github.com/jfoenixadmin/Jfoenix", APACHE_LICENSE_2_0)
			.addLibrary("jkeymaster 1.2", "https://github.com/tulskiy/jkeymaster", LGPL_3_0)
			.addLibrary("jna 4.5.1", "https://github.com/java-native-access/jna", APACHE_LICENSE_2_0)
			.addLibrary("jna-platform 4.5.1", "https://github.com/java-native-access/jna", APACHE_LICENSE_2_0)
			.addLibrary("json 20141113", "https://github.com/douglascrockford/JSON-java", JSON_LICENSE)
			.addLibrary("logback-classic 1.2.3", "https://github.com/qos-ch/logback", EPL_1_0)
			.addLibrary("logback-core 1.2.3", "https://github.com/qos-ch/logback", EPL_1_0)
			.addLibrary("maven-model 3.6.0", "https://github.com/apache/maven", APACHE_LICENSE_2_0)
			.addLibrary("plexus-utils 3.1.0", "https://github.com/codehaus-plexus/plexus-utils", APACHE_LICENSE_2_0)
			.addLibrary("proxy-vole 1.0.3", "https://github.com/MarkusBernhardt/proxy-vole", APACHE_LICENSE_2_0)
			.addLibrary("silentsoft-core 1.4.0", "https://github.com/silentsoft/silentsoft-core", APACHE_LICENSE_2_0)
			.addLibrary("silentsoft-io 1.3.0", "https://github.com/silentsoft/silentsoft-io", APACHE_LICENSE_2_0)
			.addLibrary("silentsoft-net 1.5.0", "https://github.com/silentsoft/silentsoft-net", APACHE_LICENSE_2_0)
			.addLibrary("silentsoft-ui 1.3.0", "https://github.com/silentsoft/silentsoft-ui", APACHE_LICENSE_2_0)
			.addLibrary("slf4j-api 1.7.25", "https://github.com/qos-ch/slf4j", MIT_LICENSE)
			.addLibrary("PlusHaze-TrayNotification 5393c3a54f", "https://github.com/PlusHaze/TrayNotification", MIT_LICENSE)
			.addLibrary("Oracle Java SE Runtime Environment 8", "https://www.oracle.com/technetwork/java/javase/terms/license/index.html", BCL_LICENSE)
			.generate();
	}
	
	private String readFile() throws Exception {
		return String.join("\r\n", Files.readAllLines(Paths.get(System.getProperty("user.dir"), "NOTICE.md"), StandardCharsets.UTF_8));
	}
	
}
