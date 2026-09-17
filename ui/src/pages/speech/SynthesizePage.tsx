import SectionLayoutComponent from "../../components/SectionLayoutComponent";
import Synthesize from "./Synthesize";

export default function SynthesizePage() {
    return (
        <SectionLayoutComponent
            title="Synthesize Text"
            description="You can convert text from given choice to speech here."
        >
            <Synthesize />
        </SectionLayoutComponent>
    );
}
