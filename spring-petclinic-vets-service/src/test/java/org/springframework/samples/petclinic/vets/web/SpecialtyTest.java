import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpecialtyTest {

    @Test
    void testSpecialtyConstructor() {
        // Arrange
        Specialty specialty = new Specialty("Surgery");

        // Act & Assert
        assertEquals("Surgery", specialty.getName());
    }

    @Test
    void testSpecialtySettersAndGetters() {
        // Arrange
        Specialty specialty = new Specialty();
        specialty.setName("Dentistry");

        // Act & Assert
        assertEquals("Dentistry", specialty.getName());
    }
}
