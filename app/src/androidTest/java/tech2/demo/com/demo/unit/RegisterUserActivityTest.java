package tech2.demo.com.demo.unit;

import org.apache.commons.validator.routines.EmailValidator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegisterUserActivityTest {
    private String mGoodPassword = "StrongerPasswor";
    private String mBadPassword = "One";
    private String mGoodEmail = "name@email.com";
    private String mBadEmail = "";
    private EmailValidator mValidator;

    // The email syntax validation is done automatically by the Firebase OAuth but I wanted to showcase how you can validate the email if you need to
    @Before
    public void initValidator() {
        mValidator = EmailValidator.getInstance();
    }

    @Test
    public void emailValidator_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(mValidator.isValid(mGoodEmail));
    }

    @Test
    public void emailValidator_IncorrectEmailSimple_ReturnFalse() {
        assertFalse(mValidator.isValid(mBadEmail));
    }

    @Test
    public void emailNotEmpty_ReturnTrue() {
        assertTrue(isEmpty(mBadEmail));
    }

    @Test
    public void emailNotEmpty_ReturnFalse() {
        assertFalse(isEmpty(mGoodEmail));
    }

    @Test
    public void passwordCorrectLength_ReturnsTrue() {
        assertTrue(mGoodPassword.length() > 8 && mGoodPassword.length() < 16);
    }

    @Test
    public void passwordCorrectLength_ReturnsFalse() {
        assertFalse(mBadPassword.length() > 8 && mBadPassword.length() < 16);
    }

    // Custom function to check if a string is empty (StringUtils can't be used here)
    private static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}
