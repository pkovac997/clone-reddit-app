package com.example.redditcloneapp.domain.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor
@FieldNameConstants
public class Location {

    public static String COLLECTION_NAME = "Communities";

    private String id;

    //TODO Add location specific data that will be related to Post model
}
