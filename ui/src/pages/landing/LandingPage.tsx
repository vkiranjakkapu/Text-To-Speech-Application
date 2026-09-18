import {
    ArrowRightIcon,
    KeyIcon,
    LockClosedIcon,
    MoonIcon,
    SunIcon,
    UserPlusIcon,
} from "@heroicons/react/24/outline";
import { useEffect, useState, type SubmitEvent } from "react";
import ActionButton from "../../components/ActionButtonComponent";
import InputComponent from "../../components/formelements/InputComponent";
import LoadingPortalComponent from "../../components/LoadingPortalComponent";
import ModalComponent from "../../components/ModalComponent";
import Notification from "../../components/notifications/Notification";
import { useNotifications } from "../../components/notifications/useNotifications";
import SectionLayoutComponent from "../../components/SectionLayoutComponent";
import usePrincipal, {
    AuthStatus,
    type UserProfile,
} from "../../context/usePrincipal";
import type { LoginRequest } from "../../services/AuthService";
import UserService from "../../services/UserService";
import Logo from "/favicon.png";

export default function LandingPage() {
    const { status, login } = usePrincipal();

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

    const [loginModal, setLoginModal] = useState<"login" | "register" | null>(
        null,
    );
    const { notifications: allNotifications, setNotifications } =
        useNotifications(["login", "register"]);
    const notifications = loginModal
        ? (allNotifications[loginModal] ?? null)
        : null;
    const [isLoading, setIsLoading] = useState<boolean>(false);

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
        UserService.register<UserProfile>(formData)
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
            <div className="z-0 absolute top-0 right-0 p-8">
                <ActionButton
                    icon={theme === "light" ? MoonIcon : SunIcon}
                    onClick={() => {
                        setTheme((prev) =>
                            prev === "light" ? "dark" : "light",
                        );
                    }}
                    className="btn-secondary rounded-full p-2"
                />
            </div>
            <SectionLayoutComponent className="z-1 p-0!">
                <div className="h-screen flex flex-col items-center justify-center gap-4 *:text-center">
                    <div className="flex items-center gap-2">
                        <img src={Logo} alt="TTS" className="h-30" />
                        {/* <div className="text-primary font-bold">
                        <h1>TTS</h1>
                        <p className="text-sm">Text To Speech</p>
                    </div> */}
                    </div>
                    <h2>Turn Your Text Into Natural Speech</h2>
                    <p>
                        Convert text into clear, natural-sounding audio with
                        multiple languages, voices, and AI-powered text
                        enhancement.
                    </p>
                    <ActionButton
                        className="btn-primary rounded-full px-4 p-2"
                        text="Convert Now"
                        icon={ArrowRightIcon}
                        onClick={() => setLoginModal("login")}
                    />
                </div>
                <ModalComponent
                    title={loginModal + ""}
                    isOpen={loginModal != null}
                    onClose={() => setLoginModal(null)}
                    icon={
                        loginModal === "login" ? LockClosedIcon : UserPlusIcon
                    }
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
                            className="capitalize btn-primary rounded-full w-full justify-center"
                            text={loginModal + ""}
                            loading={isLoading}
                            icon={
                                loginModal === "login" ? KeyIcon : UserPlusIcon
                            }
                            disabled={isLoading}
                        />
                        <p className="text-style-secondary text-center text-xs">
                            OR
                        </p>
                        <ActionButton
                            type="button"
                            className="capitalize btn-secondary rounded-full w-full justify-center"
                            icon={
                                loginModal === "login"
                                    ? UserPlusIcon
                                    : LockClosedIcon
                            }
                            text={loginModal === "login" ? "Register" : "Login"}
                            onClick={() => {
                                setLoginModal(
                                    loginModal === "login"
                                        ? "register"
                                        : "login",
                                );
                            }}
                        />
                    </form>
                </ModalComponent>
                <LoadingPortalComponent
                    isLoading={status === AuthStatus.INITIALIZING}
                    message="Preparing your session."
                />
            </SectionLayoutComponent>
        </>
    );
}
