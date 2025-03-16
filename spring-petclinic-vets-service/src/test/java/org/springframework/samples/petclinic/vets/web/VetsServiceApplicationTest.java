import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VetsServiceApplicationTest {

    @Test
    void testMain() {
        // Arrange
        String[] args = {};

        // Act & Assert
        assertDoesNotThrow(() -> VetsServiceApplication.main(args));
    }

    @Test
    void testVetsServiceApplicationConstructor() {
        // Act
        VetsServiceApplication app = new VetsServiceApplication();

        // Assert
        assertNotNull(app);
    }
}
