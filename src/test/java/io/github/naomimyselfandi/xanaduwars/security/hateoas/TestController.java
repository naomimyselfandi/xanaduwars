package io.github.naomimyselfandi.xanaduwars.security.hateoas;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/ing")
class TestController {

    @GetMapping("/foo/{id}")
    ResponseEntity<String> getFoo(@PathVariable int id) {
        return ResponseEntity.ok(String.valueOf(id));
    }

    @GetMapping("/bar/{id}")
    ResponseEntity<String> getBar(@PathVariable int id) {
        return ResponseEntity.ok(String.valueOf(id));
    }

    @GetMapping("/baz/{id}")
    ResponseEntity<String> getBaz(@PathVariable int id) {
        return ResponseEntity.ok(String.valueOf(id));
    }

}
