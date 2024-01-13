package com.kevinraupp.downloadandupload.downloadandupload.services;

import com.kevinraupp.downloadandupload.downloadandupload.config.FileStorageConfig;
import com.kevinraupp.downloadandupload.downloadandupload.config.exceptions.FileNotFoundException;
import com.kevinraupp.downloadandupload.downloadandupload.config.exceptions.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        Path path = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize();

        this.fileStorageLocation = path;
        System.out.println();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new FileStorageException("Não foi possvel criar o diretorio onde os arquivos de upload serão guardados", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..") || fileName.contains(" ")) {
                throw new FileStorageException("Nome do arquivo invalido! " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (Exception e) {
            throw new FileStorageException("Não foi possivel salvar o arquivo: " + fileName + ". Please try again", e);
        }
    }

    public Resource loadFileAsResource(String fileName){
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(!resource.exists()) throw new FileNotFoundException("Arquivo não encontrado!");
            return resource;
        }catch (Exception e){
            throw new FileNotFoundException("Não econtrado o arquivo: " + fileName,e);
        }
    }
}
