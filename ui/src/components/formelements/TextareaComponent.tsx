import type { TextareaHTMLAttributes } from "react";

type TextareaComponentProps = TextareaHTMLAttributes<HTMLTextAreaElement> & {
    className?: string;
};

export default function TextareaComponent({
    className,
    ...props
}: TextareaComponentProps) {
    return (
        <textarea
            rows={4}
            {...props}
            className={`block rounded-md ${className} ${props.disabled && `opacity-60`}`}
        ></textarea>
    );
}
