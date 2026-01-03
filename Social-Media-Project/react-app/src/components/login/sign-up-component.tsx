import { useState } from "react";
import { useAuth } from "../../context/AuthContext";
import config from "../../config.json";

const API_BASE_URL = config.API_BASE_URL;

function SignUpComponent() {
    const [showOverlay, setShowOverlay] = useState(false);
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    const { setAuthToken, setAuthUsername, setAuthMemberId } = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Makes sure the passwords match before continuing
        if (password != confirmPassword) {
            setErrorMessage("The passwords do not match.");
            return;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/sign-up`, {
                method: "POST",
                body: JSON.stringify({ username, password }),
            });

            const data = await response.json();

            if(data.token) {
                // Saves information globally
                setAuthToken(data.token); 
                setAuthUsername(data.username);
                setAuthMemberId(data.memberId);

                // Reset the form and hide the overlay
                setShowOverlay(false);
                setUsername("");
                setPassword("");
                setConfirmPassword("");
                setErrorMessage("");
            }
            else {
                setErrorMessage("This username is already taken.");
            }

            
        } 
        catch (error) {
            console.error("Error logging in:", error);
            setErrorMessage("This username is already taken.");
        }
    };


    return (
       <>
            <div
                className="clickable-text underline-hover"
                onClick={() => setShowOverlay(true)}
            >
                Sign Up
            </div>

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
                        <h2>Sign Up</h2>
                        {errorMessage && <p className="error-text">{errorMessage}</p>}
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
                        <input
                            type="password"
                            placeholder="Confirm Password"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                        />

                        <button type="submit">Create Account</button>
                        <button type="button" onClick={() => setShowOverlay(false)}>
                            Cancel
                        </button>
                    </form>
                </div>
            )}

        </>
    )
}

export default SignUpComponent;