import {
    AdjustmentsHorizontalIcon,
    ArrowsPointingInIcon,
    ArrowUpTrayIcon,
    ArrowUturnLeftIcon,
    Bars3BottomLeftIcon,
    Bars3CenterLeftIcon,
    CogIcon,
    CursorArrowRaysIcon,
    InformationCircleIcon,
    SparklesIcon,
    SpeakerWaveIcon,
    XMarkIcon,
} from "@heroicons/react/24/outline";
import { useEffect, useState, type ChangeEvent } from "react";
import ActionButton from "../../components/ActionButtonComponent";
import SpinnerComponent from "../../components/SpinnerComponent";
import SelectComponent from "../../components/formelements/SelectComponent";
import TextareaComponent from "../../components/formelements/TextareaComponent";
import Notification from "../../components/notifications/Notification";
import { useNotifications } from "../../components/notifications/useNotifications";
import AiService from "../../services/AiService";
import DocumentService from "../../services/DocumentService";
import SpeechService, {
    EnhancementType,
    SynthesisType,
} from "../../services/SpeechService";

type VoiceOption = {
    id: string;
    name: string;
    language: string;
    languageName: string;
    gender: string;
    styles: string[];
};

type VoiceSelection = {
    language: string;
    voice: string;
    style: string;
    rate: string | undefined;
    pitch: string | undefined;
    volume: string | undefined;
    type: SynthesisType;
};

