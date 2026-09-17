import type { HTMLAttributes, ReactNode } from "react";
import type { InputComponentProps } from "./formelements/InputComponent";
import InputComponent from "./formelements/InputComponent";
import {
    PaginationButtons,
    type PaginationButtonsProps,
} from "./pagination/PaginationButtons";

export type SectionLayoutComponentProps<T> = HTMLAttributes<HTMLElement> & {
    children: ReactNode;
    title?: string;
    description?: string;
    search?: InputComponentProps;
    pagination?: PaginationButtonsProps<T>;
};

export default function SectionLayoutComponent<T>({
    children,
    title,
    description,
    search,
    pagination,
    ...props
}: SectionLayoutComponentProps<T>) {
    return (
        <section
            {...props}
            className={`px-3 md:px-36 py-3 *:py-3 ${props.className}`}
        >
            {(title || description) && (
                <div className="border-b">
                    <h2>{title}</h2>
                    <p>{description}</p>
                </div>
            )}
            {(search || pagination) && (
                <div className="border-b flex flex-wrap justify-between">
                    {search && (
                        <div className="flex-1">
                            <InputComponent {...search} />
                        </div>
                    )}
                    {pagination && (
                        <div className="flex-1 text-right items-end">
                            <PaginationButtons
                                {...pagination}
                                className={`ml-auto ${pagination.className}`}
                            />
                        </div>
                    )}
                </div>
            )}
            {children}
        </section>
    );
}
