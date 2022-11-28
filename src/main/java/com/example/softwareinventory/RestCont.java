package com.example.softwareinventory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@org.springframework.web.bind.annotation.RestController
public class RestCont {
    private final RestRepo repo;
    RestCont(RestRepo repo) {
        this.repo = repo;
    }
    @GetMapping("/rest")
    Iterable<Rest> olvasMind() {
        return repo.findAll();
    }

    @GetMapping("/rest/{id}")
    Rest olvasEgy(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PostMapping("/rest")
    Rest telefonFeltolt(@RequestBody Rest ujTelefon) {
        return repo.save(ujTelefon);
    }

    @PutMapping("/rest/{id}")
    Rest telefonModosit(@RequestBody Rest adatTel, @PathVariable Long id) {
        return repo.findById(id)
                .map(a -> {
                    a.setBrand(adatTel.getBrand());
                    a.setModel(adatTel.getModel());
                    a.setPrice(adatTel.getPrice());
                    a.setYear(adatTel.getYear());

                    return repo.save(a);
                })
                .orElseGet(() -> {
                    adatTel.setId(id);
                    return repo.save(adatTel);
                });
    }
    @DeleteMapping("/rest/{id}")
    void torolTel(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
