import {
    ArrowRightEndOnRectangleIcon,
    Bars3Icon,
    ClockIcon,
    HomeIcon,
    KeyIcon,
    LockClosedIcon,
    MoonIcon,
    SpeakerWaveIcon,
    SunIcon,
    UserGroupIcon,
    UserPlusIcon,
} from "@heroicons/react/24/outline";

import { useEffect, useState, type SubmitEvent } from "react";
import type { ApiResponse } from "../api/api";
import Avatar from "../assets/undraw_reading-notes_dg9z.svg";
import usePrincipal, {
    AuthStatus,
    RoleType,
    type UserProfile,
} from "../context/usePrincipal";
import { RoutePaths } from "../routes/RoutePaths";
import { type LoginRequest } from "../services/AuthService";
import UserService from "../services/UserService";
import ActionButton from "./ActionButtonComponent";
import type { IconProps } from "./commons";
import InputComponent from "./formelements/InputComponent";
import LoadingPortalComponent from "./LoadingPortalComponent";
import ModalComponent from "./ModalComponent";
import Notification from "./notifications/Notification";
import { useNotifications } from "./notifications/useNotifications";
import { useNavigate } from "react-router-dom";

export default function NavbarComponent() {
    const { profile, isLoggedIn, status, login, logout } = usePrincipal();
    const navigate = useNavigate();

    const [showMenu, toggleMenu] = useState<boolean>(false);

    const [loginModal, setLoginModal] = useState<"login" | "register" | null>(
        null,
    );
    const {
        notifications: allNotifications,
        setNotifications,
        resetNotifications,
    } = useNotifications(["login", "register"]);
    const notifications = loginModal
        ? (allNotifications[loginModal] ?? null)
        : null;
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const [theme, setTheme] = useState<"dark" | "light">(() => {
        const theme = localStorage.getItem("theme");
        if (theme === "dark") {
            return "dark";
        }
        return "light";
    });

    useEffect(() => {
        localStorage.setItem("theme", theme);
        document.documentElement.classList.value = "";
        document.documentElement.classList.add(theme);
    }, [theme]);

    const NavItems: {
        label: string;
        uri: string;
        icon: IconProps;
        roles: RoleType[];
    }[] = [
        {
            label: "Dashboard",
            uri: RoutePaths.DASHBOARD,
            icon: HomeIcon,
            roles: [RoleType.ADMIN, RoleType.USER],
        },
        {
            label: "Synthesize",
            uri: RoutePaths.SYNTHESIZE,
            icon: SpeakerWaveIcon,
            roles: [RoleType.ADMIN],
        },
        {
            label: "History",
            uri: RoutePaths.HISTORY,
            icon: ClockIcon,
            roles: [RoleType.ADMIN, RoleType.USER],
        },
        {
            label: "Users",
            uri: RoutePaths.USERS,
            icon: UserGroupIcon,
            roles: [RoleType.ADMIN],
        },
    ];

    function authenticateUser(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();
        setNotifications("login", null);
        setIsLoading(true);
        const formData = new FormData(e.currentTarget);
        const payload = {
            email: formData.get("email"),
            password: formData.get("password"),
        } as LoginRequest;

        login(payload)
            .then((resp) => {
                if (resp && "errorMessage" in resp) {
                    setNotifications("login", {
                        type: "error",
                        messages:
                            resp.validationErrors.length === 0
                                ? [resp.errorMessage]
                                : [
                                      ...resp.validationErrors.map(
                                          (vr) => vr.field + " " + vr.message,
                                      ),
                                  ],
                    });
                } else {
                    setLoginModal(null);
                }
            })
            .finally(() => {
                setIsLoading(false);
            });
    }

    function registerUser(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();
        setNotifications("register", null);
        setIsLoading(true);
        const formData = new FormData(e.currentTarget);
        UserService.register<ApiResponse<UserProfile>>(formData)
            .then((resp) => {
                if (resp && !("errorMessage" in resp)) {
                    setLoginModal(null);
                    login({
                        email: formData.get("email"),
                        password: formData.get("password"),
                    } as LoginRequest);
                } else {
                    setNotifications("register", {
                        type: "error",
                        messages:
                            resp.validationErrors.length === 0
                                ? [resp.errorMessage]
                                : [
                                      ...resp.validationErrors.map(
                                          (vr) => vr.field + " " + vr.message,
                                      ),
                                  ],
                    });
                }
            })
            .finally(() => {
                setIsLoading(false);
            });
    }

    return (
        <>
            <nav className="sticky py-3 md:p-6 border-b">
                <div className="flex flex-wrap px-3 md:px-30 md:py-4 justify-between items-center gap-3">
                    <div className="flex items-center gap-2">
                        <img
                            src="./favicon.png"
                            alt="ICON"
                            className="h-18 drop-shadow-sm"
                        />
                        <div className="">
                            <h2 className="text-primary font-bold">T T S</h2>
                            <p className="capitalize">Text To Speech</p>
                        </div>
                    </div>
                    <div className="md:hidden flex gap-2">
                        <ActionButton
                            icon={theme === "light" ? MoonIcon : SunIcon}
                            onClick={() => {
                                setTheme((prev) =>
                                    prev === "light" ? "dark" : "light",
                                );
                            }}
                            className="btn-primary rounded-full p-2"
                        />
                        <ActionButton
                            icon={Bars3Icon}
                            className="btn-primary rounded-full p-2"
                            onClick={() => toggleMenu(!showMenu)}
                        />
                    </div>
                    <div
                        className={`w-full md:w-fit flex-wrap gap-3 items-center ${showMenu ? `block` : `hidden md:flex`}`}
                    >
                        {isLoggedIn ? (
                            <>
                                <ul className="p-1 flex flex-col md:flex-row gap-2 md:bg-slate-50 dark:bg-slate-800 w-full md:w-fit md:rounded-full md:shadow-sm">
                                    {NavItems.map((item, idx) => {
                                        if (
                                            profile &&
                                            !item.roles.includes(
                                                profile.roles[0],
                                            )
                                        ) {
                                            return;
                                        }

                                        const isActive =
                                            location.pathname.startsWith(
                                                item.uri,
                                            ) || location.pathname === item.uri;

                                        return (
                                            <li
                                                key={idx}
                                                className={`p-2 px-2.5 flex gap-1 items-center cursor-pointer transition-colors duration-100 rounded-full 
                                                    ${isActive ? `bg-primary text-white ` : `hover:bg-slate-100 dark:hover:bg-primary-light/10`}
                                                    `}
                                                onClick={() =>
                                                    navigate(item.uri)
                                                }
                                            >
                                                <item.icon className="size-4" />
                                                <span className="capitalize">
                                                    {item.label}
                                                </span>
                                            </li>
                                        );
                                    })}
                                </ul>
                                <div
                                    className={`mx-auto mt-2.5 md:m-0 rounded-full p-1 shadow-sm flex items-center justify-around ${location.pathname === RoutePaths.PROFILE ? `bg-primary text-white` : `bg-slate-50 dark:bg-slate-800 `}`}
                                    onClick={() => navigate(RoutePaths.PROFILE)}
                                >
                                    <img
                                        src={Avatar}
                                        alt="avatar"
                                        className="size-10 rounded-full border bg-white border-slate-100"
                                    />
                                    <ActionButton
                                        text={profile?.name}
                                        className="shadow-none"
                                        title="Edit Profile"
                                    />
                                    <ActionButton
                                        icon={ArrowRightEndOnRectangleIcon}
                                        className="shadow-none text-rose-400 hover:bg-slate-200/50 dark:hover:bg-slate-900 p-2 rounded-full"
                                        customiseIcon="size-5"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            logout();
                                        }}
                                    />
                                </div>
                            </>
                        ) : (
                            <ul className="p-1 flex flex-col md:flex-row gap-2 md:bg-slate-50 dark:bg-slate-800 w-full md:w-fit md:rounded-full md:shadow-sm">
                                <li
                                    className="p-2 px-2.5 flex gap-1 items-center hover:bg-slate-100 dark:hover:bg-primary-light/10 cursor-pointer transition-colors duration-100 rounded-full"
                                    onClick={() => {
                                        resetNotifications("login");
                                        setLoginModal("login");
                                    }}
                                >
                                    <UserPlusIcon className="size-4" />
                                    <span>Login</span>
                                </li>
                                <li
                                    className="p-2 px-2.5 flex gap-1 items-center bg-primary text-white cursor-pointer transition-colors duration-100 rounded-full"
                                    onClick={() => {
                                        resetNotifications("register");
                                        setLoginModal("register");
                                    }}
                                >
                                    <LockClosedIcon className="size-4" />
                                    <span>Register</span>
                                </li>
                            </ul>
                        )}
                        <div className="hidden md:block p-1 shadow-sm rounded-full">
                            <ActionButton
                                icon={theme === "light" ? MoonIcon : SunIcon}
                                onClick={() => {
                                    setTheme((prev) =>
                                        prev === "light" ? "dark" : "light",
                                    );
                                }}
                                className="btn-primary rounded-full p-2.5"
                            />
                        </div>
                    </div>
                </div>
            </nav>
            <ModalComponent
                title={loginModal + ""}
                isOpen={loginModal != null}
                onClose={() => setLoginModal(null)}
                icon={LockClosedIcon}
            >
                <form
                    onSubmit={(e) => {
                        if (loginModal === "login") authenticateUser(e);
                        else registerUser(e);
                    }}
                    className="space-y-3 p-1"
                >
                    {notifications != null && (
                        <Notification
                            type={notifications.type}
                            messages={notifications.messages}
                        />
                    )}
                    {loginModal === "register" ? (
                        <>
                            <InputComponent
                                type="email"
                                placeholder="Email"
                                name="email"
                                id="email"
                                required
                            />
                            <InputComponent
                                placeholder="First Name"
                                name="firstName"
                                id="firstName"
                                required
                            />
                            <InputComponent
                                placeholder="Last Name"
                                name="lastName"
                                id="lastName"
                                required
                            />
                            <hr className="border-b" />
                            <InputComponent
                                type="password"
                                placeholder="Create Password"
                                name="password"
                                id="password"
                                required
                            />
                            <InputComponent
                                type="password"
                                placeholder="Confirm Password"
                                name="confirmPassword"
                                id="confirmPassword"
                                required
                            />
                        </>
                    ) : (
                        <>
                            <InputComponent
                                type="email"
                                placeholder="Email"
                                name="email"
                                id="email"
                                required
                            />
                            <InputComponent
                                type="password"
                                placeholder="Password"
                                name="password"
                                id="password"
                                required
                            />
                        </>
                    )}
                    <ActionButton
                        type="submit"
                        className="capitalize btn-primary gap-2 ml-auto"
                        text={loginModal + ""}
                        loading={isLoading}
                        icon={loginModal === "login" ? KeyIcon : UserPlusIcon}
                        disabled={isLoading}
                    />
                </form>
            </ModalComponent>
            <LoadingPortalComponent
                isLoading={status === AuthStatus.INITIALIZING}
                message="Preparing your session."
            />
        </>
    );
}
