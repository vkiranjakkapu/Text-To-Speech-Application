import axios, { type AxiosRequestConfig } from "axios";
import { AppConfig } from "../config/AppConfig";
import { handleErrorResponse } from "../utils/ErrorHandler";
import configureRequestInterceptor from "./RequestInterceptor";
import configureResponseInterceptor from "./ResponseInterceptor";

const api = axios.create({
    baseURL: AppConfig.API_BASE_URL,
    timeout: 5000,
});

configureRequestInterceptor(api);
configureResponseInterceptor(api);

export default api;

export interface ApiResponse<T> {
    data: T;
    statusCode: number;
}

export type ErrorResponse = {
    errorName: string;
    errorCode: string;
    errorMessage: string;
    validationErrors: ValidationErrors[];
    timestamp: string;
};

export type ValidationErrors = {
    field: string;
    rejectedValue: object;
    message: string;
};

export type ApiClientProps = {
    type: "get" | "post" | "put" | "patch" | "delete";
    uri: string;
    service:
        | "identity"
        | "profile"
        | "speech"
        | "documents"
        | "history"
        | "ai"
        | "reports";
    payload?: unknown;
    config?: AxiosRequestConfig<unknown, unknown>;
    rawResponse?: boolean;
};

export async function apiClient<T>({
    type,
    uri,
    service,
    payload,
    config,
    rawResponse = false,
}: ApiClientProps): Promise<ApiResponse<T> | ErrorResponse> {
    try {
        let response, url;

        if (service == "identity") {
            url = AppConfig.IDENTITY_AUTH_URL;
        } else if (service == "profile") {
            url = AppConfig.IDENTITY_PROFILE_URL;
        } else if (service == "speech") {
            url = AppConfig.SPEECH_SERVICE_URL;
        } else if (service == "documents") {
            url = AppConfig.SPEECH_DOCUMENT_URL;
        } else if (service == "ai") {
            url = AppConfig.SPEECH_AI_URL;
        } else if (service == "history") {
            url = AppConfig.SPEECH_HISTORY_URL;
        } else {
            url = AppConfig.REPORTS_SERVICE_URL;
        }

        if (type.toLowerCase() == "post") {
            response = await api.post(url + uri, payload, config);
        } else if (type.toLowerCase() == "put") {
            response = await api.put(url + uri, payload, config);
        } else if (type.toLowerCase() == "patch") {
            response = await api.patch(url + uri, payload, config);
        } else if (type.toLowerCase() == "delete") {
            response = await api.delete(url + uri, config);
        } else {
            response = await api.get(url + uri, config);
        }

        if (rawResponse) {
            return {
                data: response.data as T,
                statusCode: response.status,
            };
        }

        const apiResponse = response.data as {
            status: string;
            data: T;
            timestamp: string;
        };

        return {
            data: apiResponse.data,
            statusCode: response.status,
        } as ApiResponse<T>;
    } catch (er) {
        return handleErrorResponse(er);
    }
}
