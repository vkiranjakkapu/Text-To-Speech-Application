import { Outlet, useNavigate } from "react-router-dom";
import usePrincipal from "../../context/usePrincipal";
import { RoutePaths } from "../../routes/RoutePaths";

export default function HomeLayout() {
    const { isLoggedIn } = usePrincipal();
    const navigate = useNavigate();

    if (isLoggedIn) {
        navigate(RoutePaths.DASHBOARD);
    }

    return <Outlet />;
}
