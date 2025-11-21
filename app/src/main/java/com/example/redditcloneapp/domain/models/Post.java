package com.example.redditcloneapp.domain.models;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor
@FieldNameConstants
public class Post {
    private String id;
    private String title;
    private String content;
    private String communityId;
    private String userId;
    private List<String> imageUrls;
    private List<String> userUpvotes;
    private List<String> userDownvotes;
    private Date createdAt;
}
