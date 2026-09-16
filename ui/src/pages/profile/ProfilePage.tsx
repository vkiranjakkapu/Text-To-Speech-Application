import { useParams } from "react-router-dom";

export default function ProfilePage() {
    const { userId } = useParams<{ userId: string }>();

    return <h1>Profile Page</h1>;
}
