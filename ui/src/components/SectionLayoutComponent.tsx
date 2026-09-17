import type { HTMLAttributes, ReactNode } from "react";
import type { InputComponentProps } from "./formelements/InputComponent";
import InputComponent from "./formelements/InputComponent";
import {
    PaginationButtons,
    type PaginationButtonsProps,
} from "./pagination/PaginationButtons";
import type { ActionButtonProps } from "./ActionButtonComponent";
import ActionButton from "./ActionButtonComponent";

export type SectionLayoutComponentProps<T> = HTMLAttributes<HTMLElement> & {
    children: ReactNode;
    title?: string;
    description?: string;
    actionEvents?: ActionButtonProps[];
    search?: InputComponentProps;
    pagination?: PaginationButtonsProps<T>;
};

export default function SectionLayoutComponent<T>({
    children,
    title,
    description,
    actionEvents,
    search,
    pagination,
    ...props
}: SectionLayoutComponentProps<T>) {
    return (
        <section
            {...props}
            className={`px-3 md:px-36 py-3 *:py-3 ${props.className}`}
        >
            {(title || description || actionEvents) && (
                <div className="flex justify-between border-b">
                    <div className="">
                        <h2>{title}</h2>
                        <p>{description}</p>
                    </div>
                    <div className="flex items-center gap-1">
                        {actionEvents?.map((act, idx) => (
                            <ActionButton
                                key={"section-action-" + idx}
                                {...act}
                                className={`btn-primary ${act.className}`}
                            />
                        ))}
                    </div>
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
