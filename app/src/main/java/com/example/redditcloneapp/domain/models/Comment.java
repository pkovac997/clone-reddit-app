package com.example.redditcloneapp.domain.models;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor
@FieldNameConstants
public class Comment {

    public static String COLLECTION_NAME = "Comments";

    private String id;
    private String postId;
    private String userId;
    private String username;
    private String text;
    private Date createdAt;
}
