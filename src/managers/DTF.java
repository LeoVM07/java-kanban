package managers;

import java.time.format.DateTimeFormatter;

public class DTF {
    public static DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm | dd.MM.yyyy");
    }
}
