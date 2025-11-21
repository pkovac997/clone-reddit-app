package com.example.redditcloneapp.domain.models;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor
@FieldNameConstants
public class Community {
    private String id;
    private String name;
    private String description;
    private String coverImageUrl;
    private String adminUser;
    private List<String> followers;
    private Date createdAt;
}
