package com.wmy.study.DearIMProject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.domain.FileBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IUploadFileService extends IService<FileBean> {

    boolean uploadAFile(MultipartFile file) throws IOException, BusinessException;
}
