package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.dao.IFileDao;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.FileBean;
import com.wmy.study.DearIMProject.service.IUploadFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UploadFileServiceImpl extends ServiceImpl<IFileDao, FileBean> implements IUploadFileService {
    @Value("${application.tempFilePath}")
    private String tempFilePath;

    @Override
    public boolean uploadAFile(MultipartFile file) throws IOException, BusinessException {
        // 上传文件
        //TODO: wmy 文件去重逻辑 如果服务端已经有这个文件了，那么直接返回这个文件的地址，成功
        if (file.isEmpty()) {
            return false;
        }
        //TODO: wmy 判断文件超过大小
        String filename = file.getOriginalFilename();
        String filePath = tempFilePath + filename;
        File dest = new File(filePath);
        if (!dest.exists()) {
            boolean createFileSuccess = dest.createNewFile();
            if (createFileSuccess) {
                file.transferTo(dest);
            }
            return createFileSuccess;
        } else {
            throw new BusinessException(ErrorCode.ERROR_CODE_FILE, "file.exist");
        }
    }
}
