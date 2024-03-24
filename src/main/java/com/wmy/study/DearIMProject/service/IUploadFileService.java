package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.FileBean;
import com.wmy.study.DearIMProject.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface IUploadFileService extends IService<FileBean> {

    boolean uploadAFile(MultipartFile file) throws IOException, BusinessException;

    /**
     * 将文件放入桶中
     *
     * @param file     文件
     * @param user     用户
     * @param fileType 文件类型
     * @return 文件链接
     */
    FileBean uploadFileToBucket(MultipartFile file, User user, String fileType) throws IOException, NoSuchAlgorithmException, BusinessException;
}
