package teamssavice.ssavice.auth.constants;

public enum Role {
    USER,
    ADMIN,
    COMPANY,
    TEMP;

    public boolean canAccess(Role required) {
        return this == required;
    }
}
