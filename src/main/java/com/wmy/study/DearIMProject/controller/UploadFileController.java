package com.wmy.study.DearIMProject.controller;

import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.FileBean;
import com.wmy.study.DearIMProject.domain.ResponseBean;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.service.IUploadFileService;
import com.wmy.study.DearIMProject.service.IUserService;
import com.wmy.study.DearIMProject.service.IUserTokenService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

@RestController
@RequestMapping("/file")
public class UploadFileController {

    @Resource
    private MessageSource messageSource;

    @Resource
    private IUploadFileService fileService;

    @Resource

    private IUserService userService;

    @PostMapping("/upload")
    public ResponseBean uploadFile(@RequestParam("file") MultipartFile file) throws IOException, BusinessException {
        if (file.isEmpty()) {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_FILE, "file.empty_file");
        }
        boolean success = fileService.uploadAFile(file);

        if (success) {
            return new ResponseBean(success, null);
        } else {
            //TODO: wmy 文件存在需要返回一个链接
            return new ResponseBean(false, ErrorCode.ERROR_CODE_FILE, "file.error");
        }

    }

    @PostMapping("/uploadBucket")
    public ResponseBean uploadFileToBucket(@RequestParam("file") MultipartFile file, String token, String fileType) throws Exception {
        if (fileType == null || fileType.isEmpty()) {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_FILE, "file.empty_file_type");
        }
        if (file.isEmpty()) {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_FILE, "file.empty_file");
        }
        User user = userService.getFromToken(token);
        FileBean resultFileBean = fileService.uploadFileToBucket(file, user, fileType);

        if (resultFileBean != null) {
            HashMap<String, Object> dataMap = new HashMap<>();
            dataMap.put("file", resultFileBean);
            return new ResponseBean(true, dataMap);
        } else {
            return new ResponseBean(false, ErrorCode.ERROR_CODE_FILE, "file.error");
        }
    }

}
