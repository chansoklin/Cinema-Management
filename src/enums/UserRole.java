package enums;

public enum UserRole {
    ADMIN("ADMIN"),
    MANAGER("MANAGER"),
    FRONT_DESK("FRONT_DESK");

    private String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromString(String text) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(text)) {
                return role;
            }
        }
        return null;
    }
}