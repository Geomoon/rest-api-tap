package com.luna.apirest.app.controller;

import com.luna.apirest.app.entity.User;
import com.luna.apirest.app.service.S3Service;
import com.luna.apirest.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<User> getAll() {
        return StreamSupport
                .stream(service.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) {
        var optional = service.findById(id);
        if (optional.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(optional);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody User user, @PathVariable Long id) {
        var optional = service.findById(id);

        if (optional.isEmpty())
            return ResponseEntity.notFound().build();

        optional.get().setEmail(user.getEmail());
        optional.get().setEstado(user.getEstado());
        optional.get().setName(user.getName());
        optional.get().setPassword(user.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.save(optional.get()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (service.findById(id).isEmpty())
            return ResponseEntity.notFound().build();
        service.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
