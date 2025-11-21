package com.example.redditcloneapp.domain.models;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor
@FieldNameConstants
public class User {

    private String id;
    private String email;
    private String username;
    private String profileImageUrl;
    private List<String> communityFollows;
    private List<String> userFollows;
    private Date createdAt;
}