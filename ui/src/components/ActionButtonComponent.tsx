import type { ButtonHTMLAttributes } from "react";
import type { IconProps } from "./commons";
import SpinnerComponent from "./SpinnerComponent";

export type ActionButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
    icon?: IconProps;
    customiseIcon?: string;
    customiseText?: string;
    text?: string;
    loading?: boolean;
};

export default function ActionButton({
    icon: Icon,
    customiseIcon,
    customiseText,
    text,
    loading = false,
    ...props
}: ActionButtonProps) {
    return (
        <button
            {...props}
            className={`flex items-center ${props.disabled && `pointer-events-none opacity-70`} ${props.className}`}
        >
            {loading ? (
                <SpinnerComponent />
            ) : (
                Icon && <Icon className={`size-4 ${customiseIcon}`} />
            )}
            {text && <span className={customiseText}>{text}</span>}
        </button>
    );
}
