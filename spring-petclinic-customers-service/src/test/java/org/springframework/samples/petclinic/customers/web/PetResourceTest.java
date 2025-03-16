package org.springframework.samples.petclinic.customers;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.samples.petclinic.customers.config.MetricConfig;
import org.springframework.samples.petclinic.customers.model.*;
import org.springframework.samples.petclinic.customers.repository.OwnerRepository;
import org.springframework.samples.petclinic.customers.repository.PetRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {MetricConfig.class, CustomersServiceTests.TestConfig.class})
class CustomersServiceTests {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private TimedAspect timedAspect;

    @Autowired
    private MetricConfig metricConfig;

    /*** 🟢 TEST MetricConfig ***/
    @Test
    void testMetricConfigBeans() {
        MeterRegistry registry = metricConfig.meterRegistry();
        assertNotNull(registry);
        assertTrue(registry instanceof SimpleMeterRegistry);
    }

    @Test
    void testTimedAspect() {
        TimedAspect aspect = metricConfig.timedAspect(meterRegistry);
        assertNotNull(aspect);
    }

    /*** 🟢 TEST CustomersServiceApplication ***/
    @Test
    void mainMethodShouldRunWithoutExceptions() {
        try (MockedStatic<SpringApplication> mockedStatic = Mockito.mockStatic(SpringApplication.class)) {
            mockedStatic.when(() -> SpringApplication.run(CustomersServiceApplication.class, new String[]{}))
                        .thenReturn(null);

            assertDoesNotThrow(() -> CustomersServiceApplication.main(new String[]{}));
            mockedStatic.verify(() -> SpringApplication.run(CustomersServiceApplication.class, new String[]{}), Mockito.times(1));
        }
    }

    /*** 🔹 TEST CONFIGURATION ***/
    @TestConfiguration
    static class TestConfig {
        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }
}

@DataJpaTest
class OwnerRepositoryTest {
    @Autowired
    private OwnerRepository ownerRepository;

    /*** 🟢 TEST OwnerRepository ***/
    @Test
    void testSaveAndFindOwner() {
        Owner owner = new Owner();
        owner.setFirstName("Alice");
        owner.setLastName("Brown");
        owner.setAddress("456 Elm St");
        owner.setCity("San Francisco");
        owner.setTelephone("9876543210");

        Owner savedOwner = ownerRepository.save(owner);
        Optional<Owner> foundOwner = ownerRepository.findById(savedOwner.getId());

        assertTrue(foundOwner.isPresent());
        assertEquals("Alice", foundOwner.get().getFirstName());
    }

    @Test
    void testDeleteOwner() {
        Owner owner = new Owner();
        owner.setFirstName("Bob");
        owner.setLastName("Smith");
        owner.setAddress("789 Oak St");
        owner.setCity("Los Angeles");
        owner.setTelephone("5678901234");

        Owner savedOwner = ownerRepository.save(owner);
        ownerRepository.delete(savedOwner);

        Optional<Owner> foundOwner = ownerRepository.findById(savedOwner.getId());
        assertFalse(foundOwner.isPresent());
    }
}

@DataJpaTest
class PetRepositoryTest {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private TestEntityManager entityManager;

    /*** 🟢 TEST PetRepository ***/
    @Test
    void testSaveAndFindPet() {
        Pet pet = new Pet();
        pet.setName("Charlie");

        PetType type = new PetType();
        type.setName("Dog");
        entityManager.persist(type);

        pet.setType(type);
        Pet savedPet = petRepository.save(pet);
        Optional<Pet> foundPet = petRepository.findById(savedPet.getId());

        assertTrue(foundPet.isPresent());
        assertEquals("Charlie", foundPet.get().getName());
    }

    @Test
    void testFindPetTypes() {
        PetType type1 = new PetType();
        type1.setName("Rabbit");

        PetType type2 = new PetType();
        type2.setName("Bird");

        entityManager.persist(type1);
        entityManager.persist(type2);
        entityManager.flush();

        List<PetType> petTypes = petRepository.findPetTypes();
        assertTrue(petTypes.size() >= 2);
    }

    @Test
    void testDeletePet() {
        Pet pet = new Pet();
        pet.setName("Milo");

        Pet savedPet = petRepository.save(pet);
        petRepository.delete(savedPet);

        Optional<Pet> foundPet = petRepository.findById(savedPet.getId());
        assertFalse(foundPet.isPresent());
    }
}

/*** 🟢 TEST ENTITY - Owner ***/
class OwnerTest {
    @Test
    void testOwnerSettersAndGetters() {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("New York");
        owner.setTelephone("1234567890");

        assertEquals("John", owner.getFirstName());
        assertEquals("Doe", owner.getLastName());
        assertEquals("123 Main St", owner.getAddress());
        assertEquals("New York", owner.getCity());
        assertEquals("1234567890", owner.getTelephone());
    }
}

/*** 🟢 TEST ENTITY - Pet ***/
class PetTest {
    @Test
    void testPetSettersAndGetters() {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setName("Buddy");
        pet.setBirthDate(new Date());

        PetType type = new PetType();
        type.setId(2);
        type.setName("Dog");
        pet.setType(type);

        Owner owner = new Owner();
        owner.setFirstName("John");
        pet.setOwner(owner);

        assertEquals(1, pet.getId());
        assertEquals("Buddy", pet.getName());
        assertNotNull(pet.getBirthDate());
        assertEquals("Dog", pet.getType().getName());
        assertEquals("John", pet.getOwner().getFirstName());
    }
}

/*** 🟢 TEST ENTITY - PetType ***/
class PetTypeTest {
    @Test
    void testPetTypeSettersAndGetters() {
        PetType type = new PetType();
        type.setId(3);
        type.setName("Cat");

        assertEquals(3, type.getId());
        assertEquals("Cat", type.getName());
    }
}