export default function Synthesize() {
    const [allowedTypes, setAllowedTypes] = useState<string[]>([]);
    const [voiceOptions, setVoiceOptions] = useState<VoiceOption[]>([]);
    const MAX_INPUT_TEXT_LENGTH = 300;

    const [voiceSelection, setVoiceSelection] = useState<VoiceSelection>(
        {} as VoiceSelection,
    );

    const [showMoreAdjustments, setShowMoreAdjustments] =
        useState<boolean>(false);
    const [voiceStyles, setVoiceStyles] = useState<string[]>([]);
    const [gender, setGender] = useState<string | null>(null);

    const { notifications, setNotifications } = useNotifications([
        "docUpload",
        "synthesis",
        "ai",
    ]);

    const [docUploadInProgress, setDocUploadInProgress] =
        useState<boolean>(false);
    const [optionFetchInProgress, setOptionFetchInProgress] =
        useState<boolean>(true);
    const [synthesisInProgress, setSynthesisInProgress] =
        useState<boolean>(false);
    const [enhancementInProgress, setEnhancementInProgress] =
        useState<boolean>(false);

    useEffect(() => {
        DocumentService.getAllowedDocTypes<string[]>()
            .then((resp) => {
                if (resp && !("errorMessage" in resp)) {
                    setAllowedTypes(resp.data);
                }
            })
            .finally(() => {
                setDocUploadInProgress(false);
            });
        SpeechService.getVoiceOptions<VoiceOption[]>()
            .then((resp) => {
                if (resp && !("errorMessage" in resp)) {
                    setVoiceOptions(resp.data);
                }
            })
            .finally(() => {
                setOptionFetchInProgress(false);
            });
    }, []);

    const [inputText, setInputText] = useState("");
    const [originalText, setOriginalText] = useState("");
    const [aiResult, setAiResult] = useState<string | null>(null);

    function updateVoiceSelection<K extends keyof VoiceSelection>(
        field: K,
        value: VoiceSelection[K],
    ) {
        setVoiceSelection((prev) => {
            if (value === "") {
                const { [field]: _, ...rest } = prev;
                (_ ?? "").concat("");
                return rest as VoiceSelection;
            }
            return {
                ...prev,
                [field]: value,
            };
        });
    }

    function removeFields(...fields: (keyof VoiceSelection)[]) {
        setVoiceSelection((prev) => {
            const updated = { ...prev };

            fields.forEach((field) => {
                delete updated[field];
            });

            return updated;
        });
    }

    function handleFileUpload(e: ChangeEvent<HTMLInputElement>) {
        if (!e.target.files || e.target.files.length === 0) return;

        const doc = e.target.files[0];

        if (!allowedTypes.includes("." + (doc.name.split(".").at(-1) ?? ""))) {
            setNotifications("docUpload", {
                type: "error",
                messages: ["Document type not allowed."],
            });
            return;
        }

        setDocUploadInProgress(true);
        setNotifications("docUpload", null);

        const payload = new FormData();
        payload.append("file", doc);

        DocumentService.extractDocContent<string>(payload)
            .then((resp) => {
                if (resp && !("errorMessage" in resp)) {
                    setInputText(resp.data);
                    setOriginalText(resp.data);
                    setAiResult(null);
                } else {
                    setNotifications("docUpload", {
                        type: "error",
                        messages: [resp.errorMessage],
                    });
                }
            })
            .finally(() => {
                setDocUploadInProgress(false);
            });
    }

    function handleAiRequirement(type: EnhancementType) {
        if (!inputText || inputText.length == 0) {
            return;
        }
        setEnhancementInProgress(true);
        setNotifications("ai", null);

        const payload = {
            text: inputText,
            type: type,
            ...(type === EnhancementType.REDUCE
                ? { length: MAX_INPUT_TEXT_LENGTH }
                : {}),
        };

        AiService.aiEnhancement<string>(payload)
            .then((resp) => {
                if (resp && !("errorMessage" in resp)) {
                    setAiResult(resp.data);
                    setInputText(resp.data);
                } else {
                    setNotifications("ai", {
                        type: "error",
                        messages: [resp.errorMessage],
                    });
                }
            })
            .finally(() => {
                setEnhancementInProgress(false);
            });
    }

    const [audioUrl, setAudioUrl] = useState<string | null>(null);

    function synthesizeTextToSpeech() {
        if (!validRequest()) {
            return;
        }

        setSynthesisInProgress(true);
        setNotifications("synthesis", null);

        const request = {
            ...voiceSelection,
            text: inputText,
        };

        SpeechService.synthesise<Blob>(request)
            .then((resp) => {
                if (resp && !("errorMessage" in resp)) {
                    const blob = resp.data as Blob;
                    const url = URL.createObjectURL(blob);

                    setAudioUrl((prev) => {
                        if (prev) {
                            URL.revokeObjectURL(prev);
                        }
                        return url;
                    });
                } else {
                    setNotifications("synthesis", {
                        type: "error",
                        messages: [resp.errorMessage],
                    });
                }
            })
            .finally(() => {
                setSynthesisInProgress(false);
            });
    }

    function validRequest() {
        setNotifications("synthesis", null);

        const errors = [];

        if (!inputText || inputText.length === 0) {
            errors.push("Text for synthesis can't be empty");
        } else if (inputText.length > MAX_INPUT_TEXT_LENGTH) {
            errors.push(
                `Input text can't exceed ${MAX_INPUT_TEXT_LENGTH} characters`,
            );
        }

        if (!voiceSelection.language) {
            errors.push("Language is required to synthesize");
        }

        if (!voiceSelection.voice) {
            errors.push("Voice is required to synthesize");
        }

        setNotifications("synthesis", {
            type: "error",
            messages: errors,
        });

        return errors.length === 0;
    }

    return (
        <>
            <div className="mt-3 grid grid-cols-1 md:grid-cols-2 md:grid-rows-[auto_1fr] gap-4 border rounded shadow p-4! *:space-y-3">
                {/* Contents */}
                <div className="order-2 md:order-1 row-span-2">
                    <h4 className="capitalize font-semibold flex items-center gap-1">
                        <Bars3CenterLeftIcon className="size-4" />
                        <span>Contents</span>
                    </h4>
                    <hr />
                    <div className="flex flex-wrap items-end justify-between gap-2">
                        <div className="space-y-2">
                            {/* <h4 className="capitalize font-semibold flex items-center gap-1">
                                <Bars3CenterLeftIcon className="size-4" />
                                <span>Contents</span>
                            </h4> */}
                            <p className="text-sm font-semibold capitalize">
                                Enter Your text below :
                            </p>
                        </div>

                        {/* Upload Section */}
                        <div className="flex-1 text-end">
                            <ActionButton
                                className="btn-primary px-3 rounded-full text-sm shadow-md ml-auto"
                                icon={ArrowUpTrayIcon}
                                spinner={{
                                    loading: docUploadInProgress,
                                    customize: "*:border-t-rose-300",
                                }}
                                text={
                                    docUploadInProgress
                                        ? `Uploading...`
                                        : "Upload"
                                }
                                onClick={() => {
                                    document
                                        .getElementById("uploadDoc")
                                        ?.click();
                                }}
                            />
                            <input
                                type="file"
                                name="uploadDoc"
                                id="uploadDoc"
                                className="hidden"
                                onChange={handleFileUpload}
                            />
                            <span className="text-xs">
                                Only{" "}
                                <span className="font-semibold">
                                    {allowedTypes.join(", ")}
                                </span>{" "}
                                are supported
                            </span>
                        </div>
                    </div>
                    {notifications["docUpload"] && (
                        <Notification
                            type={notifications["docUpload"]?.type}
                            messages={notifications["docUpload"]?.messages}
                        />
                    )}
                    {notifications["ai"] && (
                        <Notification
                            type={notifications["ai"]?.type}
                            messages={notifications["ai"]?.messages}
                        />
                    )}
                    <div className="flex flex-col border rounded-md p-1 gap-1.5 form-group *:w-full">
                        {/* AI Options */}
                        <div className="bg-primary/20 border flex flex-wrap justify-between gap-2 p-1 rounded">
                            <div className="align-middle inline-flex flex-wrap rounded-sm overflow-hidden divide-x divide-white dark:divide-slate-900">
                                <ActionButton
                                    className="p-0.5 px-1.5 rounded-none btn-primary"
                                    icon={SparklesIcon}
                                    text="enhance"
                                    onClick={() => {
                                        handleAiRequirement(
                                            EnhancementType.ENHANCE,
                                        );
                                    }}
                                    disabled={
                                        !inputText || inputText.length == 0
                                    }
                                />
                                <ActionButton
                                    className="p-0.5 px-1.5 rounded-none btn-primary"
                                    icon={Bars3BottomLeftIcon}
                                    text="sumarise"
                                    onClick={() => {
                                        handleAiRequirement(
                                            EnhancementType.SUMMARISE,
                                        );
                                    }}
                                    disabled={
                                        !inputText || inputText.length == 0
                                    }
                                />
                                <ActionButton
                                    className="p-0.5 px-1.5 rounded-none btn-primary"
                                    icon={ArrowsPointingInIcon}
                                    text="reduce"
                                    onClick={() => {
                                        handleAiRequirement(
                                            EnhancementType.REDUCE,
                                        );
                                    }}
                                    disabled={
                                        !inputText || inputText.length == 0
                                    }
                                />
                            </div>
                            <div className="align-middle inline-flex flex-wrap rounded-sm overflow-hidden divide-x divide-white dark:divide-slate-900">
                                <ActionButton
                                    className="p-0.5 px-1.5 rounded-none shadow-none btn-primary uppercase text-xs"
                                    icon={ArrowUturnLeftIcon}
                                    text="undo"
                                    title="Restore Original Text"
                                    disabled={!aiResult || !originalText}
                                    onClick={() => {
                                        setInputText(originalText);
                                    }}
                                />
                                <ActionButton
                                    className="p-0.5 px-1.5 rounded-none shadow-none btn-primary uppercase text-xs"
                                    icon={CursorArrowRaysIcon}
                                    text="use ai result"
                                    title="Apply AI Result"
                                    disabled={!aiResult}
                                    onClick={() => {
                                        setInputText(aiResult!);
                                    }}
                                />
                            </div>
                        </div>

                        {/* Text Input */}
                        <TextareaComponent
                            name="input"
                            id="input"
                            rows={8}
                            placeholder="Text to convert..."
                            className={`border-0 rounded-none ${enhancementInProgress ? `animate-pulse` : ``}`}
                            onChange={(e) => {
                                setInputText(e.target.value);
                                setOriginalText(e.target.value);
                                setAiResult(null);
                            }}
                            value={inputText ?? ""}
                        />
                        <div className="flex flex-wrap justify-between items-center">
                            <div>
                                {enhancementInProgress && (
                                    <SpinnerComponent
                                        text="Working on text..."
                                        customize="text-xs w-fit animate-pulse"
                                    />
                                )}
                            </div>
                            <p className="text-end">
                                <span
                                    className={
                                        (inputText?.length ?? 0) >
                                        MAX_INPUT_TEXT_LENGTH
                                            ? `text-rose-400`
                                            : ``
                                    }
                                >
                                    {inputText?.length ?? 0}
                                </span>
                                /{MAX_INPUT_TEXT_LENGTH}
                            </p>
                        </div>
                    </div>
                </div>

                {/* Speech */}
                <div className="order-3 md:order-2 space-y-2!">
                    <div className="flex justify-between">
                        <h4 className="capitalize font-semibold flex items-center gap-1">
                            <SpeakerWaveIcon className="size-4" />
                            <span>Speech</span>
                        </h4>
                        <ActionButton
                            icon={SpeakerWaveIcon}
                            spinner={{
                                loading: synthesisInProgress,
                                customize: `*:border-t-rose-300`,
                            }}
                            className={`btn-primary p-1.5 gap-1.5 px-2 rounded-full shadow-md text-sm capitalize ${synthesisInProgress ? `animate-pulse` : ``}`}
                            text={
                                synthesisInProgress
                                    ? `Generating Speech...`
                                    : "Generate"
                            }
                            onClick={() => {
                                synthesizeTextToSpeech();
                            }}
                        />
                    </div>
                    <hr />
                    {notifications["synthesis"] && (
                        <Notification
                            type={notifications["synthesis"]?.type}
                            messages={notifications["synthesis"]?.messages}
                        />
                    )}
                    {audioUrl ? (
                        <div>
                            <audio controls className="w-full" src={audioUrl}>
                                Your browser does not support the audio element.
                            </audio>
                        </div>
                    ) : (
                        <div className="opacity-40 pointer-events-none">
                            <audio controls className="w-full">
                                Your browser does not support the audio element.
                            </audio>
                        </div>
                    )}
                </div>

                {/* Adjustments */}
                <div className="order-1 md:order-3 md:h-full">
                    <div className="flex justify-between">
                        <div className="">
                            <h4 className="capitalize font-semibold flex items-center gap-1">
                                <CogIcon className="size-4" />
                                <span>Adjustments</span>
                            </h4>
                            {optionFetchInProgress && (
                                <SpinnerComponent
                                    text="Fetching options..."
                                    customize="animate-pulse text-xs"
                                />
                            )}
                        </div>
                        <ActionButton
                            icon={
                                showMoreAdjustments
                                    ? XMarkIcon
                                    : AdjustmentsHorizontalIcon
                            }
                            className="btn-secondary p-1 text-xs capitalize"
                            text="More"
                            onClick={() => {
                                setShowMoreAdjustments(!showMoreAdjustments);
                            }}
                        />
                    </div>
                    <hr />
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                        <SelectComponent
                            emptyOption={"Language"}
                            id="Language"
                            options={[
                                ...new Map(
                                    voiceOptions.map((voice) => [
                                        voice.language,
                                        {
                                            text: voice.languageName,
                                            value: voice.language,
                                        },
                                    ]),
                                ).values(),
                            ]}
                            onChange={(e) => {
                                updateVoiceSelection(
                                    "language",
                                    e.target.value,
                                );

                                setGender(null);
                                setVoiceStyles([]);

                                removeFields(
                                    "voice",
                                    "style",
                                    "rate",
                                    "pitch",
                                    "volume",
                                );
                            }}
                            disabled={optionFetchInProgress}
                            required
                        />
                        <SelectComponent
                            emptyOption="Gender"
                            id="Gender"
                            options={[
                                {
                                    value: "Female",
                                },
                                {
                                    value: "Male",
                                },
                            ]}
                            onChange={(e) => {
                                setGender(e.target.value);

                                setVoiceStyles([]);

                                removeFields(
                                    "voice",
                                    "style",
                                    "rate",
                                    "pitch",
                                    "volume",
                                );
                            }}
                            value={gender ?? ""}
                            disabled={!voiceSelection.language}
                            required
                        />
                    </div>
                    <SelectComponent
                        key={voiceSelection.language}
                        emptyOption="Voice"
                        id="Voice"
                        options={voiceOptions
                            .filter(
                                (voc) =>
                                    voc.language == voiceSelection.language &&
                                    voc.gender == gender,
                            )
                            .map((voice) => ({
                                text: voice.name,
                                value: voice.id,
                            }))}
                        onChange={(e) => {
                            updateVoiceSelection("voice", e.target.value);

                            if (e.target.value === "") {
                                setVoiceStyles([]);
                                removeFields(
                                    "style",
                                    "rate",
                                    "pitch",
                                    "volume",
                                );
                                return;
                            }

                            setVoiceStyles(
                                voiceOptions.find(
                                    (voice) => voice.id === e.target.value,
                                )?.styles ?? [],
                            );

                            removeFields("style", "rate", "pitch", "volume");
                        }}
                        disabled={!gender}
                        required
                    />
                    <SelectComponent
                        emptyOption={
                            voiceStyles.length == 0 &&
                            voiceSelection.voice != null
                                ? "No styles available"
                                : "Style"
                        }
                        id="Style"
                        options={voiceStyles.map((style) => ({
                            value: style,
                        }))}
                        onChange={(e) => {
                            updateVoiceSelection("style", e.target.value);

                            removeFields("rate", "pitch", "volume");
                        }}
                        disabled={!gender || voiceStyles.length == 0}
                    />
                    {showMoreAdjustments && (
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                            <div className="">
                                <SelectComponent
                                    emptyOption="Rate"
                                    id="Rate"
                                    options={[
                                        {
                                            text: "x-slow → -50%",
                                            value: "-50%",
                                        },
                                        {
                                            text: "slow → -46%",
                                            value: "-46%",
                                        },
                                        {
                                            text: "medium → 0%",
                                            value: "0%",
                                        },
                                        {
                                            text: "fast → +55%",
                                            value: "+55%",
                                        },
                                        {
                                            text: "x-fast → +100%",
                                            value: "+100%",
                                        },
                                    ]}
                                    onChange={(e) => {
                                        updateVoiceSelection(
                                            "rate",
                                            e.target.value,
                                        );
                                    }}
                                />
                                <p className="text-sm font-semibold flex items-center gap-1">
                                    <InformationCircleIcon className="size-4" />
                                    <span>Rate Controls speaking speed.</span>
                                </p>
                            </div>
                            <div className="">
                                <SelectComponent
                                    emptyOption="Volume"
                                    id="Volume"
                                    options={[
                                        {
                                            text: "silent → 0",
                                            value: "0",
                                        },
                                        {
                                            text: "x-soft → 0.2",
                                            value: "0.2",
                                        },
                                        {
                                            text: "soft → 0.4",
                                            value: "0.4",
                                        },
                                        {
                                            text: "medium → 0.6",
                                            value: "0.6",
                                        },
                                        {
                                            text: "loud → 0.8",
                                            value: "0.8",
                                        },
                                        {
                                            text: "x-loud → 1.0",
                                            value: "1.0",
                                        },
                                    ]}
                                    onChange={(e) => {
                                        updateVoiceSelection(
                                            "volume",
                                            e.target.value,
                                        );
                                    }}
                                />
                                <p className="text-sm font-semibold flex items-center gap-1">
                                    <InformationCircleIcon className="size-4" />
                                    <span>Volume Controls Loudness.</span>
                                </p>
                            </div>
                            <div className="col-span-full">
                                <SelectComponent
                                    emptyOption="Pitch"
                                    id="Pitch"
                                    options={[
                                        {
                                            text: "x-low → -45%",
                                            value: "-45%",
                                        },
                                        {
                                            text: "low → -20%",
                                            value: "-20%",
                                        },
                                        {
                                            text: "medium → default",
                                            value: "default",
                                        },
                                        {
                                            text: "high → +20%",
                                            value: "+20%",
                                        },
                                        {
                                            text: "x-high → +45%",
                                            value: "+45%",
                                        },
                                    ]}
                                    onChange={(e) => {
                                        updateVoiceSelection(
                                            "pitch",
                                            e.target.value,
                                        );
                                    }}
                                />
                                <p className="text-sm font-semibold flex items-center gap-1">
                                    <InformationCircleIcon className="size-4" />
                                    <span>
                                        Pitch Controls how high or low the voice
                                        sounds.
                                    </span>
                                </p>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </>
    );
}
