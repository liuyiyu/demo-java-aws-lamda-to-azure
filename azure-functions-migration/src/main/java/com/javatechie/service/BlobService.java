package com.javatechie.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlobService {
/*
    @Autowired
    private BlobServiceClient blobServiceClient;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String readBlobContent(String blobName) {
        try {
            BlobClient blobClient = blobServiceClient
                    .getBlobContainerClient(containerName)
                    .getBlobClient(blobName);
            return blobClient.downloadContent().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error reading blob: " + e.getMessage(), e);
        }
    }

    public List<String> listBlobs() {
        try {
            BlobContainerClient containerClient = blobServiceClient
                    .getBlobContainerClient(containerName);
            return containerClient.listBlobs().stream()
                    .map(BlobItem::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error listing blobs: " + e.getMessage(), e);
        }
    }

    public boolean blobExists(String blobName) {
        try {
            BlobClient blobClient = blobServiceClient
                    .getBlobContainerClient(containerName)
                    .getBlobClient(blobName);
            return blobClient.exists();
        } catch (Exception e) {
            return false;
        }
    }
    */
}
