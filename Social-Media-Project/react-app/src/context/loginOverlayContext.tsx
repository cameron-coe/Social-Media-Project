import { createContext, useContext, useState, ReactNode } from "react";

type LoginOverlayContextType = {
    showLoginOverlay: boolean;
    setShowLoginOverlay: (value: boolean) => void;
};

const LoginOverlayContext = createContext<LoginOverlayContextType | null>(null);

export function LoginOverlayProvider({ children }: { children: ReactNode }) {
    const [showLoginOverlay, setShowLoginOverlay] = useState(false);

    return (
        <LoginOverlayContext.Provider value={{ showLoginOverlay, setShowLoginOverlay }}>
            {children}
        </LoginOverlayContext.Provider>
    );
}

export function useLoginOverlay() {
    const ctx = useContext(LoginOverlayContext);
    if (!ctx) throw new Error("useLoginOverlay must be used inside LoginOverlayProvider");
    return ctx;
}
