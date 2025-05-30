package io.github.naomimyselfandi.xanaduwars.info;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// A trivial controller for checking if the API is up.
@RestController
@RequiredArgsConstructor
@RequestMapping("/info/ping")
public class PingController {

    @GetMapping
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong!");
    }

}
