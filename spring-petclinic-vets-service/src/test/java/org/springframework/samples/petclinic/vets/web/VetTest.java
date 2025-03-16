package org.springframework.samples.petclinic.vets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VetTest {

    private Vet vet;

    @BeforeEach
    void setUp() {
        vet = new Vet();
    }

    @Test
    void testSetAndGetId() {
        vet.setId(1);
        assertEquals(1, vet.getId(), "Vet ID should be 1");
    }

    @Test
    void testSetAndGetFirstName() {
        vet.setFirstName("John");
        assertEquals("John", vet.getFirstName(), "Vet first name should be John");
    }

    @Test
    void testSetAndGetLastName() {
        vet.setLastName("Doe");
        assertEquals("Doe", vet.getLastName(), "Vet last name should be Doe");
    }

    @Test
    void testSetAndGetName() {
        // Kiểm tra setName và getName
        vet.setName("Dr. John Doe");
        assertEquals("Dr. John Doe", vet.getName(), "Vet name should be Dr. John Doe");
    }

    @Test
    void testAddSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("Surgery");
        vet.addSpecialty(specialty);
        assertTrue(vet.getSpecialties().contains(specialty), "Specialty should be added to vet's specialties");
    }

    @Test
    void testGetSpecialties() {
        Specialty specialty = new Specialty();
        specialty.setName("Surgery");
        vet.addSpecialty(specialty);
        assertEquals(1, vet.getSpecialties().size(), "Vet should have 1 specialty");
    }

    @Test
    void testEmptySpecialties() {
        assertTrue(vet.getSpecialties().isEmpty(), "Vet should have no specialties initially");
    }
}
