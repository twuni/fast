package org.twuni.fast.util;

import org.junit.Assert;
import org.junit.Test;

public class IOUtilsTest extends Assert {

	@Test
	public void toLong_shouldWorkProperly() {
		long expected = 0x1234567812345678L;
		long actual = IOUtils.toLong( IOUtils.toByteArray( expected ) );
		assertEquals( expected, actual );
	}

}
