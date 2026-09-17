export type SpinnerComponentProps = {
    text?: string;
    size?: string;
    customize?: string;
    animate?: string;
};

export default function SpinnerComponent({
    text,
    size,
    customize,
    animate,
}: SpinnerComponentProps) {
    return (
        <div
            className={`container inline-flex items-center gap-1.5 justify-center ${customize}`}
        >
            <div
                className={`border-2 border-slate-300 border-t-primary animate-spin rounded-full ${size ?? "size-4"}`}
            ></div>
            {text && (
                <span className={`capitalize ${animate}`}>
                    {text ?? "Loading..."}
                </span>
            )}
        </div>
    );
}
