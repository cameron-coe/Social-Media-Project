import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import config from "../../config.json";
import { usePosts } from "../../context/PostsContext";
import { PostType } from "../../models/post";

const API_BASE_URL = config.API_BASE_URL;

function NewPostComponent() {
    const [showOverlay, setShowOverlay] = useState(false);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");

    const { addPost } = usePosts();
    const {fetchPosts} = usePosts();
    const { memberId, token } = useAuth();

    const tag = "All";

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await fetch(`${API_BASE_URL}/new-post`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ memberId, title, content, tag }),
            });

            const data = await response.json();

            if (data.response) {
                // const newPost: PostType = {
                //     id: 0,
                //     title: title,
                //     author: "",
                //     content: content,
                //     numOfLikes: 1,
                //     comments: [],
                //     likedByUser: true
                // }
                // addPost(newPost)
                fetchPosts();
            }

            // Reset the form and hide the overlay
            setShowOverlay(false);
            setTitle("");
            setContent("");
        } 
        catch (error) {
            console.error("Error creating post:", error);
        }
    };


    return (
    <>
        {memberId && (
            <>
                <div
                    className="clickable-text underline-hover"
                    onClick={() => setShowOverlay(true)}
                >
                    New Post
                </div>
                <>
                    {showOverlay && (
                        <div
                            className="modal-overlay"
                            onClick={() => setShowOverlay(false)}
                        >
                            <form
                                className="modal-form"
                                onClick={(e) => e.stopPropagation()} // Prevent closing when clicking inside modal
                                onSubmit={handleSubmit}
                            >
                                <h2>New Post</h2>
                                <input
                                    type="text"
                                    placeholder="Title"
                                    value={title}
                                    onChange={(e) => setTitle(e.target.value)}
                                    required
                                />
                                <textarea
                                    placeholder="Content"
                                    value={content}
                                    onChange={(e) => setContent(e.target.value)}
                                    rows={6}
                                    required
                                />
                                <button type="submit">Submit</button>
                                <button
                                    type="button"
                                    onClick={() => setShowOverlay(false)}
                                >
                                    Cancel
                                </button>
                            </form>
                        </div>
                    )}
                </>
            </>
        )}
    </>
);

}

export default NewPostComponent;
