package com.wmy.study.DearIMProject.domain.gitee;

import lombok.Data;

@Data
public class GiteeuploadContentBean {
    private String name;
    private String path;
    private Integer size;
    private String sha;
    private String type;
    private String url;
    private String download_url;
}
