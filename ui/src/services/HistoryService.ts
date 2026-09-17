import { apiClient, type ApiResponse, type ErrorResponse } from "../api/api";
import type { UserProfile } from "../context/usePrincipal";

class HistoryService {
    async getHistory<T>(): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "get",
            service: "history",
            uri: "/",
        });
    }

    async deleteRecording<T>(
        historyId: string,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "delete",
            service: "history",
            uri: "/" + historyId,
        });
    }
}

export default new HistoryService();

export type SpeechHistory = {
    id: string;
    ownerId: string;
    user: UserProfile;
    text: string;
    language: string;
    voice: string;
    audioPath: string;
    createdAt: string;
};
