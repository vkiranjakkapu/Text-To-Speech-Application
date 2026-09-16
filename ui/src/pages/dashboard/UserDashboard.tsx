import SectionLayoutComponent from "../../components/SectionLayoutComponent";
import usePrincipal from "../../context/usePrincipal";

export default function UserDashboard() {
    const { profile } = usePrincipal();

    return (
        <SectionLayoutComponent
            title={`Welcome ${profile?.name}`}
            description={`You can convert text from your choice to speech in this page.`}
        >
            <h1>User Dashboard</h1>
        </SectionLayoutComponent>
    );
}
