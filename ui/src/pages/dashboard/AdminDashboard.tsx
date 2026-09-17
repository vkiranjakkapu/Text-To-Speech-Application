import { useCallback, useEffect, useState } from "react";
import {
    Area,
    AreaChart,
    CartesianGrid,
    Legend,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from "recharts";
import InputComponent from "../../components/formelements/InputComponent";
import SectionLayoutComponent from "../../components/SectionLayoutComponent";
import SpinnerComponent from "../../components/SpinnerComponent";
import usePrincipal from "../../context/usePrincipal";
import ReportsService, {
    type DataRecord,
    type RequestsReports,
    type UsageMetricsReports,
    type UserReports,
} from "../../services/ReportsService";

export default function AdminDashboard() {
    const { profile } = usePrincipal();

    const [monthlyUserReports, setMonthlyUserReports] =
        useState<UserReports | null>(null);
    const [usageMetrics, setUsageMetrics] =
        useState<UsageMetricsReports | null>(null);
    const [monthlyRequestReports, setMonthlyRequestReports] =
        useState<RequestsReports | null>(null);

    const [userReports, setUserReports] = useState<DataRecord[]>([]);

    const fetchDailyUserReports = useCallback(
        (month: string) => {
            if (month == "") {
                setUserReports(monthlyUserReports?.monthlyReports ?? []);
                return;
            }
            ReportsService.getMetricsReports<DataRecord[]>(month).then(
                (resp) => {
                    if (resp && !("errorMessage" in resp)) {
                        setUserReports(resp.data);
                    }
                },
            );
        },
        [monthlyUserReports?.monthlyReports],
    );

    const [synthesisReports, setSynthesisReports] = useState<DataRecord[]>([]);

    const fetchDailySynthesisReports = useCallback(
        (month: string) => {
            if (month == "") {
                setSynthesisReports(usageMetrics?.monthlyUtilization ?? []);
                return;
            }
            ReportsService.getMetricsReports<DataRecord[]>(month).then(
                (resp) => {
                    if (resp && !("errorMessage" in resp)) {
                        setSynthesisReports(resp.data);
                    }
                },
            );
        },
        [usageMetrics?.monthlyUtilization],
    );

    const [requestReports, setRequestReports] = useState<DataRecord[]>([]);

    const fetchDailyRequestsReports = useCallback(
        (month: string) => {
            if (month == "") {
                setRequestReports(monthlyRequestReports?.monthlyRequests ?? []);
                return;
            }
            ReportsService.getRequestsReports<DataRecord[]>(month).then(
                (resp) => {
                    if (resp && !("errorMessage" in resp)) {
                        setRequestReports(resp.data);
                    }
                },
            );
        },
        [monthlyRequestReports?.monthlyRequests],
    );

    useEffect(() => {
        ReportsService.getUserReports<UserReports>().then((resp) => {
            if (resp && !("errorMessage" in resp)) {
                setMonthlyUserReports(resp.data);
                setUserReports(resp.data.monthlyReports);
            }
        });
        ReportsService.getMetricsReports<UsageMetricsReports>().then((resp) => {
            if (resp && !("errorMessage" in resp)) {
                setUsageMetrics(resp.data);
                setSynthesisReports(resp.data.monthlyUtilization);
            }
        });
        ReportsService.getRequestsReports<RequestsReports>().then((resp) => {
            if (resp && !("errorMessage" in resp)) {
                setMonthlyRequestReports(resp.data);
                setRequestReports(resp.data.monthlyRequests);
            }
        });
    }, []);

    return (
        <SectionLayoutComponent
            title={`Welcome ${profile?.name}`}
            description={`This page shows the stats of the application.`}
        >
            <div className="grid grid-cols-1 md:grid-cols-4 gap-3 *:space-y-1 *:bg-slate-50 *:dark:bg-slate-800 *:border">
                <div className="p-2">
                    {monthlyUserReports == null ? (
                        <SpinnerComponent />
                    ) : (
                        <h1>{monthlyUserReports.totalUsers}</h1>
                    )}
                    <p className="capitalize">Total Users till date</p>
                    {/* <a href="#" className="text-sm text-style-secondary">Show Monthly Trend</a> */}
                </div>
                <div className="p-2">
                    {monthlyUserReports == null ? (
                        <SpinnerComponent />
                    ) : (
                        <h1>
                            {monthlyUserReports?.monthlyReports.at(-1)?.value ??
                                0}
                            <sub className="text-md">
                                {" "}
                                this month. (cavg:{" "}
                                {monthlyUserReports.cumMonthlyAvg})
                            </sub>
                        </h1>
                    )}
                    <p className="capitalize">New Users</p>
                    {/* <a href="#" className="text-sm text-style-secondary">Show Monthly Trend</a> */}
                </div>
                <div className="p-2">
                    <h1>
                        {usageMetrics?.monthlyUtilization.at(-1)?.value ?? 0}{" "}
                        <sub className="text-md">characters</sub>
                    </h1>
                    <p className="capitalize">Monthly Synthesis</p>
                    {/* <a href="#" className="text-sm text-style-secondary">Show Monthly Trend</a> */}
                </div>
                <div className="p-2">
                    <h1>
                        {monthlyRequestReports?.monthlyRequests.at(-1)?.value ??
                            0}{" "}
                        <sub className="text-md">this month</sub>
                    </h1>
                    <p className="capitalize">Total Requests</p>
                    {/* <a href="#" className="text-sm text-style-secondary">Show Monthly Trend</a> */}
                </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 *:h-80 *:p-4 *:flex *:flex-col">
                <div className="rounded border shadow-sm p-1 space-y-2 col-span-full">
                    {monthlyUserReports == null ? (
                        <SpinnerComponent />
                    ) : (
                        <>
                            <label htmlFor="userReportsMonth">
                                <span>Select Month </span>
                                <InputComponent
                                    className="w-fit"
                                    type="month"
                                    id="userReportsMonth"
                                    onChange={(e) => {
                                        fetchDailyUserReports(e.target.value);
                                    }}
                                />
                            </label>
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart data={userReports ?? []}>
                                    <defs>
                                        <linearGradient
                                            id="usersGradient"
                                            x1="0"
                                            y1="0"
                                            x2="0"
                                            y2="1"
                                        >
                                            <stop
                                                offset="0%"
                                                stopColor="#3181bd"
                                                stopOpacity={0.35}
                                            />
                                            <stop
                                                offset="100%"
                                                stopColor="#3181bd"
                                                stopOpacity={0}
                                            />
                                        </linearGradient>
                                    </defs>

                                    <CartesianGrid strokeDasharray="3 3" />

                                    <XAxis
                                        dataKey="key"
                                        // interval={0}
                                        angle={
                                            userReports.length > 20 ? -90 : 0
                                        }
                                        textAnchor={
                                            userReports.length > 20
                                                ? "end"
                                                : "middle"
                                        }
                                        height={
                                            userReports.length > 20 ? 90 : 30
                                        }
                                    />

                                    <YAxis />

                                    <Tooltip />

                                    <Legend position="bottom" />

                                    <Area
                                        type="monotone"
                                        dataKey="value"
                                        name={"User Registrations"}
                                        strokeWidth={2}
                                        fill="url(#usersGradient)"
                                    />
                                </AreaChart>
                            </ResponsiveContainer>
                        </>
                    )}
                </div>
                <div className="rounded border shadow-sm p-1 space-y-2">
                    {usageMetrics == null ? (
                        <SpinnerComponent />
                    ) : (
                        <>
                            <label htmlFor="synthesisReportsMonth">
                                <span>Select Month </span>
                                <InputComponent
                                    className="w-fit"
                                    type="month"
                                    id="synthesisReportsMonth"
                                    onChange={(e) => {
                                        fetchDailySynthesisReports(
                                            e.target.value,
                                        );
                                    }}
                                />
                            </label>
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart data={synthesisReports ?? []}>
                                    <defs>
                                        <linearGradient
                                            id="usersGradient"
                                            x1="0"
                                            y1="0"
                                            x2="0"
                                            y2="1"
                                        >
                                            <stop
                                                offset="0%"
                                                // stopColor="#3181bd"
                                                stopOpacity={0.35}
                                            />
                                            <stop
                                                offset="100%"
                                                // stopColor="#3181bd"
                                                stopOpacity={0}
                                            />
                                        </linearGradient>
                                    </defs>

                                    <CartesianGrid strokeDasharray="3 3" />

                                    <XAxis
                                        dataKey="key"
                                        // interval={0}
                                        angle={
                                            synthesisReports.length > 20
                                                ? -90
                                                : 0
                                        }
                                        textAnchor={
                                            synthesisReports.length > 20
                                                ? "end"
                                                : "middle"
                                        }
                                        height={
                                            synthesisReports.length > 20
                                                ? 90
                                                : 30
                                        }
                                    />

                                    <YAxis />

                                    <Tooltip />

                                    <Legend position="bottom" />

                                    <Area
                                        type="monotone"
                                        dataKey="value"
                                        name={"Total Synthesized"}
                                        strokeWidth={2}
                                        fill="url(#usersGradient)"
                                    />
                                </AreaChart>
                            </ResponsiveContainer>
                        </>
                    )}
                </div>
                <div className="rounded border shadow-sm p-1 space-y-2">
                    {monthlyRequestReports == null ? (
                        <SpinnerComponent />
                    ) : (
                        <>
                            <label htmlFor="requestsReportsMonth">
                                <span>Select Month </span>
                                <InputComponent
                                    className="w-fit"
                                    type="month"
                                    id="requestsReportsMonth"
                                    onChange={(e) => {
                                        fetchDailyRequestsReports(
                                            e.target.value,
                                        );
                                    }}
                                />
                            </label>
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart data={requestReports ?? []}>
                                    <defs>
                                        <linearGradient
                                            id="usersGradient"
                                            x1="0"
                                            y1="0"
                                            x2="0"
                                            y2="1"
                                        >
                                            <stop
                                                offset="0%"
                                                // stopColor="#3181bd"
                                                stopOpacity={0.35}
                                            />
                                            <stop
                                                offset="100%"
                                                // stopColor="#3181bd"
                                                stopOpacity={0}
                                            />
                                        </linearGradient>
                                    </defs>

                                    <CartesianGrid strokeDasharray="3 3" />

                                    <XAxis
                                        dataKey="key"
                                        // interval={0}
                                        angle={
                                            requestReports.length > 20 ? -90 : 0
                                        }
                                        textAnchor={
                                            requestReports.length > 20
                                                ? "end"
                                                : "middle"
                                        }
                                        height={
                                            requestReports.length > 20 ? 90 : 30
                                        }
                                    />

                                    <YAxis />

                                    <Tooltip />

                                    <Legend position="bottom" />

                                    <Area
                                        type="monotone"
                                        dataKey="value"
                                        name={"Total Requests"}
                                        strokeWidth={2}
                                        fill="url(#usersGradient)"
                                    />
                                </AreaChart>
                            </ResponsiveContainer>
                        </>
                    )}
                </div>
            </div>
        </SectionLayoutComponent>
    );
}
