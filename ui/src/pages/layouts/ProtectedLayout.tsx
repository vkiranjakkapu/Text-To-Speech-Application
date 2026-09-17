import { Navigate, Outlet } from "react-router-dom";
import usePrincipal, { AuthStatus } from "../../context/usePrincipal";
import { RoutePaths } from "../../routes/RoutePaths";

export default function ProtectedLayout() {
    const { status, isLoggedIn } = usePrincipal();

    if (status === AuthStatus.INITIALIZING) {
        return;
    }

    if (!isLoggedIn) {
        return <Navigate to={RoutePaths.HOME} />;
    }

    return <Outlet />;
}
