import { Navigate, Outlet } from "react-router-dom";
import usePrincipal from "../../context/usePrincipal";
import { RoutePaths } from "../../routes/RoutePaths";

export default function AdminLayout() {
    const { isAdmin } = usePrincipal();

    if (!isAdmin) {
        return <Navigate to={RoutePaths.DASHBOARD} />;
    }

    return <Outlet />;
}
