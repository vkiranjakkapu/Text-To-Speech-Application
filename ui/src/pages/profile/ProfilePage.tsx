import { useParams } from "react-router-dom";
import SectionLayoutComponent from "../../components/SectionLayoutComponent";

export default function ProfilePage() {
    const { userId } = useParams<{ userId: string }>();

    return (
        <SectionLayoutComponent>
            <h1>Profile Page</h1>
        </SectionLayoutComponent>
    );
}
