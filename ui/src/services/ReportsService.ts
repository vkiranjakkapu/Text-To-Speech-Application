import { apiClient, type ApiResponse, type ErrorResponse } from "../api/api";

class ReportsService {
    async getUserReports<T>(
        month?: string,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "get",
            service: "reports",
            uri: month ? "/users/" + month : "/users",
        });
    }
    async getMetricsReports<T>(
        month?: string,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "get",
            service: "reports",
            uri: month ? "/synthesis/" + month : "/synthesis",
        });
    }
    async getRequestsReports<T>(
        month?: string,
    ): Promise<ApiResponse<T> | ErrorResponse> {
        return apiClient({
            type: "get",
            service: "reports",
            uri: month ? "/requests/" + month : "/requests",
        });
    }
}

export type UserReports = {
    totalUsers: number;
    cumMonthlyAvg: number;
    monthlyReports: DataRecord[];
};

export type UsageMetricsReports = {
    currentMonthUtilization: number;
    monthlyUtilization: DataRecord[];
};

export type RequestsReports = {
    currentRequests: number;
    monthlyRequests: DataRecord[];
};

export type DataRecord = {
    key: string;
    value: number;
};

export default new ReportsService();
