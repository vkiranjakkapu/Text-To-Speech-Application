import type { SelectHTMLAttributes } from "react";

export type SelectComponentProps = SelectHTMLAttributes<HTMLSelectElement> & {
    options: {
        text: string;
        value?: string;
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
        <select {...props} className={`${className}`}>
            <option value="">{emptyOption ?? "Select"}</option>
            {options.map((opt, idx) => {
                return (
                    <option
                        key={"Option" + idx + opt.text}
                        value={opt.value ?? opt.text}
                        {...opt}
                    >
                        {opt.text}
                    </option>
                );
            })}
        </select>
    );
}
