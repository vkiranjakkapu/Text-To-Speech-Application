import { apiClient, type ApiResponse, type ErrorResponse } from "../api/api";

class SpeechService {
    async downloadRecording<T>(
        speechId: string,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "get",
            service: "speech",
            uri: `/${speechId}/download`,
            rawResponse: true,
            config: {
                responseType: "blob",
            },
        });
    }
}

export default new SpeechService();
