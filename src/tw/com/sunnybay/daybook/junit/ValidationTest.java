package tw.com.sunnybay.daybook.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import tw.com.sunnybay.daybook.Validation;

public class ValidationTest {

	@Test
	public void isNumber() {
		assertTrue(Validation.isNumber("0"));
		assertTrue(Validation.isNumber("9"));
		assertTrue(Validation.isNumber("10"));
		assertTrue(Validation.isNumber("99"));
		assertTrue(Validation.isNumber("1234567890"));
		assertFalse(Validation.isNumber("A0"));
	}
	
	@Test
	public void isDate() {
		
	}

}
