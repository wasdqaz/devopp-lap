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
import org.springframework.samples.petclinic.customers.web.mapper.OwnerEntityMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for OwnerResource
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(OwnerResource.class)
@ActiveProfiles("test")
class OwnerResourceTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    OwnerRepository ownerRepository;

    @MockBean
    OwnerEntityMapper ownerEntityMapper;

    @Test
    void shouldReturnOwnerInJsonFormat() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");

        given(ownerRepository.findById(1)).willReturn(Optional.of(owner));

        mvc.perform(get("/owners/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void shouldCreateOwnerSuccessfully() throws Exception {
        OwnerRequest request = new OwnerRequest("John", "Doe", "123 Main St", "City", "123456789");

        Owner savedOwner = new Owner();
        ReflectionTestUtils.setField(savedOwner, "id", 1);
        savedOwner.setFirstName("John");

        given(ownerEntityMapper.map(any(Owner.class), any(OwnerRequest.class))).willReturn(savedOwner);
        given(ownerRepository.save(any(Owner.class))).willReturn(savedOwner);

        mvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "address": "123 Main St",
                        "city": "City",
                        "telephone": "123456789"
                    }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void shouldUpdateOwnerSuccessfully() throws Exception {
        Owner existingOwner = new Owner();
        ReflectionTestUtils.setField(existingOwner, "id", 1);
        existingOwner.setFirstName("OldName");

        Owner updatedOwner = new Owner();
        ReflectionTestUtils.setField(updatedOwner, "id", 1);
        updatedOwner.setFirstName("NewName");

        given(ownerRepository.findById(1)).willReturn(Optional.of(existingOwner));
        given(ownerEntityMapper.map(any(Owner.class), any(OwnerRequest.class))).willReturn(updatedOwner);
        given(ownerRepository.save(any(Owner.class))).willReturn(updatedOwner);

        mvc.perform(put("/owners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "firstName": "NewName",
                        "lastName": "Doe",
                        "address": "New Address",
                        "city": "New City",
                        "telephone": "987654321"
                    }
                """))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnListOfOwners() throws Exception {
        Owner owner = new Owner();
        ReflectionTestUtils.setField(owner, "id", 1);
        owner.setFirstName("John");
        List<Owner> owners = List.of(owner);

        given(ownerRepository.findAll()).willReturn(owners);

        mvc.perform(get("/owners"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("John"));
    }
}
