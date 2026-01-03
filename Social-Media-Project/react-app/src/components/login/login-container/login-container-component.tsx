import LoginComponent from "../login-component";
import SignUpComponent from "../sign-up-component";
import { useAuth } from "../../../context/AuthContext"; 

import './login-container.css';
import LogoutComponent from "../logout-component";

function LoginContainerComponent() {
    const { username } = useAuth();

    return (
        <div className="login-container">
            { username ? (
                <>
                    <div>Hello, {username}</div>
                    <div>|</div>
                    <LogoutComponent></LogoutComponent>
                </>
            ) : (
                <>
                    <LoginComponent></LoginComponent>
                    <div>|</div>
                    <SignUpComponent></SignUpComponent>
                </>
            )}
        </div>
    )
}

export default LoginContainerComponent;