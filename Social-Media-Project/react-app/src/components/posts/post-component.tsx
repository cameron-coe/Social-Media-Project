import { useNavigate } from 'react-router-dom';
import { PostType } from "../../models/post";
import LikeComponent from '../like-component/like-component';

//Styling Sheet
import './post-component.css';

type Props = {
    post: PostType;
};

function PostComponent({ post }: Props) {
    const navigate = useNavigate();

    const goToPostPage = () => {
        navigate(`/post/${post.id}`);
    };

    return (
        <div
            className="post-card clickable"
            onClick={goToPostPage}
        >
            <p>{post.author}</p>
            <h2>{post.title}</h2>
            <p className="main-content">{post.content}</p>

            <p>{post.numberOfComments || 0} Comments</p>

            <LikeComponent 
                commentType='post' 
                id={post.id} 
                numOfLikesInput={post.numOfLikes}
                isLikedByUserInput={post.likedByUser}
            ></LikeComponent>
        </div>
    );
}

export default PostComponent;
