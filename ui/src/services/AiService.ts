import { apiClient, type ApiResponse, type ErrorResponse } from "../api/api";

class AiService {
    async aiEnhancement<T>(
        payload: unknown,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "post",
            service: "ai",
            uri: "/enhance",
            payload,
        });
    }
}

export default new AiService();
