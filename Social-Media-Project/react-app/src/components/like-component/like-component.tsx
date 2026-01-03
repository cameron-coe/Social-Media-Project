import { useEffect, useState } from "react";
import config from "../../config.json";
import { useAuth } from "../../context/AuthContext";

const API_BASE_URL = config.API_BASE_URL;

interface LikeComponentProps {
    commentType: "post" | "comment";
    id: number;
    numOfLikesInput: number;
    isLikedByUserInput?: boolean;
}

function LikeComponent({ commentType, id, numOfLikesInput, isLikedByUserInput }: LikeComponentProps) {
    const [isLikedByUser, setIsLikedByUser] = useState(isLikedByUserInput);
    const [numOfLikes, setNumOfLikes] = useState(numOfLikesInput);

    const { memberId, token } = useAuth();

    // Set values when a prop changes
    useEffect(() => {
        setIsLikedByUser(isLikedByUserInput);
        setNumOfLikes(numOfLikesInput);
    }, [memberId, numOfLikesInput, isLikedByUserInput]);

    

    const handleLikeToggle = async (e: React.MouseEvent<HTMLButtonElement>) => {
        e.stopPropagation();

        if (!memberId || !token) return;

        const body = { id: id, userId: memberId };

        const endpoint = commentType === "post"
            ? isLikedByUser
                ? `${API_BASE_URL}/unlike-post`
                : `${API_BASE_URL}/like-post`
            : isLikedByUser
                ? `${API_BASE_URL}/unlike-comment`
                : `${API_BASE_URL}/like-comment`;

        try {
            toggleLikeButton();

            const res = await fetch(endpoint, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(body),
            });

            if (!res.ok) {
                toggleLikeButton(); // revert if failed
                console.error("Failed to update like status");
            }
        } catch (err) {
            console.error("Error updating like status:", err);
        }
    };

    function toggleLikeButton() {
        const newNumberOfLikes = isLikedByUser ? numOfLikes - 1 : numOfLikes + 1;

        setIsLikedByUser(prevIsLikedState => {
            setNumOfLikes(newNumberOfLikes);
            return !prevIsLikedState;
        });
    }

    return (
        <div>
            {memberId ? (
                <button onClick={handleLikeToggle}>
                    {numOfLikes} {numOfLikes === 1 ? "Like" : "Likes"} | {isLikedByUser ? "👍 Liked" : "👎 Unliked"}
                </button>
            ) : (
                <button>
                    {numOfLikes} {numOfLikes === 1 ? "Like" : "Likes"}
                </button>
            )}
        </div>
    );
}

export default LikeComponent;
