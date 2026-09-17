import { createContext, useContext } from "react";
import type { LoginRequest, LoginResponse } from "../services/AuthService";
import type { ApiResponse, ErrorResponse } from "../api/api";

export type UserProfile = {
    id: string;
    email: string;
    name: string;
    gender: UserGender;
    firstName: string;
    lastName: string;
    phone: string;
    address: Address;
    dob: string;
    roles: RoleType[];
};

export const UserGender = {
    MALE: "MALE",
    FEMALE: "FEMALE",
    NON_DISCLOSED: "NON_DISCLOSED",
} as const;
export type UserGender = (typeof UserGender)[keyof typeof UserGender];

export const RoleType = {
    ADMIN: "ADMIN",
    USER: "USER",
} as const;
export type RoleType = (typeof RoleType)[keyof typeof RoleType];

export type Address = {
    id: number;
    street: string;
    city: string;
    pinCode: string;
    state: string;
    country: string;
    deleted: boolean;
};

export const AuthStatus = {
    INITIALIZING: "INITIALIZING",
    AUTHENTICATED: "AUTHENTICATED",
    UNAUTHENTICATED: "UNAUTHENTICATED",
} as const;
export type AuthStatus = (typeof AuthStatus)[keyof typeof AuthStatus];

export type PrincipalContext = {
    profile: UserProfile | null;
    status: AuthStatus;
    isAdmin: boolean;
    isLoggedIn: boolean;
    login: (
        request: LoginRequest,
    ) => Promise<ApiResponse<LoginResponse> | ErrorResponse>;
    logout: () => void;
};

export const PrincipalContext = createContext<PrincipalContext | null>(
    {} as PrincipalContext,
);

export default function usePrincipal() {
    const context = useContext(PrincipalContext);
    if (context === null) throw new Error("Principal Context Was Null");
    return context;
}
