import { apiClient, type ApiResponse, type ErrorResponse } from "../api/api";

class SpeechService {
    async synthesise<T>(
        payload: unknown,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "post",
            service: "speech",
            uri: "/synthesize",
            payload,
            rawResponse: true,
            config: {
                responseType: "blob"
            }
        });
    }
    async getVoiceOptions<T>(): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "get",
            service: "speech",
            uri: "/voices",
        });
    }

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

export type SynthesisResponse = {
    error: ErrorResponse;
    suggestion: string;
    text: string;
};

export const SynthesisType = {
    TEXT: "TEXT",
    DOCUMENT: "DOCUMENT",
} as const;

export type SynthesisType = (typeof SynthesisType)[keyof typeof SynthesisType];

export const EnhancementType = {
    ENHANCE: "ENHANCE",
    SUMMARISE: "SUMMARISE",
    REDUCE: "REDUCE",
} as const;

export type EnhancementType =
    (typeof EnhancementType)[keyof typeof EnhancementType];
