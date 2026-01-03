import { Link, useParams } from 'react-router-dom';
import { useEffect, useState } from "react";
import { PostType } from "../../models/post";
import config from "../../config.json";
import CommentsComponent from '../paging/comments/comments-component';
import LikeComponent from '../like-component/like-component';
import { useAuth } from '../../context/AuthContext';

const API_BASE_URL = config.API_BASE_URL;

function PostDetailsPage() {
    const postId = useParams().post_id;
    const [post, setPost] = useState<PostType | null>(null);
    const { memberId, token } = useAuth();


    const fetchPost = async () => {
        const body = {
            postId: postId,
            memberId: memberId
        }
        try {
            const response = await fetch(`${API_BASE_URL}/get-post-2`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(body),
            });

            const data = await response.json();

            setPost(data.response);
        } 
        catch (error) {
            console.error(`Error getting post ${postId}:`, error);
        }
    };


    useEffect(() => {
        fetchPost();
    }, [postId, memberId]);

    if (!post) {
        return <div>Loading post...</div>;
    }

    return (
        <div style={{ padding: '24px' }}>
            <Link to={`/`}>Go Back</Link>
            <p>Author: {post.author}</p>
            <h2>{post.title}</h2>
            <p className="main-content">{post.content}</p>
            <LikeComponent 
                commentType='post' 
                id={post.id} 
                numOfLikesInput={post.numOfLikes}
                isLikedByUserInput={post.likedByUser}
            ></LikeComponent>

            <p>{post.numberOfComments || 0} Comments</p>

            <CommentsComponent 
                commentType='post' 
                id={post.id} 
                rootPostId={post.id}
                placeholder="Write a comment..." 
                submitButtonText="Comment"
            ></CommentsComponent>
        </div>

        
    );
}

export default PostDetailsPage;
