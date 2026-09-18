import {
    PencilIcon,
    PlusCircleIcon,
    TrashIcon,
    UserPlusIcon,
} from "@heroicons/react/24/outline";
import {
    useCallback,
    useEffect,
    useMemo,
    useState,
    type SubmitEvent,
} from "react";
import { useNavigate } from "react-router-dom";
import ActionButton from "../../components/ActionButtonComponent";
import InputComponent from "../../components/formelements/InputComponent";
import SelectComponent from "../../components/formelements/SelectComponent";
import ModalComponent from "../../components/ModalComponent";
import Notification from "../../components/notifications/Notification";
import { useNotifications } from "../../components/notifications/useNotifications";
import usePagination from "../../components/pagination/usePagination";
import SectionLayoutComponent from "../../components/SectionLayoutComponent";
import SpinnerComponent from "../../components/SpinnerComponent";
import usePrincipal, {
    RoleType,
    UserGender,
    type UserProfile,
} from "../../context/usePrincipal";
import { RoutePaths } from "../../routes/RoutePaths";
import UserService from "../../services/UserService";
import UserCard from "./UserCard";

export default function UsersPage() {
    const { profile } = usePrincipal();
    const navigate = useNavigate();

    const [addModal, setAddModal] = useState<boolean>(false);

    const [loading, setLoading] = useState<boolean>(true);
    const [creationInProgress, setCreationInProgress] =
        useState<boolean>(false);

    const [allUsers, setAllUsers] = useState<UserProfile[]>([]);
    const [searchQuery, setSearchQuery] = useState<string | null>(null);

    const { notifications, setNotifications } = useNotifications(["newuser"]);

    const refreshUsers = useCallback(() => {
        UserService.getAllUsers<UserProfile[]>()
            .then((resp) => {
                if (resp && !("errorMessage" in resp)) {
                    setAllUsers(
                        resp.data
                            .filter((user) => user.id != profile?.id)
                            .map((user) => ({
                                ...user,
                                name: `${user.firstName} ${user.lastName}`,
                            })),
                    );
                }
            })
            .finally(() => {
                setLoading(false);
            });
    }, [profile]);

    useEffect(() => {
        refreshUsers();
    }, [refreshUsers]);

    const users = useMemo(() => {
        if (!searchQuery || searchQuery == "") {
            return allUsers;
        }

        const query = searchQuery.toLowerCase().trim();

        allUsers.filter(
            (user) =>
                user.email.toLowerCase().includes(query) ||
                user.name.toLowerCase().includes(query),
        );
    }, [searchQuery, allUsers]);

    const pagination = usePagination(users ?? [], 10);

    function addUser(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);
        setCreationInProgress(true);

        const payload = {
            role: formData.get("role"),
            email: formData.get("email"),
            firstName: formData.get("firstName"),
            lastName: formData.get("lastName"),
            phone: formData.get("phone"),
            dob: formData.get("dob"),
            gender: formData.get("gender"),
            address: {
                street: formData.get("street"),
                pinCode: formData.get("pinCode"),
                state: formData.get("state"),
                country: formData.get("country"),
            },
        };

        UserService.createUser<UserProfile>(payload)
            .then((resp) => {
                if (resp && !("errorMessage" in resp)) {
                    setNotifications("newuser", {
                        type: "success",
                        messages: ["User registered successfully"],
                    });
                } else {
                    setNotifications("newuser", {
                        type: "error",
                        messages:
                            resp.validationErrors &&
                            resp.validationErrors.length > 0
                                ? resp.validationErrors.map(
                                      (ve) => ve.field + " " + ve.message,
                                  )
                                : [resp.errorMessage],
                    });
                }
            })
            .finally(() => {
                setCreationInProgress(false);
            });
    }

    const deleteUser = useCallback(
        (user: UserProfile) => {
            if (
                window.confirm(
                    "Are You Sure? You Want to delete '" + user.email + "'?",
                )
            ) {
                UserService.deleteProfile<{ status: boolean }>(user.id).then(
                    (resp) => {
                        if (resp && "errorMessage" in resp) {
                            window.alert(resp.errorMessage);
                        } else {
                            window.alert("User Deleted Successfully");
                        }
                        refreshUsers();
                    },
                );
            }
        },
        [refreshUsers],
    );

    return (
        <>
            <SectionLayoutComponent
                title="Manage Users"
                description="You can manage registered users from this page."
                actionEvents={[
                    {
                        icon: UserPlusIcon,
                        text: "New User",
                        onClick() {
                            setAddModal(true);
                        },
                    },
                ]}
                search={{
                    type: "text",
                    placeholder: "Email | Name",
                    className: "w-full md:w-1/2",
                    onChange(e) {
                        setSearchQuery(e.target.value);
                    },
                }}
                pagination={pagination}
            >
                {loading ? (
                    <SpinnerComponent text="Fetching users..." />
                ) : pagination.currentItems.length == 0 ? (
                    searchQuery != null && searchQuery != "" ? (
                        <p>No user except you in this application</p>
                    ) : (
                        <p>No results for given search</p>
                    )
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3">
                        {pagination.currentItems.map((user) => {
                            return (
                                <UserCard
                                    key={user.id}
                                    user={user}
                                    actionEvents={[
                                        {
                                            icon: PencilIcon,
                                            // text: "Edit",
                                            onClick() {
                                                navigate(
                                                    RoutePaths.USER_DETAILS.replace(
                                                        ":userId",
                                                        user.id,
                                                    ),
                                                );
                                            },
                                        },
                                        {
                                            icon: TrashIcon,
                                            // text: "Delete",
                                            onClick() {
                                                deleteUser(user);
                                            },
                                        },
                                    ]}
                                />
                            );
                        })}
                    </div>
                )}
            </SectionLayoutComponent>
            <ModalComponent
                title="Add User"
                icon={UserPlusIcon}
                isOpen={addModal}
                onClose={() => {
                    setAddModal(!addModal);
                }}
                maxWidthClass="max-w-xl"
            >
                <form
                    onSubmit={addUser}
                    className="grid grid-cols-1 md:grid-cols-2 gap-2 p-1"
                >
                    {notifications["newuser"] && (
                        <div className="col-span-full">
                            <Notification
                                type={notifications["newuser"]?.type}
                                messages={notifications["newuser"]?.messages}
                            />
                        </div>
                    )}
                    <h3 className="col-span-full text-style-secondary text-sm uppercase mt-2">
                        Role
                    </h3>
                    <label htmlFor="role">
                        <SelectComponent
                            id="role"
                            name="role"
                            emptyOption="User Role"
                            options={Object.keys(RoleType).map((role) => ({
                                value: role,
                            }))}
                            required
                        ></SelectComponent>
                    </label>
                    <h3 className="col-span-full text-style-secondary text-sm uppercase mt-2">
                        User Info
                    </h3>
                    <label htmlFor="email">
                        <span>Email</span>
                        <InputComponent
                            type="email"
                            id="email"
                            name="email"
                            placeholder="User Email"
                            required
                        />
                    </label>
                    <label htmlFor="firstName">
                        <span>First Name</span>
                        <InputComponent
                            type="text"
                            id="firstName"
                            name="firstName"
                            placeholder="First Name"
                            required
                        />
                    </label>
                    <label htmlFor="lastName">
                        <span>Last Name</span>
                        <InputComponent
                            type="text"
                            id="lastName"
                            name="lastName"
                            placeholder="Last Name"
                            required
                        />
                    </label>
                    <label htmlFor="phone">
                        <span>Phone</span>
                        <InputComponent
                            type="number"
                            id="phone"
                            name="phone"
                            placeholder="Phone"
                            onWheel={(e) =>
                                (e.target as HTMLInputElement).blur()
                            }
                            onKeyDown={(e) => {
                                if (
                                    ["e", "E", "-", "+", ".", ","].includes(
                                        e.key,
                                    )
                                )
                                    e.preventDefault();
                            }}
                            required
                        />
                    </label>
                    <label htmlFor="dob">
                        <span>DOB</span>
                        <InputComponent
                            type="date"
                            id="dob"
                            name="dob"
                            required
                        />
                    </label>
                    <label htmlFor="gender">
                        <span>Gender</span>
                        <SelectComponent
                            id="gender"
                            name="gender"
                            emptyOption="Gender"
                            options={Object.keys(UserGender).map((gender) => ({
                                value: gender,
                            }))}
                            required
                        />
                    </label>
                    <h3 className="col-span-full text-style-secondary text-sm uppercase mt-2">
                        User Address
                    </h3>
                    <label htmlFor="street">
                        <span>Street</span>
                        <InputComponent
                            type="text"
                            id="street"
                            name="street"
                            placeholder="Street"
                            required
                        />
                    </label>
                    <label htmlFor="pinCode">
                        <span>Pincode</span>
                        <InputComponent
                            type="number"
                            id="pinCode"
                            name="pinCode"
                            placeholder="Pincode"
                            onWheel={(e) =>
                                (e.target as HTMLInputElement).blur()
                            }
                            onKeyDown={(e) => {
                                if (
                                    ["e", "E", "-", "+", ".", ","].includes(
                                        e.key,
                                    )
                                )
                                    e.preventDefault();
                            }}
                            required
                        />
                    </label>
                    <label htmlFor="state">
                        <span>State</span>
                        <InputComponent
                            type="text"
                            id="state"
                            name="state"
                            placeholder="State"
                            required
                        />
                    </label>
                    <label htmlFor="country">
                        <span>Country</span>
                        <InputComponent
                            type="text"
                            id="country"
                            name="country"
                            placeholder="Country"
                            required
                        />
                    </label>
                    <hr className="col-span-full" />
                    <div className="col-span-full">
                        <ActionButton
                            icon={PlusCircleIcon}
                            className="btn-primary w-fit ml-auto"
                            text={
                                creationInProgress
                                    ? "Registering.."
                                    : "Register"
                            }
                            type="submit"
                            disabled={creationInProgress}
                        />
                    </div>
                </form>
            </ModalComponent>
        </>
    );
}
