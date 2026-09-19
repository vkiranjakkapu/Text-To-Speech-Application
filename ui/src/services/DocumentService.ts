import { apiClient, type ApiResponse, type ErrorResponse } from "../api/api";

class DocumentService {
    async extractDocContent<T>(
        payload: unknown,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "post",
            service: "documents",
            uri: "/extract",
            payload,
        });
    }

    async getAllowedDocTypes<T>(): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "get",
            service: "documents",
            uri: "/support",
        });
    }
}

export default new DocumentService();
