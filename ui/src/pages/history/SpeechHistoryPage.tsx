import {
    ArrowDownCircleIcon,
    ClipboardDocumentListIcon,
    TrashIcon,
} from "@heroicons/react/24/outline";
import { useCallback, useEffect, useMemo, useState } from "react";
import usePagination from "../../components/pagination/usePagination";
import TableComponent, {
    type HeaderAlias,
} from "../../components/TableComponent";
import usePrincipal from "../../context/usePrincipal";
import HistoryService, {
    type SpeechHistory,
} from "../../services/HistoryService";
import SpeechService from "../../services/SpeechService";

type HistoryTableData = SpeechHistory & {
    userName: string;
    email: string;
};

export default function SpeechHistoryPage() {
    const { isAdmin } = usePrincipal();

    const [fullHistory, setFullHistory] = useState<HistoryTableData[] | null>(
        null,
    );
    const [loading, setLoading] = useState<boolean>(true);
    const [searchQuery, setSearchQuery] = useState<string | null>();

    const refreshHistory = useCallback(() => {
        HistoryService.getHistory<SpeechHistory[]>()
            .then((resp) => {
                if (resp && !("errorMessage" in resp)) {
                    setFullHistory(
                        resp.data.map(
                            (rec) =>
                                ({
                                    ...rec,
                                    ...(isAdmin
                                        ? {
                                              userName: `${rec.user.firstName} ${rec.user.lastName}`,
                                              email: rec.user.email,
                                          }
                                        : {}),
                                }) as HistoryTableData,
                        ),
                    );
                }
            })
            .finally(() => {
                setLoading(false);
            });
    }, [isAdmin]);

    useEffect(() => {
        refreshHistory();
    }, [refreshHistory]);

    const history = useMemo(() => {
        if (!searchQuery?.trim()) {
            return fullHistory;
        }

        const query = searchQuery.toLowerCase().trim();

        return fullHistory?.filter(
            (rec) =>
                rec.text.toLowerCase().includes(query) ||
                rec.voice.toLowerCase().includes(query) ||
                rec.userName.toLowerCase().includes(query) ||
                rec.email.toLowerCase().includes(query) ||
                rec.language.toLowerCase().includes(query),
        );
    }, [searchQuery, fullHistory]);

    const pagination = usePagination(history ?? [], 10);

    function deleteRecording(recording: HistoryTableData) {
        if (
            window.confirm(
                `Are you sure you want to delete the following recording? \n\n "${recording.text}"`,
            )
        ) {
            HistoryService.deleteRecording<boolean>(recording.id).then(
                (resp) => {
                    if (resp && !("errorMessage" in resp)) {
                        window.alert("Recording deleted successfully.");
                        refreshHistory();
                    } else {
                        window.alert(
                            "Error deleting recording! \n\n ERROR::" +
                                resp.errorMessage,
                        );
                    }
                },
            );
        }
    }

    function dowloadRecording(recording: HistoryTableData) {
        SpeechService.downloadRecording<Blob>(recording.id).then((resp) => {
            if (resp && !("errorMessage" in resp)) {
                const url = window.URL.createObjectURL(resp.data);

                const link = document.createElement("a");

                link.href = url;
                link.download = `speech-${recording.id}.mp3`;

                document.body.appendChild(link);

                link.click();

                link.remove();

                window.URL.revokeObjectURL(url);
            } else {
                window.alert(
                    "Error downloading recording!\n\nERROR::" +
                        resp.errorMessage,
                );
            }
        });
    }

    return (
        <TableComponent<HistoryTableData>
            title="Speech History"
            description="This page shows the history of text to speech conversions"
            body={pagination.currentItems}
            headers={
                [
                    ...(isAdmin
                        ? [
                              {
                                  key: "userName",
                                  alias: "User",
                                  customiseColumn: "max-w-[20ch] truncate",
                              },
                              {
                                  key: "email",
                                  alias: "User Email",
                                  customiseColumn: "max-w-[25ch] truncate",
                              },
                          ]
                        : []),
                    {
                        key: "text",
                        alias: "text",
                        customiseColumn: "max-w-[45ch] truncate",
                    },
                    { key: "voice" },
                    { key: "createdAt", alias: "Created" },
                ] as HeaderAlias<HistoryTableData>[]
            }
            actionEvents={[
                {
                    title: "Copy text",
                    clickEvent: {
                        icon: ClipboardDocumentListIcon,
                        className: "text-secondary dark:text-primary",
                        onClick(item) {
                            navigator.clipboard.writeText(item.text);
                            window.alert(
                                `Below text has been copied to your clipboard. \n\n ${item.text}`,
                            );
                        },
                    },
                },
                {
                    title: "Download",
                    clickEvent: {
                        icon: ArrowDownCircleIcon,
                        className: "text-emerald-500",
                        onClick(item) {
                            dowloadRecording(item);
                        },
                    },
                },
                {
                    title: "Delete",
                    clickEvent: {
                        icon: TrashIcon,
                        className: "text-rose-400",
                        onClick(item) {
                            deleteRecording(item);
                        },
                    },
                },
            ]}
            search={
                fullHistory && fullHistory.length > 0
                    ? {
                          type: "text",
                          id: "historySearchField",
                          placeholder: "Search...",
                          className: "w-full md:w-1/2 p-2",
                          onChange(e) {
                              setSearchQuery(e.target.value);
                          },
                      }
                    : undefined
            }
            loading={{
                showSpinner: loading,
                spinner: { text: "Loading History..." },
            }}
            pagination={
                fullHistory && fullHistory.length > 0 ? pagination : undefined
            }
        />
    );
}
