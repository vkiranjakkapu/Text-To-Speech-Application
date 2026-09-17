import SectionLayoutComponent from "../../components/SectionLayoutComponent";
import usePrincipal from "../../context/usePrincipal";
import Synthesize from "../speech/Synthesize";

export default function UserDashboard() {
    const { profile } = usePrincipal();

    return (
        <SectionLayoutComponent
            title={`Welcome ${profile?.name}`}
            description={`You can convert text from your choice to speech in this page.`}
        >
            <Synthesize />
        </SectionLayoutComponent>
    );
}
