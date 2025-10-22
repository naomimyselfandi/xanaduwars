package io.github.naomimyselfandi.xanaduwars.auth.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/foo")
class HateoasServiceTestController {

    @GetMapping("/bar/{id}")
    ResponseEntity<String> bar(@PathVariable String id) {
        return ResponseEntity.ok(id);
    }

}
