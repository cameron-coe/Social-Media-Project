import { CommentType } from "../../../models/comment";
import './comment-component.css';
import CommentsComponent from './comments-component';
import { useState, useEffect } from "react";
import LikeComponent from "../../like-component/like-component";
import { timeAgo } from "../../../utils/timeUtils";
import { useAuth } from "../../../context/AuthContext";

type Props = {
    comment: CommentType;
    rootPostId: number;
};

function CommentComponent({ comment, rootPostId }: Props) {
    const [forceShowComments, setForceShowComments] = useState(
        !!(comment.replies && comment.replies.length > 0) // only true if replies exist
    );
    const [forceReply, setForceReply] = useState(false);
    const { memberId } = useAuth();

    useEffect(() => {
        if (forceReply) {
            setForceReply(false); // reset so button can trigger again
        }
    }, [forceReply]);

    return (
        <div className="comment-card" >
            <p>Author: {comment.author}</p>
            <p className="comment-content">{comment.content}</p>
            <p>Likes: {comment.numOfLikes}</p>
            <p>Comment ID: {comment.id}</p>
            <p>Posted: {timeAgo(comment.createdOn)}</p>

            <div className="comment-options-container">
                <LikeComponent 
                    commentType='comment' 
                    id={comment.id} 
                    numOfLikesInput={comment.numOfLikes}
                    isLikedByUserInput={comment.likedByUser}
                ></LikeComponent>

                {memberId && (
                    <button onClick={() => setForceReply(true)}>
                        Reply
                    </button>
                )}
            </div>

            <div className="comment-divider"></div>

            <button onClick={() => setForceShowComments(!forceShowComments)}>
                {forceShowComments ? 'Hide Replies' : 'Show Replies'}
            </button>

            <div>
                <CommentsComponent 
                    commentType='comment' 
                    id={comment.id} 
                    rootPostId={rootPostId}
                    placeholder="Write a reply..." 
                    submitButtonText="Reply"
                    forceReplySectionActive={forceReply}
                    showComments={forceShowComments}
                    commentsInput={comment.replies}
                ></CommentsComponent>
            </div>
        </div>
    );
}

export default CommentComponent;