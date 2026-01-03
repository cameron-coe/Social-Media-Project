import { BrowserRouter, Routes, Route } from 'react-router-dom';
import "./center-menu-component.css";
import PostsComponent from "../../posts/posts-component";
import PostDetailsPage from '../../posts/post-details-page-component';


function CenterMenuComponent() {
    return(
        <div className="center-menu-component">
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<PostsComponent />} />
                    <Route path="/post/:post_id" element={<PostDetailsPage />} />
                    <Route path="/post/:post_id/comment/:comment_id" element={<PostDetailsPage />} />
                </Routes>
            </BrowserRouter>
        </div>
    )
}

export default CenterMenuComponent;