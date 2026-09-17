import type { AxiosInstance, InternalAxiosRequestConfig } from "axios";
import TokenStorage from "../storage/TokenStorage";
import { AppConfig } from "../config/AppConfig";

export default function configureRequestInterceptor(api: AxiosInstance) {
    api.interceptors.request.use(
        (config: InternalAxiosRequestConfig) => {
            if (
                AppConfig.PUBLIC_ENDPOINTS.some((path) =>
                    config.url?.includes(path),
                )
            ) {
                return config;
            }
            const accessToken = TokenStorage.getAccessToken();

            if (accessToken) {
                config.headers.Authorization = `Bearer ${accessToken}`;
            }

            return config;
        },
        (error) => Promise.reject(error),
    );
}
