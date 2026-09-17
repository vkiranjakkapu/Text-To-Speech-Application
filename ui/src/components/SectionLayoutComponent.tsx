import type { HTMLAttributes, ReactNode } from "react";

export type SectionLayoutComponentProps = HTMLAttributes<HTMLElement> & {
    children: ReactNode;
    title?: string;
    description?: string;
};

export default function SectionLayoutComponent({
    children,
    title,
    description,
    ...props
}: SectionLayoutComponentProps) {
    return (
        <section {...props} className={`px-3 md:px-36 py-3 *:py-3 ${props.className}`}>
            {(title || description) && (
                <div className="border-b">
                    <h2>{title}</h2>
                    <p>{description}</p>
                </div>
            )}
            {children}
        </section>
    );
}
