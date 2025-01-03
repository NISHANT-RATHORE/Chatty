package com.example.backendservice.DTO;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class ImageModel {
    public MultipartFile file;
}