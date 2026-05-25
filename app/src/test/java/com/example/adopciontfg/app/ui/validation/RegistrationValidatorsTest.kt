package com.example.adopciontfg.app.ui.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistrationValidatorsTest {
    @Test
    fun validSpanishPhonesReturnTrue() {
        assertTrue(isValidSpanishPhone("612345678"))
        assertTrue(isValidSpanishPhone("912345678"))
        assertTrue(isValidSpanishPhone("+34 612 345 678"))
        assertTrue(isValidSpanishPhone("0034-612-345-678"))
    }

    @Test
    fun invalidSpanishPhonesReturnFalse() {
        assertFalse(isValidSpanishPhone("512345678"))
        assertFalse(isValidSpanishPhone("61234"))
        assertFalse(isValidSpanishPhone("+33 612 345 678"))
        assertFalse(isValidSpanishPhone("telefono 612345678"))
    }

    @Test
    fun validGoogleFormsUrlsReturnTrue() {
        assertTrue(isValidGoogleFormsUrl("https://forms.gle/abc123"))
        assertTrue(isValidGoogleFormsUrl("https://docs.google.com/forms/d/e/abc123/viewform"))
        assertTrue(isValidGoogleFormsUrl(" https://www.docs.google.com/forms/d/abc123/edit "))
    }

    @Test
    fun invalidGoogleFormsUrlsReturnFalse() {
        assertFalse(isValidGoogleFormsUrl(""))
        assertFalse(isValidGoogleFormsUrl("https://example.com/forms/d/abc123/viewform"))
        assertFalse(isValidGoogleFormsUrl("http://forms.gle/abc123"))
        assertFalse(isValidGoogleFormsUrl("https://forms.gle/"))
    }
}
