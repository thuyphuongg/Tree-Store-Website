package com.cdweb.Treestore.dto;

import lombok.*;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class BannerDto implements Serializable {
    private Long id;
    private String name;
    private String url;
}