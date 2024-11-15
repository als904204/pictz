package online.pictz.api.util;

import java.lang.reflect.Field;

public class TestUtils {

    private TestUtils() {

    }

    /**
     * 리프렉션을 이용해 id 수정
     * @param e 변경할 엔티티
     * @param id 변경할 id 값
     */
    public static void setId(Object e, Long id) {
        try {
            Field idField = e.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(e, id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
