import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import config from "../../config.json";
import { useLoginOverlay } from "../../context/loginOverlayContext";

const API_BASE_URL = config.API_BASE_URL;

function LoginComponent() {
    const { showLoginOverlay, setShowLoginOverlay } = useLoginOverlay();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const { setAuthToken, setAuthUsername, setAuthMemberId } = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await fetch(`${API_BASE_URL}/login`, {
                method: "POST",
                body: JSON.stringify({ username, password }),
            });

            const data = await response.json();

            // Saves information globally
            setAuthToken(data.token); 
            setAuthUsername(data.username);
            setAuthMemberId(data.memberId);

            // Reset the form and hide the overlay
            setShowLoginOverlay(false);
            setUsername("");
            setPassword("");
        } 
        catch (error) {
            console.error("Error logging in:", error);
        }
    };


    return (
        <>
            <div
                className="clickable-text underline-hover"
                onClick={() => setShowLoginOverlay(true)}
            >
                Login
            </div>

            {showLoginOverlay && (
                <div
                    className="modal-overlay"
                    onClick={() => setShowLoginOverlay(false)}
                >
                    <form
                        className="modal-form"
                        onClick={(e) => e.stopPropagation()} // Prevent closing when clicking inside modal
                        onSubmit={handleSubmit}
                    >
                        <h2>Login</h2>
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />

                        <button type="submit">Submit</button>
                        <button type="button" onClick={() => setShowLoginOverlay(false)}>
                            Cancel
                        </button>
                    </form>
                </div>
            )}

        </>
    );
}

export default LoginComponent;