import { createContext, useContext, useState, ReactNode } from "react";

type AuthContextType = {
    token: string | null;
    setAuthToken: (token: string | null) => void;
    username: string | null;
    setAuthUsername: (username: string | null) => void;
    memberId: number | null;
    setAuthMemberId: (memberId: number | null) => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [token, setAuthToken] = useState<string | null>(null);
    const [username, setAuthUsername] = useState<string | null>(null);
    const [memberId, setAuthMemberId] = useState<number | null>(null);

    return (
        <AuthContext.Provider value={{ token, setAuthToken, username, setAuthUsername, memberId, setAuthMemberId }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};