import usePrincipal from "../../context/usePrincipal";
import AdminDashboard from "./AdminDashboard";
import UserDashboard from "./UserDashboard";

export default function Dashboard() {
    const { isAdmin } = usePrincipal();

    return isAdmin ? <AdminDashboard /> : <UserDashboard />;
}
