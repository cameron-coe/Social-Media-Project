import { createContext, useContext, useEffect, useState } from "react";
import { PostType } from "../models/post";
import config from "../config.json";
import { useAuth } from "./AuthContext";

const API_BASE_URL = config.API_BASE_URL;

type PostsContextType = {
    posts: PostType[];
    fetchPosts: () => Promise<void>;
    addPost: (post: PostType) => void;
    clearPosts: () => void;
};

const PostsContext = createContext<PostsContextType | null>(null);

export function PostsProvider({ children }: { children: React.ReactNode }) {
    const [posts, setPosts] = useState<PostType[]>([]);
    const { memberId, token } = useAuth();

    const fetchPosts = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/get-posts-2`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ memberId }),
            });

            const data = await response.json();
            setPosts(data.response);
        } catch (error) {
            console.error("Error fetching posts:", error);
        }
    };

    const addPost = (post: PostType) => {
        // prepend so new posts appear at the top
        setPosts((prev) => [post, ...prev]);
    };

    const clearPosts = () => {
        setPosts([]);
    };

    // Auto-fetch when auth changes
    useEffect(() => {
        fetchPosts();
    }, [memberId, token]);

    return (
        <PostsContext.Provider
            value={{ posts, fetchPosts, addPost, clearPosts }}
        >
            {children}
        </PostsContext.Provider>
    );
}

export function usePosts() {
    const context = useContext(PostsContext);
    if (!context) {
        throw new Error("usePosts must be used within a PostsProvider");
    }
    return context;
}
