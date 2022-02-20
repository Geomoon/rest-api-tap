package com.luna.apirest.app.controller;

import com.luna.apirest.app.entity.vm.Asset;
import com.luna.apirest.app.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/assets/")
public class AssetController {

    @Autowired
    private S3Service service;

    @PostMapping("/upload")
    public Map<String, String> upload(@RequestParam MultipartFile cedula, @RequestParam MultipartFile photo) {
        String cedulaKey = service.putObject(cedula);
        String photoKey = service.putObject(photo);

        var result = new HashMap<String, String>();
        result.put("cedula_url", service.getObjectUrl(cedulaKey));
        result.put("photo_url", service.getObjectUrl(photoKey));
        return result;
    }

    @GetMapping(value = "/get-object", params = "key")
    public ResponseEntity<ByteArrayResource> getObject(@RequestParam String key) {
        Asset asset = service.getObject(key);
        var resource = new ByteArrayResource(asset.getContent());
        return ResponseEntity
                .ok()
                .header("Content-Type", asset.getContentType())
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @DeleteMapping(value = "/delete-object", params = "key")
    public void deleteObject(@RequestParam String key) {
        service.deleteObject(key);
    }
}
