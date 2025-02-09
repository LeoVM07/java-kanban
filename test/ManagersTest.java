import managers.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void notNullForDefaultMethodsTest() {
        Assertions.assertNotNull(Managers.getDefault(), "Метод getDefault ничего не создал");
        Assertions.assertNotNull(Managers.getDefaultHistory(), "Метод getDefaultHistory ничего не создал");
    }

}