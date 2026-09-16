import { apiClient, type ApiResponse, type ErrorResponse } from "../api/api";
import TokenStorage from "../storage/TokenStorage";

export interface LoginRequest {
    email: string;
    password: string;
}

export interface LoginResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
}

class AuthService {
    async getMe<T>(): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "get",
            service: "profile",
            uri: "/me",
        });
    }

    async login<T>(
        payload: LoginRequest,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "post",
            service: "identity",
            uri: "/login",
            payload,
        });
    }

    async refresh<T>(
        refreshToken: string,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "post",
            service: "identity",
            uri: "/refresh",
            payload: { refreshToken },
        });
    }

    async logout<T>(): Promise<ApiResponse<T> | ErrorResponse> {
        const refreshToken = TokenStorage.getRefreshToken();
        return apiClient({
            type: "post",
            service: "identity",
            uri: "/logout",
            payload: { refreshToken },
        });
    }
}

export default new AuthService();
