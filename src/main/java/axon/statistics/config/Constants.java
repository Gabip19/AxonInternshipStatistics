package axon.statistics.config;

public class Constants {
    public static final int LINE_FIELDS_NUMBER = 4;
    public static final String NAME_REGEX = "^([A-Z\\p{Lu}][a-z\\p{Ll}]+[ -])+[A-Z\\p{Lu}][a-z\\p{Ll}]+$";
    public static final String EMAIL_REGEX = "^[a-z][a-z0-9-_.]+@[a-z][a-z0-9-_.]*\\.[a-z]+$";
    public static final String TIME_REGEX = "^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$";
    public static final String SCORE_REGEX = "^[1-9]0?(\\.[0-9]{2})?$";
    public static final int TOP_NUMBER = 3;
}
