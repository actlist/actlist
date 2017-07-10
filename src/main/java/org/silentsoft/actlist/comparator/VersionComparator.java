package org.silentsoft.actlist.comparator;

import java.util.Comparator;

public class VersionComparator implements Comparator<String> {

	private static VersionComparator instance;
	
	public static VersionComparator getInstance() {
		if (instance == null) {
			instance = new VersionComparator();
		}
		
		return instance;
	}
	
	@Override
	public int compare(String o1, String o2) {
		String[] _o1 = o1.split("\\.");
		String[] _o2 = o2.split("\\.");
		
		Integer o1Major = Integer.valueOf(_o1[0]);
		Integer o2Major = Integer.valueOf(_o2[0]);
		
		int majorCompare = o1Major.compareTo(o2Major);
		if (majorCompare == 0) {
			Integer o1Minor = Integer.valueOf(_o1[1]);
			Integer o2Minor = Integer.valueOf(_o2[1]);
			
			int minorCompare = o1Minor.compareTo(o2Minor);
			if (minorCompare == 0) {
				Integer o1Patch = Integer.valueOf(_o1[2]);
				Integer o2Patch = Integer.valueOf(_o2[2]);
				
				return o1Patch.compareTo(o2Patch);
			} else {
				return minorCompare;
			}
		}
		
		return majorCompare;
	}
	
}
