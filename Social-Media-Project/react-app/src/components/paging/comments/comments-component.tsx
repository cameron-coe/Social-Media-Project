import { useEffect, useState } from "react";
import { CommentType } from "../../../models/comment";
import { useAuth } from "../../../context/AuthContext";
import CommentComponent from "./comment-component";
import config from "../../../config.json";
import "./comments-component.css";
import { useLoginOverlay } from "../../../context/loginOverlayContext";

const API_BASE_URL = config.API_BASE_URL;

interface CommentsComponentProps {
    commentType: "post" | "comment";
    id: number;
    rootPostId: number;
    placeholder: string;
    submitButtonText: string;
    forceReplySectionActive?: boolean;
    showComments?: boolean;
    commentsInput?: CommentType[];
}

function CommentsComponent({
    commentType,
    id,
    placeholder,
    submitButtonText,
    forceReplySectionActive = false,
    showComments = true,
    commentsInput,
    rootPostId,
}: CommentsComponentProps) {
    const [comments, setComments] = useState<CommentType[]>([]);
    const [newComment, setNewComment] = useState("");
    const [isReplySectionActive, setIsReplySectionActive] = useState(commentType === "post");
    const [areCommentsVisible, setAreCommentsVisible] = useState(showComments); // local state
    const { memberId, token } = useAuth();
    const { setShowLoginOverlay } = useLoginOverlay();

    useEffect(() => {
        if (forceReplySectionActive) {
            setIsReplySectionActive(true);
        }
    }, [forceReplySectionActive]);

    // Sync showComments prop to the local state
    useEffect(() => {
        setAreCommentsVisible(showComments);
    }, [showComments]);

    // Update when a key variable changes
    useEffect(() => {
        fetchComments();
    }, [commentType, id, commentsInput, showComments, memberId]);

    const promptLogin = () => {
        setShowLoginOverlay(true);
    };

    const fetchComments = () => {
        if (!showComments) {
            return;
        }

        if (commentsInput && commentsInput.length > 0) {
            setComments(commentsInput);
            return;
        }

        if (commentType === "post") {
            fetchCommentsForPost();
        } 
        else if (commentType === "comment") {
            fetchCommentsForComment();
        }
    };

    const fetchCommentsForPost = async () => {
        const body = {
            postId: id,
            memberId: memberId
        }
        try {
            const response = await fetch(`${API_BASE_URL}/get-comments-for-post-2`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(body),
            });

            const data = await response.json();

            setComments(data.response);
        } 
        catch (error) {
            console.error(`Error comments for post ${id}:`, error);
        }
    };

    const fetchCommentsForComment = async () => {
        const body = {
            commentId: id,
            memberId: memberId
        }
        try {
            const response = await fetch(`${API_BASE_URL}/get-comments-for-comment-2`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(body),
            });

            const data = await response.json();

            setComments(data.response);
        } 
        catch (error) {
            console.error(`Error comments for comment ${id}:`, error);
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            const body =
                commentType === "post"
                    ? { parentPostId: id, memberId, content: newComment } // for post comments
                    : { parentCommentId: id, memberId, content: newComment, rootPostId: rootPostId }; // for comment replies

            await fetch(
                commentType === "post"
                    ? `${API_BASE_URL}/new-comment-to-post`
                    : `${API_BASE_URL}/new-comment-to-comment`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify(body),
                }
            );

            setNewComment("");
            if (commentType !== "post") {
                setIsReplySectionActive(false);
            }
            
            if (!commentsInput || commentsInput.length === 0) {
                fetchComments();
            }
        } catch (error) {
            console.error("Error creating comment:", error);
        }
    };

    return (
        <div>
            <div className="add-space-below"></div>


                <form className="new-comment-form add-space-below" onSubmit={handleSubmit}>
                    {isReplySectionActive && (
                        <>
                            {memberId ? (
                                <input
                                    type="text"
                                    placeholder={placeholder}
                                    value={newComment}
                                    onChange={(e) => setNewComment(e.target.value)}
                                    onFocus={() => setIsReplySectionActive(true)}
                                    required
                                />
                            ) : (
                                // If user is not logged in, clicking the text input should prompt the login overlay
                                <input
                                    type="text"
                                    placeholder="Login to join the conversation"
                                    value=""
                                    readOnly
                                    onClick={promptLogin}
                                    onFocus={(e) => {
                                        e.target.blur(); // prevents cursor & keyboard
                                        promptLogin();
                                    }}
                                    className="login-input"
                                />
                            )}
                            

                            <div className="comment-actions">
                            {memberId && (
                                <>
                                    <button type="submit">{submitButtonText}</button>
                                    <button
                                        type="button"
                                        onClick={() => {
                                            setNewComment("");
                                            setIsReplySectionActive(commentType === "post");
                                        }}
                                    >
                                        Cancel
                                    </button>
                                </>

                            
                            )}
                            </div>
                        </>
                    )}
                </form>


            {areCommentsVisible && (
                <div className="comments-container">
                    {comments.map((comment, idx) => (
                        <div
                            key={comment.id}
                            className={`comment-wrapper ${idx === comments.length - 1 ? "last" : ""}`}
                        >
                            <CommentComponent 
                                comment={comment}
                                rootPostId={rootPostId}
                            ></CommentComponent>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}



export default CommentsComponent;
