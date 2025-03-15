package org.springframework.samples.petclinic.customers.web;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Maciej Szarlinski
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(PetResource.class)
@ActiveProfiles("test")
class PetResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PetRepository petRepository;

    @MockBean
    OwnerRepository ownerRepository;

    @Test
    void shouldGetAPetInJSonFormat() throws Exception {

        Pet pet = setupPet();

        given(petRepository.findById(2)).willReturn(Optional.of(pet));


        mvc.perform(get("/owners/2/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("Basil"))
            .andExpect(jsonPath("$.type.id").value(6));
    }
    
    @Test
    void shouldReturnNotFoundWhenPetDoesNotExist() throws Exception {
        given(petRepository.findById(999)).willReturn(Optional.empty());
    
        mvc.perform(get("/owners/2/pets/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldGetPetWithNullType() throws Exception {
        Pet pet = new Pet();
        pet.setId(3);
        pet.setName("Ghost");
    
        // Gán owner để tránh NullPointerException
        Owner owner = new Owner();
        owner.setFirstName("Test");
        owner.setLastName("Owner");
        pet.setOwner(owner);
    
        given(petRepository.findById(3)).willReturn(Optional.of(pet));
    
        mvc.perform(get("/owners/2/pets/3").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("Ghost"))
            .andExpect(jsonPath("$.type").doesNotExist());
    }

    
    @Test
    void shouldReturnJsonContentType() throws Exception {
        Pet pet = setupPet();
        given(petRepository.findById(2)).willReturn(Optional.of(pet));
    
        mvc.perform(get("/owners/2/pets/2").accept(MediaType.APPLICATION_JSON))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldGetPetWithNullTypeName() throws Exception {
        Pet pet = new Pet();
        pet.setId(4);
        pet.setName("NoNameType");
    
        PetType petType = new PetType();
        petType.setId(7); // Có id
        pet.setType(petType); // Nhưng không set name
    
        Owner owner = new Owner();
        owner.setFirstName("Anonymous");
        owner.setLastName("Owner");
        pet.setOwner(owner);
    
        given(petRepository.findById(4)).willReturn(Optional.of(pet));
    
        mvc.perform(get("/owners/2/pets/4").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(4))
            .andExpect(jsonPath("$.type.id").value(7))
            .andExpect(jsonPath("$.type.name").doesNotExist());
    }

    @Test
    void shouldReturnPetEvenIfNotInOwnerPetsList() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
    
        Pet pet = new Pet();
        pet.setId(5);
        pet.setName("StrayCat");
    
        PetType type = new PetType();
        type.setId(9);
        type.setName("Cat");
        pet.setType(type);
    
        pet.setOwner(owner);
        // Không gọi owner.addPet(pet); để mô phỏng trường hợp pet không nằm trong danh sách
    
        given(petRepository.findById(5)).willReturn(Optional.of(pet));
    
        mvc.perform(get("/owners/2/pets/5").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.name").value("StrayCat"))
            .andExpect(jsonPath("$.type.name").value("Cat"));
    }

    @Test
    void shouldHandleOwnerWithoutLastName() throws Exception {
        Pet pet = new Pet();
        pet.setId(6);
        pet.setName("NoLastName");
    
        PetType petType = new PetType();
        petType.setId(8);
        petType.setName("Dog");
        pet.setType(petType);
    
        Owner owner = new Owner();
        owner.setFirstName("OnlyFirst"); // Không setLastName
        pet.setOwner(owner);
    
        given(petRepository.findById(6)).willReturn(Optional.of(pet));
    
        mvc.perform(get("/owners/2/pets/6").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(6))
            .andExpect(jsonPath("$.name").value("NoLastName"))
            .andExpect(jsonPath("$.type.name").value("Dog"));
    }


    private Pet setupPet() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");

        Pet pet = new Pet();

        pet.setName("Basil");
        pet.setId(2);

        PetType petType = new PetType();
        petType.setId(6);
        pet.setType(petType);

        owner.addPet(pet);
        return pet;
    }
}
