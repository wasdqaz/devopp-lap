import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

// Đặt lớp Vet và Specialty ở đây
public class VetTest {

    static class Vet {
        private Integer id;
        private String firstName;
        private String lastName;
        private String name;
        private Set<Specialty> specialties = new HashSet<>();

        // Getters and setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<Specialty> getSpecialties() {
            return specialties;
        }

        public void addSpecialty(Specialty specialty) {
            this.specialties.add(specialty);
        }

        // Thêm phương thức đếm số lượng specialty
        public int getNrOfSpecialties() {
            return specialties.size();
        }
    }

    static class Specialty {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // Các phương thức test
    @Test
    void testVetGettersAndSetters() {
        // Arrange
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("John");
        vet.setLastName("Doe");

        // Act & Assert
        assertEquals(1, vet.getId());
        assertEquals("John", vet.getFirstName());
        assertEquals("Doe", vet.getLastName());
    }

    @Test
    void testAddSpecialty() {
        // Arrange
        Vet vet = new Vet();
        Specialty specialty = new Specialty();
        specialty.setName("Surgery");

        // Act
        vet.addSpecialty(specialty);

        // Assert
        assertTrue(vet.getSpecialties().contains(specialty));
    }

    @Test
    void testGetNrOfSpecialties() {
        // Arrange
        Vet vet = new Vet();
        Specialty specialty1 = new Specialty();
        specialty1.setName("Surgery");
        Specialty specialty2 = new Specialty();
        specialty2.setName("Dentistry");

        // Act
        vet.addSpecialty(specialty1);
        vet.addSpecialty(specialty2);

        // Assert
        assertEquals(2, vet.getNrOfSpecialties());
    }
}
