import { ChevronLeftIcon, ChevronRightIcon } from "@heroicons/react/24/outline";
import ActionButton from "../ActionButtonComponent";
import type { PaginationProps } from "./usePagination";

export interface PaginationButtonsProps<T> extends Omit<
    PaginationProps<T>,
    "currentItems"
> {
    className?: string;
}

export function PaginationButtons<T>({
    className,
    currentPage,
    totalPages,
    goToNextPage,
    goToPrevPage,
}: PaginationButtonsProps<T>) {
    if (totalPages <= 1) {
        return;
    }

    return (
        <div className={`w-fit ${className}`}>
            <div className="rounded-md overflow-hidden w-fit flex justify-self-end">
                <ActionButton
                    icon={ChevronLeftIcon}
                    className="btn-primary rounded-none p-2"
                    onClick={goToPrevPage}
                    disabled={currentPage == 1}
                />
                <ActionButton
                    icon={ChevronRightIcon}
                    className="btn-primary rounded-none p-2"
                    onClick={goToNextPage}
                    disabled={totalPages == currentPage}
                />
            </div>
            <span className="block lowercase text-sm">
                Showing page {currentPage} of {totalPages}
            </span>
        </div>
    );
}
