import PostComponent from "./post-component";
import { usePosts } from "../../context/PostsContext";

function PostsComponent() {
    const { posts } = usePosts();

    return (
        <div>
            {posts.map((post) => (
                <PostComponent key={post.id} post={post} />
            ))}
        </div>
    );
}

export default PostsComponent;
