package com.luna.apirest.app.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.luna.apirest.app.entity.vm.Asset;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class S3Service {

    private static final String BUCKET = "demo-ista-s3bucket";

    private final AmazonS3Client amazonS3Client;

    public String putObject(MultipartFile multipartFile) {
        String extension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
        String key = String.format("%s.%s", UUID.randomUUID(), extension);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        try {
            PutObjectRequest request = new PutObjectRequest(BUCKET, key, multipartFile.getInputStream(), metadata);
            amazonS3Client.putObject(request);
            return key;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Asset getObject(String key) {
        S3Object s3Object = amazonS3Client.getObject(BUCKET, key);
        ObjectMetadata metadata = s3Object.getObjectMetadata();
        try {
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return new Asset(bytes, metadata.getContentType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteObject(String key) {
        amazonS3Client.deleteObject(BUCKET, key);
    }

    public String getObjectUrl(String key) {
        return String.format("https://%s.s3.amazonaws.com/%s", BUCKET, key);
    }
}
