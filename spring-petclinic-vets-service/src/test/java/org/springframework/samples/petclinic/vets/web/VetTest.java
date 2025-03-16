import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VetTest {

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
