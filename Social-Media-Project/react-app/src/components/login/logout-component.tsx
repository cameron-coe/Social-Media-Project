import { useState } from "react";
import { useAuth } from "../../context/AuthContext";

function LogoutComponent() {
    const [showOverlay, setShowOverlay] = useState(false);

    const { setAuthToken, setAuthUsername, setAuthMemberId } = useAuth();

    const handleLogout = () => {
        // Clear global auth state
        setAuthToken(null);
        setAuthUsername(null);
        setAuthMemberId(null);

        setShowOverlay(false);
    };

    return (
        <>
            <div
                className="clickable-text underline-hover"
                onClick={() => setShowOverlay(true)}
            >
                Logout
            </div>

            {showOverlay && (
                <div
                    className="modal-overlay"
                    onClick={() => setShowOverlay(false)}
                >
                    <div
                        className="modal-form"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <h2>Are you sure you want to leave?</h2>

                        <button onClick={handleLogout}>Yes, Log out</button>
                        <button onClick={() => setShowOverlay(false)}>
                            Cancel
                        </button>
                    </div>
                </div>
            )}
        </>
    );
}

export default LogoutComponent;
