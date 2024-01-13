package com.kevinraupp.downloadandupload.downloadandupload.controllers;

import com.kevinraupp.downloadandupload.downloadandupload.dto.UploadFileResponseDTO;
import com.kevinraupp.downloadandupload.downloadandupload.services.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Tag(name = "File Endpoint")
@RestController
@RequestMapping(path = "/api/v1/file")
public class FileController {

    private Logger logger = Logger.getLogger(FileController.class.getName());

    @Autowired
    private FileStorageService service;

    @GetMapping(path = "/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        logger.info("Baixando o arquivo: " + filename);

        Resource resource = service.loadFileAsResource(filename);
        String contentType = "";
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception e) {
            logger.info("Não conseguiu determinar o tipo do arquivo");
        }

        if (contentType.isBlank()) contentType = "application/octet-stream";

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping(path = "/upload")
    public UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file) {
        var filename = service.storeFile(file);
        logger.info("Salvando o arquivo: " + filename);

        String fileDownloadURI = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/v1/file/downloadfile/").path(filename).toUriString();

        return new UploadFileResponseDTO(filename, fileDownloadURI, file.getContentType(), file.getSize());
    }

    @PostMapping(path = "/uploadMultiple")
    public List<UploadFileResponseDTO> uploadMultipleFiles(@RequestParam("files") MultipartFile files[]) {
        return Arrays.asList(files).stream().map(file -> uploadFile(file)).collect(Collectors.toList());
    }
}