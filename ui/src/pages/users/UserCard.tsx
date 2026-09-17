import MaleAvatar from "../../assets/undraw_reading-notes_dg9z.svg";
import FemaleAvatar from "../../assets/undraw_cool-break_cipj.svg";
import type { ActionButtonProps } from "../../components/ActionButtonComponent";
import ActionButton from "../../components/ActionButtonComponent";
import type { UserProfile } from "../../context/usePrincipal";

export type UserCardProps = {
    user: UserProfile;
    actionEvents?: ActionButtonProps[];
};

export default function UserCard({ user, actionEvents }: UserCardProps) {
    return (
        <div className="p-2.5 border bg-slate-50 dark:bg-slate-800 rounded-md">
            <div className="space-y-3">
                <div className="size-30 mx-auto border rounded-full drop-shadow-sm bg-primary-light dark:bg-white">
                    <img
                        src={
                            user.gender === "FEMALE" ? FemaleAvatar : MaleAvatar
                        }
                        alt={user.name + "'s DP"}
                        className="w-full h-full"
                    />
                </div>
                <hr className="w-1/2 mx-auto" />
                <div className="flex flex-col items-center gap-1">
                    <p className="p-1 text-xs bg-secondary rounded-md text-white w-fit uppercase">
                        {user.roles[0]}
                    </p>
                    <p>{user.name}</p>
                    <p>{user.email}</p>
                </div>
                {actionEvents && (
                    <div className="flex justify-center *:rounded-sm gap-1">
                        {actionEvents.map((act, idx) => {
                            return (
                                <ActionButton
                                    key={"UserCard" + idx}
                                    {...act}
                                    className={`btn-primary p-1.5 gap-1 ${act.className}`}
                                />
                            );
                        })}
                    </div>
                )}
            </div>
        </div>
    );
}
