package com.wmy.study.DearIMProject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wmy.study.DearIMProject.Exception.BusinessException;
import com.wmy.study.DearIMProject.Utils.FileUtils;
import com.wmy.study.DearIMProject.dao.IFileDao;
import com.wmy.study.DearIMProject.domain.ErrorCode;
import com.wmy.study.DearIMProject.domain.FileBean;
import com.wmy.study.DearIMProject.domain.User;
import com.wmy.study.DearIMProject.domain.gitee.GiteeUploadResponseBean;
import com.wmy.study.DearIMProject.service.IUploadFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UploadFileServiceImpl extends ServiceImpl<IFileDao, FileBean> implements IUploadFileService {
    //    @Value("${application.tempFilePath}")
//    private String tempFilePath;
    final static long MAX_FILE_SIZE = 100 * 1024 * 1024;

    @Value("${application.tempFilePath}")
    private String tempFilePath; // 文件名称
    @Value("${application.gitee.accessToken}")
    private String accessToken; // 令牌
    @Value("${application.gitee.owner}")
    private String owner; // 令牌

    @Value("${application.gitee.repo}")
    private String repo; // 令牌

//    private WebClient webClient;

    private final RestTemplate restTemplate = new RestTemplate();


//    UploadFileServiceImpl() {
//        restTemplate = new RestTemplate();
//    }


    @Override
    public boolean uploadAFile(MultipartFile file) throws IOException, BusinessException {
        // 上传文件
        //TODO: wmy 文件去重逻辑 如果服务端已经有这个文件了，那么直接返回这个文件的地址，成功
        if (file.isEmpty()) {
            return false;
        }
        //        https://gitee.com/api/v5/repos/{owner}/{repo}/contents/{path}

        // 判断文件超过大小
        long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.ERROR_CODE_FILE, "file.over_size");
        }

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

    @Override
    public FileBean uploadFileToBucket(MultipartFile file, User user, String fileType) throws Exception {
        // 上传文件
        //TODO: wmy 文件去重逻辑 如果服务端已经有这个文件了，那么直接返回这个文件的地址，成功
        if (file.isEmpty()) {
            return null;
        }
        //        https://gitee.com/api/v5/repos/{owner}/{repo}/contents/{path}

        // 判断文件是否存在
        String fileMd5 = FileUtils.caculateMd5(file);
        QueryWrapper<FileBean> existQuery = new QueryWrapper<>();
        existQuery.eq("file_md5", fileMd5);
        FileBean fileBean = getOne(existQuery);
        if (fileBean != null) {
            return fileBean;
        }

        // 判断文件超过大小
        long size = file.getSize();
        log.info("size = " + size);
        if (size > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.ERROR_CODE_FILE, "file.over_size");
        }

//            String uri = "https://gitee.com/api/v5/repos/{owner}/{repo}/contents/{path}";
        String uri = String.format("https://gitee.com/api/v5/repos/%s/%s/contents/%s", owner, repo, fileType + "/" + fileMd5);
        log.info("uri = " + uri);
        Map<String, Object> requestMap = new HashMap<String, Object>();
        requestMap.put("content", FileUtils.encodeFileToBase64(file));
        requestMap.put("message", "update file " + fileMd5 + " to " + fileType);
        requestMap.put("access_token", accessToken);
        ResponseEntity<GiteeUploadResponseBean> responseMap;
        try {
            responseMap = restTemplate.postForEntity(uri, requestMap, GiteeUploadResponseBean.class);

        } catch (HttpClientErrorException e) {
            HashMap errorMap = e.getResponseBodyAs(HashMap.class);
            assert errorMap != null;
            String message = (String) errorMap.get("message");
            throw new BusinessException(ErrorCode.ERROR_CODE_FILE, message);
        }
        GiteeUploadResponseBean responseMapBody = responseMap.getBody();

        assert responseMapBody != null;
        String link = responseMapBody.getContent().getDownload_url();
        FileBean sqlFileBean = new FileBean();
        sqlFileBean.setFilePath(link);
        sqlFileBean.setFileMd5(fileMd5);
        // 获取宽高
        List<Integer> list = FileUtils.imageWidth(file);
        sqlFileBean.setWidth(list.get(0));
        sqlFileBean.setHeight(list.get(1));
        save(sqlFileBean);
        return sqlFileBean;

    }
}
