package com.wilma.web.controller.api.v1;

import com.wilma.config.web.UserDocumentConfiguration;
import com.wilma.service.docs.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("${api.v1.context-path}/partners")
public class PartnerController {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private UserDocumentConfiguration documentConfiguration;

    @GetMapping("/view-attachment/{documentId}")
    public ResponseEntity<?> viewAttachment(@PathVariable Integer documentId) throws IOException {
        var file = documentService.findById(documentId);
        return ResponseEntity.ok()
                .header("Content-type", documentConfiguration.getMediaType(file.getFilename()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"".concat(file.getFilename()).concat("\""))
                .body(Files.readAllBytes(Path.of(file.getFilepath())));
    }
}
