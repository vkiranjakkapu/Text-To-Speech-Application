import {
    ArrowRightEndOnRectangleIcon,
    Bars3Icon,
    ClockIcon,
    HomeIcon,
    MoonIcon,
    SpeakerWaveIcon,
    SunIcon,
    UserGroupIcon,
} from "@heroicons/react/24/outline";

import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Avatar from "../assets/undraw_talking-on-the-phone_lc9v.svg";
import usePrincipal, { RoleType } from "../context/usePrincipal";
import { RoutePaths } from "../routes/RoutePaths";
import ActionButton from "./ActionButtonComponent";
import type { IconProps } from "./commons";

export default function NavbarComponent() {
    const { profile, logout } = usePrincipal();
    const navigate = useNavigate();

    const [showMenu, toggleMenu] = useState<boolean>(false);

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
                        <ul className="p-1 flex flex-col md:flex-row gap-2 md:bg-slate-50 dark:bg-slate-800 w-full md:w-fit md:rounded-full md:shadow-sm">
                            {NavItems.map((item, idx) => {
                                if (
                                    profile &&
                                    !item.roles.includes(profile.roles[0])
                                ) {
                                    return;
                                }

                                const isActive =
                                    location.pathname.startsWith(item.uri) ||
                                    location.pathname === item.uri;

                                return (
                                    <li
                                        key={idx}
                                        className={`p-2 px-2.5 flex gap-1 items-center cursor-pointer transition-colors duration-100 rounded-full 
                                                    ${isActive ? `bg-primary text-white ` : `hover:bg-slate-100 dark:hover:bg-primary-light/10`}
                                                    `}
                                        onClick={() => navigate(item.uri)}
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
        </>
    );
}
