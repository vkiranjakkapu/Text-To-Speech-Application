import SectionLayoutComponent from "../../components/SectionLayoutComponent";
import usePrincipal from "../../context/usePrincipal";

export default function AdminDashboard() {
    const { profile } = usePrincipal();

    return (
        <SectionLayoutComponent
            title={`Welcome ${profile?.name}`}
            description={`This page shows the stats of the application.`}
        >
            <div className="grid grid-cols-1 md:grid-cols-4 gap-3 *:space-y-1 *:bg-slate-50 *:dark:bg-slate-800 *:border">
                <div className="p-2">
                    <h1>01</h1>
                    <p className="capitalize">Total Users till date</p>
                    {/* <a href="#" className="text-sm text-style-secondary">Show Monthly Trend</a> */}
                </div>
                <div className="p-2">
                    <h1>
                        02 <sub className="text-md">this month</sub>
                    </h1>
                    <p className="capitalize">New Users</p>
                    {/* <a href="#" className="text-sm text-style-secondary">Show Monthly Trend</a> */}
                </div>
                <div className="p-2">
                    <h1>
                        03 <sub className="text-md">characters</sub>
                    </h1>
                    <p className="capitalize">Monthly Synthesis</p>
                    {/* <a href="#" className="text-sm text-style-secondary">Show Monthly Trend</a> */}
                </div>
                <div className="p-2">
                    <h1>
                        04 <sub className="text-md">this month</sub>
                    </h1>
                    <p className="capitalize">Total Requests</p>
                    {/* <a href="#" className="text-sm text-style-secondary">Show Monthly Trend</a> */}
                </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">

            </div>
        </SectionLayoutComponent>
    );
}
