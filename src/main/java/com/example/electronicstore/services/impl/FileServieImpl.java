package com.example.electronicstore.services.impl;

import com.example.electronicstore.exception.BadApiRequestException;
import com.example.electronicstore.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


@Service
public class FileServieImpl implements FileService {
    @Override
    public String uploadFile(MultipartFile file, String path) throws IOException {

        String orginalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString();
        String extension = orginalFileName.substring(orginalFileName.lastIndexOf("."));
        String fileNameWithExtension = fileName + extension;
        String fullPathWithFileName = path + "/" + fileNameWithExtension;

        if(extension.equalsIgnoreCase(".jpg") || extension.equalsIgnoreCase(".png") ||
                extension.equalsIgnoreCase(".jpeg")){
            //upload file

            File folder = new File(path);

            if(!folder.exists()){
                folder.mkdirs();
            }

            Files.copy(file.getInputStream(), Paths.get(fullPathWithFileName));
            return fileNameWithExtension;
        }
        else {
            //throw exception
            throw new BadApiRequestException("File with extension " + extension + " is not supported");
        }
    }

    @Override
    public InputStream getResource(String name, String path) throws FileNotFoundException {

        String fullPathWithFileName = path + File.separator + name;
        InputStream inputStream = new FileInputStream(fullPathWithFileName);
        return inputStream;
    }

}
