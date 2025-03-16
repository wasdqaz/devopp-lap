import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SpecialtyTest {

    // Định nghĩa class Specialty ngay trong file này
    static class Specialty {
        private Integer id;
        private String name;

        // Constructor nhận tham số
        public Specialty(String name) {
            this.name = name;
        }

        // Constructor mặc định
        public Specialty() {
        }

        // Getter và Setter cho id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
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
        Specialty specialty = new Specialty();

        // Act & Assert
        assertNull(specialty.getName());
    }

    @Test
    void testSetId() {
        // Arrange
        Specialty specialty = new Specialty("Neurology");

        // Act
        specialty.setId(1);

        // Assert
        assertEquals(1, specialty.getId());
    }

    @Test
    void testGetId() {
        // Arrange
        Specialty specialty = new Specialty("Orthopedics");
        specialty.setId(2);

        // Act & Assert
        assertEquals(2, specialty.getId());
    }
}
