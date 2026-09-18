import type { ButtonHTMLAttributes } from "react";
import type { IconProps } from "./commons";
import SpinnerComponent, {
    type SpinnerComponentProps,
} from "./SpinnerComponent";

export type ActionButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
    icon?: IconProps;
    customiseIcon?: string;
    customiseText?: string;
    text?: string;
    spinner?: SpinnerComponentProps & {
        loading?: boolean;
    };
};

export default function ActionButton({
    icon: Icon,
    customiseIcon,
    customiseText,
    text,
    spinner,
    ...props
}: ActionButtonProps) {
    return (
        <button
            {...props}
            className={`flex items-center gap-1 ${props.disabled && `pointer-events-none opacity-70`} ${props.className}`}
        >
            {spinner && spinner.loading ? (
                <SpinnerComponent {...spinner} customize={`w-fit ${spinner.customize}`} />
            ) : (
                Icon && <Icon className={`size-4 ${customiseIcon}`} />
            )}
            {text && <span className={customiseText}>{text}</span>}
        </button>
    );
}
