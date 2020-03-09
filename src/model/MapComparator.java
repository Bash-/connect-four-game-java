package model;

import java.util.Comparator;
import java.util.Map;

/**
 * A class used to compare values of a map.
 * @author Bas Hendrikse & X Wang
 *
 */
public class MapComparator implements Comparator {

	private Map map;

	public MapComparator(Map map) {
		this.map = map;
	}

	/**
	 * The method returns -1 too when valueA and valueB are equal. If this is
	 * not done (result == 0), the entry will not be added to the new sorted
	 * map. The method sorts values from low to high.
	 */
	public int compare(Object keyA, Object keyB) {

		int result = 0;
		Comparable valueA = (Comparable) map.get(keyA);
		Comparable valueB = (Comparable) map.get(keyB);
		if (valueA == valueB) {
			result = -1;
		} else if ((int) valueA - (int) valueB < 0) {
			result = -1;
		} else {
			result = 1;
		}
		return result;
	}
}