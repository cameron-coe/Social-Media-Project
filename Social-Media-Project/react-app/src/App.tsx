import { AuthProvider } from "./context/AuthContext";
import CenterMenuComponent from "./components/paging/center-menu/center-menu-component";
import LeftMenuComponent from "./components/paging/left-menu-component";
import RightMenuComponent from "./components/paging/right-menu-component";
import HeaderComponent from "./components/paging/header/header-component";

// Styling for the App Page
import './App-gridding.css';
import './App-coloring.css';
import './App-styling-toolkit.css';

// Other global styling sheets
import './global-styling-sheets/modal-overlay.css';
import './global-styling-sheets/interactables.css';
import './global-styling-sheets/scrollable.css';
import './global-styling-sheets/text.css';
import { LoginOverlayProvider } from "./context/loginOverlayContext";
import { PostsProvider } from "./context/PostsContext";




function App() {
  return (
    <LoginOverlayProvider>
      <AuthProvider>
        <PostsProvider>
          <div className="app-container">
            <div className="header">
              <HeaderComponent></HeaderComponent>
            </div>

            <div className="left-menu">
              <LeftMenuComponent></LeftMenuComponent>
            </div>

            <div className="center-menu scrollable">
              <CenterMenuComponent />
            </div>

            <div className="right-menu">
              <RightMenuComponent />
            </div>

          </div>
        </PostsProvider>
      </AuthProvider>
    </LoginOverlayProvider>
  );
}

export default App;