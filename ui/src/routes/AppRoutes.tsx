import { Route, Routes } from "react-router-dom";
import Dashboard from "../pages/dashboard/Dashboard";
import SpeechHistoryPage from "../pages/history/SpeechHistoryPage";
import LandingPage from "../pages/LandingPage";
import HomeLayout from "../pages/layouts/HomeLayout";
import ProtectedLayout from "../pages/layouts/ProtectedLayout";
import ProfilePage from "../pages/profile/ProfilePage";
import UsersPage from "../pages/users/UsersPage";
import { RoutePaths } from "./RoutePaths";
import AdminLayout from "../pages/layouts/AdminLayout";

export default function AppRoutes() {
    return (
        <Routes>
            <Route element={<HomeLayout />}>
                <Route element={<LandingPage />} path={RoutePaths.HOME} />
            </Route>
            <Route element={<ProtectedLayout />}>
                <Route path={RoutePaths.DASHBOARD} element={<Dashboard />} />
                <Route
                    path={RoutePaths.HISTORY}
                    element={<SpeechHistoryPage />}
                />
                <Route path={RoutePaths.PROFILE} element={<ProfilePage />} />
                <Route element={<AdminLayout />}>
                    <Route path={RoutePaths.USERS} element={<UsersPage />} />
                    <Route
                        path={RoutePaths.USER_DETAILS}
                        element={<ProfilePage />}
                    />
                </Route>
            </Route>
        </Routes>
    );
}
