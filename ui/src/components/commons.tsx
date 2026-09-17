import type {
    ForwardRefExoticComponent,
    PropsWithoutRef,
    SVGProps,
} from "react";
import { DateFormatter } from "../utils/DateFormatter";

export function renderCellValue(value: unknown) {
    if (DateFormatter.isTimestampFormat(value)) {
        return DateFormatter.toRelativeTime(value);
    }

    return String(value);
}

export type IconProps = ForwardRefExoticComponent<
    PropsWithoutRef<SVGProps<SVGSVGElement>>
>;
