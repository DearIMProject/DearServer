package com.wmy.study.DearIMProject.controller;

import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.ResponseBean;
import com.wmy.study.DearIMProject.service.IUploadFileService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class UploadFileController {

    @Autowired
    private MessageSource messageSource;

    @Resource
    private IUploadFileService fileService;

    @PostMapping("/upload")
    public ResponseBean uploadFile(@RequestParam("file") MultipartFile file) throws IOException, BusinessException {
        if (file.isEmpty()) {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_FILE, "file.empty_file");
        }

        boolean success = fileService.uploadAFile(file);

        if (success) {
            return new ResponseBean(success, null);
        } else {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_FILE, "file.exist");
        }

    }
}
