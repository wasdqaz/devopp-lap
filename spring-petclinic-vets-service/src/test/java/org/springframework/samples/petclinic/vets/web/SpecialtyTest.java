import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpecialtyTest {

    // Định nghĩa class Specialty ngay trong file này
    static class Specialty {
        private String name;

        // Constructor nhận tham số
        public Specialty(String name) {
            this.name = name;
        }

        // Getter và Setter cho name
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

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
        Specialty specialty = new Specialty("Dentistry");

        // Act & Assert
        assertEquals("Dentistry", specialty.getName());
    }

    @Test
    void testSetName() {
        // Arrange
        Specialty specialty = new Specialty("Cardiology");

        // Act
        specialty.setName("Neurology");

        // Assert
        assertEquals("Neurology", specialty.getName());
    }

    @Test
    void testGetName() {
        // Arrange
        Specialty specialty = new Specialty("Pediatrics");

        // Act & Assert
        assertEquals("Pediatrics", specialty.getName());
    }

    @Test
    void testSpecialtyDefaultConstructor() {
        // Arrange
        Specialty specialty = new Specialty(null);

        // Act & Assert
        assertNull(specialty.getName());
    }
}
