package org.silentsoft.actlist.comparator;

import org.junit.Test;

import junit.framework.Assert;

public class VersionComparatorTest {

	@Test
	public void compareTest() {
		Assert.assertEquals(0, VersionComparator.getInstance().compare("1.0.0", "1.0.0"));
		
		Assert.assertEquals(-1, VersionComparator.getInstance().compare("1.0.0", "1.1.0"));
		Assert.assertEquals(1, VersionComparator.getInstance().compare("1.1.0", "1.0.0"));
		
		Assert.assertEquals(-1, VersionComparator.getInstance().compare("1.9.9", "1.9.99"));
		Assert.assertEquals(1, VersionComparator.getInstance().compare("1.99.0", "1.9.0"));
		
		Assert.assertEquals(-1, VersionComparator.getInstance().compare("1.2.99", "1.12.0"));
		Assert.assertEquals(1, VersionComparator.getInstance().compare("1.12.0", "1.2.0"));
	}
	
}
