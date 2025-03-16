package org.springframework.samples.petclinic.customers.web;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRepository;

@RestController
@RequestMapping("/owners/{ownerId}/pets")
public class PetResource {

    private final OwnerRepository ownerRepository;
    private final PetRepository petRepository;

    public PetResource(OwnerRepository ownerRepository, PetRepository petRepository) {
        this.ownerRepository = ownerRepository;
        this.petRepository = petRepository;
    }

    /**
     * Lấy danh sách tất cả thú cưng của một chủ sở hữu (Owner).
     */
    @GetMapping
    public ResponseEntity<List<Pet>> getPetsByOwner(@PathVariable("ownerId") int ownerId) {
        Optional<Owner> owner = ownerRepository.findById(ownerId);
        if (owner.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(owner.get().getPets());
    }

    /**
     * Lấy thông tin một thú cưng cụ thể của chủ sở hữu.
     */
    @GetMapping("/{petId}")
    public ResponseEntity<Pet> getPetById(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId) {
        Optional<Pet> pet = petRepository.findById(petId);
        if (pet.isEmpty() || pet.get().getOwner().getId() != ownerId) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pet.get());
    }
}
