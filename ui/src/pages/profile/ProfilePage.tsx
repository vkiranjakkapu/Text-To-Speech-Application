import { useEffect, useState, type SubmitEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useNotifications } from "../../components/notifications/useNotifications";
import SectionLayoutComponent from "../../components/SectionLayoutComponent";
import type { UserProfile } from "../../context/usePrincipal";
import { RoutePaths } from "../../routes/RoutePaths";
import UserService from "../../services/UserService";
import usePrincipal, { UserGender } from "../../context/usePrincipal";
import InputComponent from "../../components/formelements/InputComponent";
import Notification from "../../components/notifications/Notification";
import { CheckBadgeIcon, KeyIcon } from "@heroicons/react/24/outline";
import ActionButton from "../../components/ActionButtonComponent";
import SelectComponent from "../../components/formelements/SelectComponent";

export default function ProfilePage() {
    const { userId } = useParams<{ userId: string }>();

    const { profile, isAdmin } = usePrincipal();
    const navigate = useNavigate();

    const [fetchedProfile, setFetchedUserProfile] =
        useState<UserProfile | null>(null);
    const userProfile = userId ? fetchedProfile : profile;

    const { notifications, setNotifications } = useNotifications([
        "profile",
        "password",
    ]);

    const profileNotifications = notifications["profile"] ?? null;
    const passwordNotifications = notifications["password"] ?? null;

    useEffect(() => {
        if (!userId) return;

        if (!isAdmin) {
            navigate(RoutePaths.PROFILE);
            return;
        }

        UserService.getUserById<UserProfile>(userId).then((resp) => {
            if (resp && !("errorMessage" in resp)) {
                setFetchedUserProfile(resp.data);
            }
        });
    }, [userId, isAdmin, navigate]);

    function handleProfileUpdate(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        const payload = {
            ...userProfile,
            name: formData.get("firstName") + " " + formData.get("lastName"),
            firstName: formData.get("firstName"),
            lastName: formData.get("lastName"),
            phone: formData.get("phone"),
            gender: formData.get("gender"),
            dob: formData.get("dob"),
            address: userProfile?.address,
        } as UserProfile;

        UserService.updateProfile<UserProfile>(
            String(userId ?? profile?.id),
            payload,
        ).then((resp) => {
            if (resp && !("errorMessage" in resp)) {
                // setFetchedUserProfile({
                //     ...resp.data,
                //     name: `${resp.data.firstName} ${resp.data.lastName}`,
                // });
                setNotifications("profile", {
                    type: "success",
                    messages: ["Profile updated successfully"],
                });
            } else {
                setNotifications("profile", {
                    type: "error",
                    messages: [resp.errorMessage],
                });
            }
        });
    }

    function handlePasswordChange(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        const confirmPassword = formData.get("confirmPassword");
        const newPassword = formData.get("newPassword");

        if (newPassword !== confirmPassword) {
            setNotifications("password", {
                type: "error",
                messages: ["Passwords didn't matched!"],
            });
            return;
        }

        const payload = {
            email: userProfile?.email,
            oldPassword: formData.get("oldPassword"),
            newPassword: formData.get("newPassword"),
        };
        UserService.changePassword<UserProfile>(payload).then((resp) => {
            if (resp && !("errorMessage" in resp)) {
                setNotifications("password", {
                    type: "success",
                    messages: ["Password updated successfully"],
                });
            } else {
                setNotifications("password", {
                    type: "error",
                    messages:
                        resp.validationErrors &&
                        resp.validationErrors.length > 0
                            ? [...resp.validationErrors.map((ve) => ve.message)]
                            : [resp.errorMessage],
                });
                console.log(passwordNotifications);
            }
        });
    }

    return (
        <SectionLayoutComponent>
            <div className="flex flex-wrap gap-2 justify-center *:flex-1">
                <form
                    key={fetchedProfile?.id ?? "loading"}
                    onSubmit={handleProfileUpdate}
                    className="grid grid-cols-1 md:grid-cols-2 gap-3 bg-slate-50 dark:bg-slate-800 p-3 rounded border border-slate-200 dark:border-slate-700 shadow-sm"
                >
                    <h3 className="col-span-full">Edit Profile</h3>
                    {profileNotifications && (
                        <div className="col-span-full">
                            <Notification
                                type={profileNotifications.type}
                                messages={profileNotifications.messages}
                            />
                        </div>
                    )}
                    <InputComponent
                        id="firstName"
                        placeholder="enter firstname"
                        name="firstName"
                        defaultValue={userProfile?.firstName ?? ""}
                        required
                    />
                    <InputComponent
                        id="lastName"
                        placeholder="enter lastname"
                        name="lastName"
                        defaultValue={userProfile?.lastName ?? ""}
                        required
                    />
                    <InputComponent
                        id="email"
                        type="email"
                        placeholder="enter email"
                        name="email"
                        defaultValue={userProfile?.email ?? ""}
                        disabled
                        required
                    />
                    <InputComponent
                        id="phone"
                        placeholder="enter phone"
                        name="phone"
                        defaultValue={userProfile?.phone ?? ""}
                        required
                    />
                    <InputComponent
                        id="date"
                        type="date"
                        placeholder="Date Of Birth"
                        name="dob"
                        defaultValue={userProfile?.dob ?? ""}
                        required
                    />
                    <SelectComponent
                        id="userGender"
                        emptyOption="Gender"
                        options={Object.keys(UserGender).map((g) => ({
                            text: g,
                            value: g,
                        }))}
                        name="gender"
                        defaultValue={userProfile?.gender ?? ""}
                        required
                    />
                    <div className="col-span-full text-end">
                        <ActionButton
                            text="Update"
                            type="submit"
                            icon={CheckBadgeIcon}
                            className="btn-primary"
                        />
                    </div>
                </form>
                {!userId && (
                    <>
                        <form
                            onSubmit={handlePasswordChange}
                            className="grid grid-cols-1 gap-3 bg-slate-50 dark:bg-slate-800 p-3 rounded border border-slate-200 dark:border-slate-700 shadow-sm"
                        >
                            <h3 className="capitalize">Old Password</h3>
                            {passwordNotifications && (
                                <div className="col-span-full">
                                    <Notification
                                        type={passwordNotifications.type}
                                        messages={
                                            passwordNotifications.messages
                                        }
                                    />
                                </div>
                            )}
                            <InputComponent
                                id="currentPass"
                                placeholder="Authenticate"
                                name="oldPassword"
                                required
                            />
                            <div className=""></div>
                            <h3 className="capitalize">new Password</h3>
                            <InputComponent
                                type="password"
                                id="newPassword"
                                placeholder="New password"
                                name="newPassword"
                                onChange={(e) => {
                                    const confirmPass = document.getElementById(
                                        "confirmPassword",
                                    ) as HTMLInputElement;
                                    if (
                                        e.target.value !== "" &&
                                        confirmPass.value !== e.target.value
                                    ) {
                                        setNotifications("password", {
                                            type: "error",
                                            messages: [
                                                "Two Passwords didn't matched!",
                                            ],
                                        });
                                    } else {
                                        setNotifications("password", {
                                            type: "error",
                                            messages: [],
                                        });
                                    }
                                }}
                                required
                            />
                            <InputComponent
                                type="password"
                                id="confirmPassword"
                                placeholder="Confirm password"
                                name="confirmPassword"
                                onChange={(e) => {
                                    const newPass = document.getElementById(
                                        "newPassword",
                                    ) as HTMLInputElement;
                                    if (
                                        e.target.value !== "" &&
                                        newPass.value !== e.target.value
                                    ) {
                                        setNotifications("password", {
                                            type: "error",
                                            messages: [
                                                "Two Passwords didn't matched!",
                                            ],
                                        });
                                    } else {
                                        setNotifications("password", {
                                            type: "error",
                                            messages: [],
                                        });
                                    }
                                }}
                                required
                            />
                            <div className="col-span-full">
                                <ActionButton
                                    type="submit"
                                    text="Change"
                                    icon={KeyIcon}
                                    className="btn-primary"
                                />
                            </div>
                        </form>
                    </>
                )}
            </div>
        </SectionLayoutComponent>
    );
}
