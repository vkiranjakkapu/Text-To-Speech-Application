import type { InputHTMLAttributes } from "react";

type InputComponentProps = InputHTMLAttributes<HTMLInputElement> & {
    className?: string;
};

export default function InputComponent({
    className,
    ...props
}: InputComponentProps) {
    return <input type="text" {...props} className={`${className}`} />;
}
