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
}
