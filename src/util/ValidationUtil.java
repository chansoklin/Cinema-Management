package util;

import java.util.regex.Pattern;
import exception.ValidationException;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\d{10,11}$"
    );

    public static void validateEmail(String email) throws ValidationException {
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email format");
        }
    }

    public static void validatePhone(String phone) throws ValidationException {
        if (phone != null && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Phone number must be 10-11 digits");
        }
    }

    public static void validateNotEmpty(String value, String fieldName)
            throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty");
        }
    }

    public static void validatePositiveNumber(int number, String fieldName)
            throws ValidationException {
        if (number <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }
}