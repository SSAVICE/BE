package teamssavice.ssavice.imageresource.constants;

public class ImageConstants {

    public static String DEFAULT_COMPANY_IMAGE_OBJECT_KEY = "company/default.png";
    public static String DEFAULT_PROFILE_IMAGE_OBJECT_KEY = "profile/default.png";
    public static String DEFAULT_SERVICE_ITEM_IMAGE_OBJECT_KEY = "serviceItem/default.png";


    public static String defaultKey(ImagePath path) {
        return switch (path) {
            case profile -> DEFAULT_PROFILE_IMAGE_OBJECT_KEY;
            case company -> DEFAULT_COMPANY_IMAGE_OBJECT_KEY;
            case serviceItem -> DEFAULT_SERVICE_ITEM_IMAGE_OBJECT_KEY;
            default -> throw new IllegalArgumentException("No default image for path: " + path);
        };
    }
}
