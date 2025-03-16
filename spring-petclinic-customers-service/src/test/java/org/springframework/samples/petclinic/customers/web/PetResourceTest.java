package org.springframework.samples.petclinic.customers.web;

import java.util.Optional;
import java.util.List;

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
import org.springframework.samples.petclinic.customers.web.ResourceNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


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

    @Test
    void shouldGetPetWithTypeMissingId() throws Exception {
        Pet pet = new Pet();
        pet.setId(7);
        pet.setName("NamelessID");
    
        PetType type = new PetType();
        type.setName("Mystery"); // Không set ID
        pet.setType(type);
    
        Owner owner = new Owner();
        owner.setFirstName("Someone");
        owner.setLastName("Important");
        pet.setOwner(owner);
    
        given(petRepository.findById(7)).willReturn(Optional.of(pet));
    
        mvc.perform(get("/owners/2/pets/7").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(7))
            .andExpect(jsonPath("$.type.name").value("Mystery"))
            .andExpect(jsonPath("$.type.id").doesNotExist());
    }

    @Test
    void shouldHandleOwnerWithoutFirstName() throws Exception {
        Pet pet = new Pet();
        pet.setId(8);
        pet.setName("OnlyLast");
    
        PetType type = new PetType();
        type.setId(10);
        type.setName("Lizard");
        pet.setType(type);
    
        Owner owner = new Owner();
        owner.setLastName("Solo"); // Không set FirstName
        pet.setOwner(owner);
    
        given(petRepository.findById(8)).willReturn(Optional.of(pet));
    
        mvc.perform(get("/owners/2/pets/8").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(8))
            .andExpect(jsonPath("$.type.name").value("Lizard"));
    }

    @Test
    void shouldGetPetWithTypeNameButNullTypeId() throws Exception {
        Pet pet = new Pet();
        pet.setId(10);
        pet.setName("Choco");
    
        PetType type = new PetType();
        type.setName("Tiger"); // Không set ID
        pet.setType(type);
    
        Owner owner = new Owner();
        owner.setFirstName("Alex");
        owner.setLastName("Jungle");
        pet.setOwner(owner);
    
        given(petRepository.findById(10)).willReturn(Optional.of(pet));
    
        mvc.perform(get("/owners/2/pets/10").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.name").value("Choco"))
            .andExpect(jsonPath("$.type.name").value("Tiger"))
            .andExpect(jsonPath("$.type.id").doesNotExist());
    }

    @Test
    void shouldCreatePetSuccessfully() throws Exception {
        Owner owner = new Owner();
        owner.setId(1);
        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));
    
        PetType type = new PetType();
        type.setId(2);
        type.setName("Dog");
        given(petRepository.findPetTypeById(2)).willReturn(Optional.of(type));
    
        given(petRepository.save(any(Pet.class))).willAnswer(invocation -> {
            Pet p = invocation.getArgument(0);
            p.setId(99);
            return p;
        });
    
        mvc.perform(post("/owners/1/pets")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "name": "Rex",
                    "birthDate": "2020-01-01",
                    "typeId": 2
                }
            """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(99))
            .andExpect(jsonPath("$.name").value("Rex"))
            .andExpect(jsonPath("$.type.id").value(2));
    }

    @Test
    void shouldUpdatePetSuccessfully() throws Exception {
        Pet pet = new Pet();
        pet.setId(1);
        given(petRepository.findById(1)).willReturn(Optional.of(pet));
    
        PetType type = new PetType();
        type.setId(3);
        given(petRepository.findPetTypeById(3)).willReturn(Optional.of(type));
    
        mvc.perform(put("/owners/any/pets/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "id": 1,
                    "name": "UpdatedName",
                    "birthDate": "2022-12-31",
                    "typeId": 3
                }
            """))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetPetTypes() throws Exception {
        PetType cat = new PetType(); cat.setId(1); cat.setName("Cat");
        PetType dog = new PetType(); dog.setId(2); dog.setName("Dog");
    
        given(petRepository.findPetTypes()).willReturn(List.of(cat, dog));
    
        mvc.perform(get("/petTypes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].name").value("Dog"));
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
