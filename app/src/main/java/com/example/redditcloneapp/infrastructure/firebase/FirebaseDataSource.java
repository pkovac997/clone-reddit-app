package com.example.redditcloneapp.infrastructure.firebase;

import com.example.redditcloneapp.domain.models.Comment;
import com.example.redditcloneapp.domain.models.Community;
import com.example.redditcloneapp.domain.models.Post;
import com.example.redditcloneapp.domain.models.User;
import com.example.redditcloneapp.infrastructure.firebase.common.DbCallback;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FirebaseDataSource {
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();

    public void getAllCommunities(DbCallback<List<Community>> callback) {
        database.collection(Community.COLLECTION_NAME)
                .orderBy(Community.Fields.createdAt)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->  {
                    var communities = new ArrayList<Community>();
                    for (var document : queryDocumentSnapshots) {
                        var community = document.toObject(Community.class);
                        community.setId(document.getId());
                    }
                    callback.onSuccess(communities);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getAllPosts(DbCallback<List<Post>> callback) {
        database.collection(Post.COLLECTION_NAME)
                .orderBy(Post.Fields.createdAt, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Post> posts = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Post post = doc.toObject(Post.class);
                        post.setId(doc.getId());
                        posts.add(post);

                        getUserById(post.getUserId(), new DbCallback<>() {
                            @Override
                            public void onSuccess(User entity) {
                                post.setUserUsername(entity.getUsername());
                            }

                            @Override
                            public void onError(Exception exception) {

                            }
                        });
                    }

                    callback.onSuccess(posts);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getPostsForCommunity(String communityId, DbCallback<List<Post>> callback) {
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

    public void getCommentsByPost(String postId, DbCallback<List<Comment>> callback) {
        database.collection(Comment.COLLECTION_NAME)
                .whereEqualTo(Comment.Fields.postId, postId)
                .orderBy(Comment.Fields.createdAt, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Comment> comments = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Comment comment = doc.toObject(Comment.class);
                        comment.setId(doc.getId());
                        comments.add(comment);
                    }
                    callback.onSuccess(comments);
                })
                .addOnFailureListener(callback::onError);
    }

    public void createPost(Post post, DbCallback<Post> callback) {
        var documentReference = database.collection(Post.COLLECTION_NAME).document();
        post.setCreatedAt(Date.from(Instant.now()));
        post.setId(documentReference.getId());

        documentReference.set(post)
                .addOnSuccessListener(x -> callback.onSuccess(post))
                .addOnFailureListener(callback::onError);
    }

    public void createCommunity(Community community, DbCallback<Community> callback) {
        var documentReference = database.collection(Community.COLLECTION_NAME).document();
        community.setId(documentReference.getId());
        documentReference.set(community)
                .addOnSuccessListener(x -> callback.onSuccess(community))
                .addOnFailureListener(callback::onError);
    }


    public void getPostById(String postId, DbCallback<Post> callback) {
        database.collection(Post.COLLECTION_NAME)
                .document(postId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    var post = querySnapshot.toObject(Post.class);
                    callback.onSuccess(post);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getPostByCommunityFromUserId(String userId, DbCallback<List<Post>> callback) {

        database.collection(User.COLLECTION_NAME)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    var communityIds = documentSnapshot.toObject(User.class).getCommunityFollows();

                    database.collection(Post.COLLECTION_NAME)
                            .whereIn(Post.Fields.communityId, communityIds)
                            .orderBy(Post.Fields.createdAt, Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                var posts = queryDocumentSnapshots
                                        .getDocuments()
                                        .stream()
                                        .map(snapshot -> snapshot.toObject(Post.class))
                                        .collect(Collectors.toList());
                                callback.onSuccess(posts);
                            })
                            .addOnFailureListener(callback::onError);
                }).addOnFailureListener(callback::onError);
    }

    public void getUserById(String userId, DbCallback<User> callback) {
        database.collection(User.COLLECTION_NAME)
                .whereEqualTo(User.Fields.id, userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    var user = getDocument(queryDocumentSnapshots).toObject(User.class);
                    user.setId(getDocumentId(queryDocumentSnapshots));
                    callback.onSuccess(user);
                })
                .addOnFailureListener(callback::onError);
    }

    public void createUser(User user, DbCallback<User> callback) {
        database.collection(User.COLLECTION_NAME)
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(x -> callback.onSuccess(user))
                .addOnFailureListener(callback::onError);
    }

    public void getCommunityById(String communityId, DbCallback<Community> callback) {
        database.collection(Community.COLLECTION_NAME)
                .document(communityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    var community = documentSnapshot.toObject(Community.class);
                    callback.onSuccess(community);
                })
                .addOnFailureListener(callback::onError);
    }

    public void ensureDefaultCommunityExists() {
        database.collection(Community.COLLECTION_NAME)
                .document("DEFAULT")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // Ako ne postoji – kreiraj
                        var community = new Community();
                        community.setId("DEFAULT");
                        community.setName("News");
                        community.setDescription("Get the latest news, always - right here!");
                        community.setAdminUser("N/A");
                        community.setFollowers(List.of());
                        community.setCreatedAt(Date.from(Instant.now()));

                        database.collection(Community.COLLECTION_NAME)
                                .document("DEFAULT")
                                .set(community, SetOptions.merge());
                    }
                });
    }

    public void getDefaultCommunity(DbCallback<Community> callback) {
        database
                .collection(Community.COLLECTION_NAME)
                .document("DEFAULT")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    var community = documentSnapshot.toObject(Community.class);
                    community.setId(documentSnapshot.getId());
                    callback.onSuccess(community);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getCommunitiesByUser(String userId, DbCallback<List<Community>> callback) {
        database.collection(Community.COLLECTION_NAME)
                .whereArrayContains(Community.Fields.followers, userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    var communities = new ArrayList<Community>();
                    for (var document : queryDocumentSnapshots.getDocuments()) {
                        var community = document.toObject(Community.class);
                        community.setId(document.getId());
                        communities.add(community);
                    }
                    callback.onSuccess(communities);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getCommunities(List<String> communityIds, DbCallback<List<Community>> callback) {
        database.collection(Community.COLLECTION_NAME)
                .whereIn(Community.Fields.id, communityIds)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    var communities = new ArrayList<Community>();
                    for (var document : queryDocumentSnapshots.getDocuments()) {
                        var community = document.toObject(Community.class);
                        community.setId(document.getId());
                        communities.add(community);
                    }
                    callback.onSuccess(communities);
                })
                .addOnFailureListener(callback::onError);
    }

    public void userFollowsDefaultCommunity(String userId) {
        database.collection(Community.COLLECTION_NAME)
                .document("DEFAULT")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        var community = documentSnapshot.toObject(Community.class);

                        if (community.getFollowers().isEmpty()) {
                            community.setFollowers(List.of(userId));
                        }
                        else {
                            community.getFollowers().add(userId);
                        }

                        database.collection(Community.COLLECTION_NAME)
                                .document("DEFAULT")
                                .set(community, SetOptions.merge());
                    }
                });
    }

    private DocumentSnapshot getDocument(QuerySnapshot queryDocumentSnapshots) {
        return queryDocumentSnapshots.getDocuments().getFirst();
    }

    private String getDocumentId(QuerySnapshot queryDocumentSnapshots) {
        return queryDocumentSnapshots.getDocuments().getFirst().getId();
    }

    public void createComment(Comment comment, DbCallback<Comment> callback) {

        var commentDocumentReference = database.collection(Comment.COLLECTION_NAME)
                .document();

        comment.setId(commentDocumentReference.getId());
        commentDocumentReference.set(comment);

        database.collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.Fields.id, comment.getPostId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    var post = queryDocumentSnapshots.getDocuments().getFirst().toObject(Post.class);

                    if (post.getComments().isEmpty()) {
                        post.setComments(List.of(comment.getId()));
                    }
                    else {
                        post.getComments().add(comment.getId());
                    }

                    callback.onSuccess(comment);

                    database.collection(Post.COLLECTION_NAME)
                            .document(post.getId())
                            .set(post, SetOptions.merge());
                })
                .addOnFailureListener(callback::onError);
    }

    public void userFollowingCommunities(String userId, DbCallback<List<Community>> callback) {
        database.collection(User.COLLECTION_NAME)
                        .document(userId)
                        .get()
                .addOnSuccessListener(documentSnapshot ->  {
                    var user = documentSnapshot.toObject(User.class);

                    database.collection(Community.COLLECTION_NAME)
                            .whereIn(Community.Fields.id, user.getCommunityFollows())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                var communities = new ArrayList<Community>();
                                for (var document : queryDocumentSnapshots.getDocuments()) {
                                    var community = document.toObject(Community.class);
                                    communities.add(community);
                                }
                                callback.onSuccess(communities);
                            })
                            .addOnFailureListener(callback::onError);
                });
    }

    public void userNotFollowingCommunities(String userId, DbCallback<List<Community>> callback) {
        database.collection(User.COLLECTION_NAME)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot ->  {
                    var user = documentSnapshot.toObject(User.class);

                    database.collection(Community.COLLECTION_NAME)
                            .get()
                            .addOnSuccessListener(docSnapshot -> {
                                var communities = new ArrayList<Community>();
                                for (var document : docSnapshot.getDocuments()) {
                                    var community = document.toObject(Community.class);

                                    if (!user.getCommunityFollows().contains(community.getId())) {
                                        communities.add(community);
                                    }
                                }
                                callback.onSuccess(communities);
                            })
                            .addOnFailureListener(callback::onError);
                });
    }

    public void followCommunity(String userId, String communityId, DbCallback<Community> callback) {
        database.collection(Community.COLLECTION_NAME)
                .document(communityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    var community = documentSnapshot.toObject(Community.class);

                    if (community.getFollowers() == null) {
                        community.setFollowers(List.of(userId));
                    }
                    else if (!community.getFollowers().contains(userId)){
                        community.getFollowers().add(userId);
                    }

                    database.collection(Community.COLLECTION_NAME)
                            .document(communityId)
                            .set(community, SetOptions.merge());

                    database.collection(User.COLLECTION_NAME)
                            .document(userId)
                            .get()
                            .addOnSuccessListener(snapShot -> {
                                var user = snapShot.toObject(User.class);

                                if (user.getCommunityFollows().size() == 0) {
                                    user.setCommunityFollows(List.of(communityId));
                                } else if (!user.getCommunityFollows().contains(communityId)) {
                                    user.getCommunityFollows().add(communityId);

                                    database.collection(User.COLLECTION_NAME)
                                            .document(userId)
                                            .set(user, SetOptions.merge());

                                    callback.onSuccess(community);
                                }
                            });
                })
                .addOnFailureListener(callback::onError);
    }

    public void unfollowCommunity(String userId, String communityId, DbCallback<Community> callback) {
        database.collection(Community.COLLECTION_NAME)
                .document(communityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    var community = documentSnapshot.toObject(Community.class);

                    var following = community.getFollowers();

                    if (following.contains(userId)) {
                        following.remove(userId);

                        database.collection(Community.COLLECTION_NAME)
                                .document(communityId)
                                .set(community, SetOptions.merge());

                        database.collection(User.COLLECTION_NAME)
                                .document(userId)
                                .get()
                                .addOnSuccessListener(snapShot -> {
                                    var user = snapShot.toObject(User.class);

                                    if (user.getCommunityFollows().contains(communityId)) {
                                        user.getCommunityFollows().remove(communityId);

                                        database.collection(User.COLLECTION_NAME)
                                                .document(userId)
                                                .set(user, SetOptions.merge())
                                                .addOnSuccessListener(x -> callback.onSuccess(community));
                                    }
                                });
                    }
                }).addOnFailureListener(callback::onError);
    }

    public void upvotePost(String userId, String postId, DbCallback<Post> callback) {
        database.collection(Post.COLLECTION_NAME)
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    var post = documentSnapshot.toObject(Post.class);

                    if (post.getUserDownvotes() != null && post.getUserDownvotes().contains(userId)) {
                        post.getUserDownvotes().remove(userId);
                    }

                    if (post.getUserUpvotes() == null || post.getUserUpvotes().size() == 0) {
                        post.setUserUpvotes(List.of(userId));
                    } else if (!post.getUserUpvotes().contains(userId)) {
                        post.getUserUpvotes().add(userId);
                    }

                    database.collection(Post.COLLECTION_NAME)
                            .document(postId)
                            .set(post, SetOptions.merge())
                            .addOnSuccessListener(x -> {
                                callback.onSuccess(post);
                            });
                })
                .addOnFailureListener(callback::onError);
    }

    public void downvotePost(String userId, String postId, DbCallback<Post> callback) {
        database.collection(Post.COLLECTION_NAME)
                .document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    var post = documentSnapshot.toObject(Post.class);

                    if (post.getUserUpvotes() != null && post.getUserUpvotes().contains(userId)) {
                        post.getUserUpvotes().remove(userId);
                    }

                    if (post.getUserDownvotes() == null || post.getUserDownvotes().size() == 0) {
                        post.setUserDownvotes(List.of(userId));
                    } else if (!post.getUserDownvotes().contains(userId)) {
                        post.getUserDownvotes().add(userId);
                    }

                    database.collection(Post.COLLECTION_NAME)
                            .document(postId)
                            .set(post, SetOptions.merge())
                            .addOnSuccessListener(x -> {
                                callback.onSuccess(post);
                            });
                })
                .addOnFailureListener(callback::onError);
    }
}
