import type { SelectHTMLAttributes } from "react";

export type SelectComponentProps = SelectHTMLAttributes<HTMLSelectElement> & {
    options: {
        value: string;
        text?: string;
    }[];
    emptyOption?: string;
    className?: string;
};

export default function SelectComponent({
    options,
    emptyOption,
    className,
    ...props
}: SelectComponentProps) {
    return (
        <select {...props} className={`${className} ${props.disabled && `opacity-60`}`}>
            <option value="">{emptyOption ?? "Select"}</option>
            {options.map((opt, idx) => {
                return (
                    <option key={"Option" + idx + opt.value} {...opt}>
                        {opt.text ?? opt.value}
                    </option>
                );
            })}
        </select>
    );
}
