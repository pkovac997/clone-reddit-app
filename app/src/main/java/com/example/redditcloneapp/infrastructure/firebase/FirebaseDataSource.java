package com.example.redditcloneapp.infrastructure.firebase;

import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.infrastructure.firebase.community.CommunityRepository;
import com.example.redditcloneapp.infrastructure.firebase.post.PostRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataSource {
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public void getAllCommunities(CommunityRepository.CommunitiesCallback callback) {
        database.collection(Community.COLLECTION_NAME)
                .orderBy(Community.Fields.createdAt)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->  {
                    var communities = queryDocumentSnapshots.toObjects(Community.class);
                    callback.onSuccess(communities);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getAllPosts(PostRepository.PostsCallback callback) {
        database.collection(Post.COLLECTION_NAME)
                .orderBy(Post.Fields.createdAt, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Post> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Post post = doc.toObject(Post.class);
                        post.setId(doc.getId()); // post.id = Firestore doc ID
                        posts.add(post);
                    }
                    callback.onSuccess(posts);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getPostsForCommunity(String communityId, PostRepository.PostsCallback callback) {
        database.collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.Fields.communityId, communityId)
                .orderBy(Post.Fields.createdAt, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Post> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Post post = doc.toObject(Post.class);
                        post.setId(doc.getId());
                        posts.add(post);
                    }
                    callback.onSuccess(posts);
                })
                .addOnFailureListener(callback::onError);
    }

    public void createPost(Post post, PostRepository.CreatePostCallback callback) {

        var documentReference = database.collection(Post.COLLECTION_NAME).document();

        documentReference.set(post)
                .addOnSuccessListener(x -> callback.onSuccess(post))
                .addOnFailureListener(callback::onError);
    }
}
