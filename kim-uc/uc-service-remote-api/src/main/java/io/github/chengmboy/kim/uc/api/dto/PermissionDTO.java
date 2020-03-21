package io.github.chengmboy.kim.uc.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionDTO implements Serializable {
    private String name;

    private String url;

    private String method;
}
