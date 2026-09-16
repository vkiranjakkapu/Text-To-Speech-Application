export const AppConfig = {
    API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    IDENTITY_AUTH_URL: "identity/api/v1/auth",
    IDENTITY_PROFILE_URL: "identity/api/v1/users",
    SPEECH_SERVICE_URL: "speech/api/v1",
    SPEECH_DOCUMENT_URL: "speech/api/v1/documents",
    SPEECH_AI_URL: "speech/api/v1/ai",
    SPEECH_HISTORY_URL: "speech/api/v1/history",
    REPORTS_SERVICE_URL: "reports/api/v1/speech",

    LOCAL_AUTH_KEY: "tts_auth",
    PUBLIC_ENDPOINTS: ["/auth/", "/users/register"],
};
