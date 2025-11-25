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

    public static String COLLECTION_NAME = "Posts";

    private String id;
    private String title;
    private String content;
    private String communityId;
    private String communityName;
    private String userId;
    private String userUsername;
    private List<String> imageUrls;
    private List<String> userUpvotes;
    private List<String> userDownvotes;
    private Date createdAt;
    private List<String> comments;
}
