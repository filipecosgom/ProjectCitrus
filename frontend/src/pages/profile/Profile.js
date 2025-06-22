import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import "./Profile.css";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { FaPen, FaUserCircle, FaAward, FaBook } from "react-icons/fa";
import UserIcon from "../../components/userIcon/UserIcon";
import ProfilePhoto from "../../assets/photos/teresamatos.png";
import ManagerPhoto from "../../assets/photos/joseferreira.png";
import Spinner from "../../components/spinner/spinner";
import handleGetUserInformation from "../../handles/handleGetUserInformation";
import handleGetUserAvatar from "../../handles/handleGetUserAvatar";
import template_backup from "../../assets/photos/template_backup.png";
import { handleUpdateUserInfo } from "../../handles/handleUpdateUser";
import { handleGetRoles, handleGetOffices } from "../../handles/handleGetEnums";
import useAuthStore from "../../stores/useAuthStore";
import handleNotification from "../../handles/handleNotification";

export default function Profile() {
  const userId = new URLSearchParams(useLocation().search).get("id");
  const [user, setUser] = useState(null);
  const [userAvatar, setUserAvatar] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [roleOptions, setRoleOptions] = useState([]);
  const [officeOptions, setOfficeOptions] = useState([]);
  const [showAddressFields, setShowAddressFields] = useState(false);
  const [activeTab, setActiveTab] = useState("profile");
  const setUserAndExpiration = useAuthStore(
    (state) => state.setUserAndExpiration
  );

  //Avatar
  const [avatarPreview, setAvatarPreview] = useState(null);
  const [avatarFile, setAvatarFile] = useState(null);

  const navigate = useNavigate();

  // react-hook-form setup
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm();

  // Carrega dados do utilizador
  useEffect(() => {
    const fetchUserInformation = async () => {
      try {
        const userInfo = await handleGetUserInformation(userId);
        console.log(userInfo);
        if (userInfo) {
          setUser(userInfo);
          reset(userInfo); // Preenche o formulário com os dados do utilizador
        }
        if (userInfo.hasAvatar) {
          const userAvatar = await handleGetUserAvatar(userId);
          if (userAvatar) {
            setUserAvatar(userAvatar.avatar);
          }
        }
      } catch (error) {
        console.log(error);
        navigate("/");
      } finally {
        setLoading(false);
      }
    };
    fetchUserInformation();
  }, [userId, reset, navigate]);

  // Carrega enums
  useEffect(() => {
    const fetchEnums = async () => {
      setRoleOptions(await handleGetRoles());
      setOfficeOptions(await handleGetOffices());
    };
    fetchEnums();
  }, []);

  const onSubmit = async (data) => {
  setLoading(true);
  
  try {
    const response = await handleUpdateUserInfo(
      userId, 
      user, 
      data, 
      avatarFile // Pass the avatar file separately
    );

    if (response?.success) {
      // Update local state
      const updatedUser = { ...user, ...data };
      if (response.avatar) {
        updatedUser.hasAvatar = true;
        setUserAvatar(response.avatar);
        setAvatarPreview(null);
        setAvatarFile(null);
      }
      
      setUser(updatedUser);
      setUserAndExpiration(
        updatedUser, 
        useAuthStore.getState().tokenExpiration
      );
      
      setEditMode(false);
      setShowAddressFields(false);
    }
  } catch (error) {
    console.error("Update error:", error);
  } finally {
    setLoading(false);
  }
};

  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    console.log(file);
    if (!file) return;

    // Validate file type and size
    const validTypes = ["image/jpeg", "image/png", "image/webp"];
    const maxSize = 5 * 1024 * 1024; // 5mb

    if (!validTypes.includes(file.type)) {
      handleNotification("error", "invalidImageType");
      return;
    }

    if (file.size > maxSize) {
      handleNotification("error", "imageTooLarge");
      return;
    }

    setAvatarFile(file);
    setAvatarPreview(URL.createObjectURL(file));
  };

  // Define as cores dos ícones conforme Menu.js
  const tabIcons = {
    profile: {
      icon: FaUserCircle,
      color: "#000000",
    },
    appraisals: {
      icon: FaAward,
      color: "#FDD835",
    },
    training: {
      icon: FaBook,
      color: "#FF5900",
    },
  };

  const renderTab = (tab) => {
    const { icon: Icon, color } = tabIcons[tab];
    return (
      <button
        className={`tab${activeTab === tab ? " active" : ""}`}
        onClick={() => setActiveTab(tab)}
        type="button"
      >
        <Icon
          style={{
            color: color,
            fontSize: "1.2em",
          }}
        />
        <span>{tab.charAt(0).toUpperCase() + tab.slice(1)}</span>
      </button>
    );
  };

  if (loading) return <Spinner />;

  return (
    <div className="user-profile">
      <div className="profile-tabs-row">
        {user ? <UserIcon avatar={userAvatar} status="check" /> : null}
        <div className="tabs">
          {renderTab("profile")}
          {renderTab("appraisals")}
          {renderTab("training")}
        </div>
        <button
          className="edit-btn"
          type={editMode ? "submit" : "button"}
          onClick={
            editMode
              ? handleSubmit(onSubmit)
              : () => {
                  setEditMode(true);
                  setShowAddressFields(true);
                }
          }
        >
          {editMode ? "Save" : "Edit"}
        </button>
      </div>
      {activeTab === "profile" && (
        <div className="profile-section">
          <div className="profile-header">
            <form className="profile-form" onSubmit={handleSubmit(onSubmit)}>
              <div className="profile-card">
                <img
                  src={
                    avatarPreview
                      ? avatarPreview
                      : userAvatar
                      ? userAvatar
                      : template_backup
                  }
                  alt="Profile"
                  onError={(e) => {
                    e.target.onerror = null;
                    e.target.src = ProfilePhoto;
                  }}
                />
                <div className="profile-label">
                  <strong>
                    {user.name} {user.surname}
                  </strong>
                  <div>{user.role ? user.role.replace(/_/g, " ") : ""}</div>
                </div>
                {editMode && (
                  <>
                    <input
                      type="file"
                      accept="image/*"
                      id="avatar-upload"
                      className="profile-avatar-input"
                      onChange={handleAvatarChange}
                    />
                    <label
                      htmlFor="avatar-upload"
                      className="profile-avatar-edit-label"
                    >
                      <FaPen
                        className="profile-avatar-edit-icon"
                        title="Edit avatar"
                      />
                    </label>
                  </>
                )}
              </div>
              {/* Manager card desktop/tablet */}
              <div className="manager-card show-desktop">
                <img src={user.manager?.avatar || ManagerPhoto} alt="Manager" />
                <div className="profile-label small">
                  <strong>
                    {user.manager?.name} {user.manager?.surname}
                  </strong>
                  <div>{user.manager?.role}</div>
                </div>
              </div>

              {/* Manager card compacto mobile */}
              <div className="manager-compact-card show-mobile">
                <span className="manager-compact-role">
                  Manager
                  <br />
                </span>
                <div className="manager-compact-avatar">
                  <img
                    src={user.manager?.avatar || ManagerPhoto}
                    alt="Manager"
                    className="manager-avatar"
                  />
                </div>
                <div className="manager-compact-info">
                  <div className="manager-compact-name">
                    {user.manager?.name} {user.manager?.surname}
                  </div>
                  <div className="manager-compact-email">
                    {user.manager?.email}
                  </div>
                </div>
              </div>
              <div className="form-fields">
                <label>
                  First Name
                  <input
                    className="profile-input"
                    {...register("name", {
                      required: "First name is required",
                    })}
                    disabled={!editMode}
                    placeholder="N/A"
                  />
                  {errors.name && (
                    <span className="error-message">{errors.name.message}</span>
                  )}
                </label>
                <label>
                  Last Name
                  <input
                    className="profile-input"
                    {...register("surname", {
                      required: "Last name is required",
                    })}
                    disabled={!editMode}
                    placeholder="N/A"
                  />
                  {errors.surname && (
                    <span className="error-message">
                      {errors.surname.message}
                    </span>
                  )}
                </label>
                <label>
                  Date of Birth
                  <input
                    type="date"
                    className="profile-input"
                    {...register("birthdate", {
                      required: "Birthdate is required",
                    })}
                    disabled={!editMode}
                  />
                  {errors.birthdate && (
                    <span className="error-message">
                      {errors.birthdate.message}
                    </span>
                  )}
                </label>
                <label>
                  Role
                  {editMode ? (
                    <div className="select-wrapper">
                      <select
                        className="profile-input"
                        {...register("role", { required: "Role is required" })}
                      >
                        <option value="">Select role</option>
                        {roleOptions.map((role) => (
                          <option key={role} value={role}>
                            {role
                              .toLowerCase()
                              .split("_")
                              .map(
                                (word) =>
                                  word.charAt(0).toUpperCase() + word.slice(1)
                              )
                              .join(" ")}
                          </option>
                        ))}
                      </select>
                      <span className="select-arrow">
                        <svg
                          width="16"
                          height="16"
                          viewBox="0 0 20 20"
                          fill="none"
                        >
                          <path
                            d="M6 8l4 4 4-4"
                            stroke="currentColor"
                            strokeWidth="2"
                            fill="none"
                            strokeLinecap="round"
                          />
                        </svg>
                      </span>
                    </div>
                  ) : (
                    <input
                      className="profile-input"
                      value={user.role ? user.role.replace(/_/g, " ") : ""}
                      disabled
                    />
                  )}
                </label>
                <label>
                  Workplace
                  {editMode ? (
                    <div className="select-wrapper">
                      <select
                        className="profile-input"
                        {...register("office", {
                          required: "Office is required",
                        })}
                      >
                        <option value="">Select office</option>
                        {officeOptions.map((office) => (
                          <option key={office} value={office}>
                            {office
                              .toLowerCase()
                              .split("_")
                              .map(
                                (word) =>
                                  word.charAt(0).toUpperCase() + word.slice(1)
                              )
                              .join(" ")}
                          </option>
                        ))}
                      </select>
                      <span className="select-arrow">
                        <svg
                          width="16"
                          height="16"
                          viewBox="0 0 20 20"
                          fill="none"
                        >
                          <path
                            d="M6 8l4 4 4-4"
                            stroke="currentColor"
                            strokeWidth="2"
                            fill="none"
                            strokeLinecap="round"
                          />
                        </svg>
                      </span>
                    </div>
                  ) : (
                    <input
                      className="profile-input"
                      value={user.office ? user.office.replace(/_/g, " ") : ""}
                      disabled
                    />
                  )}
                </label>
                <label>
                  Phone
                  <input
                    className="profile-input"
                    {...register("phone", {
                      required: "Phone is required",
                      pattern: {
                        value: /^[0-9+\s()-]{6,}$/,
                        message: "Invalid phone number",
                      },
                    })}
                    disabled={!editMode}
                    placeholder="N/A"
                  />
                  {errors.phone && (
                    <span className="error-message">
                      {errors.phone.message}
                    </span>
                  )}
                </label>
                <div className="address-container">
                  <label>
                    Address
                    <input
                      className="profile-input"
                      value={[user.street, user.postalCode, user.municipality]
                        .filter(Boolean)
                        .join(", ")}
                      disabled
                      placeholder="N/A"
                    />
                  </label>
                  {editMode && (
                    <div
                      className={`address-edit-fields${
                        showAddressFields ? " slide-in" : ""
                      }`}
                    >
                      <label>
                        Street
                        <input
                          className="profile-input"
                          {...register("street", {
                            required: "Street is required",
                          })}
                          placeholder="Street"
                        />
                        {errors.street && (
                          <span className="error-message">
                            {errors.street.message}
                          </span>
                        )}
                      </label>
                      <label>
                        Postal Code
                        <input
                          className="profile-input"
                          {...register("postalCode", {
                            required: "Postal code is required",
                          })}
                          placeholder="Postal Code"
                        />
                        {errors.postalCode && (
                          <span className="error-message">
                            {errors.postalCode.message}
                          </span>
                        )}
                      </label>
                      <label>
                        Municipality
                        <input
                          className="profile-input"
                          {...register("municipality", {
                            required: "Municipality is required",
                          })}
                          placeholder="Municipality"
                        />
                        {errors.municipality && (
                          <span className="error-message">
                            {errors.municipality.message}
                          </span>
                        )}
                      </label>
                    </div>
                  )}
                </div>
                <label>
                  Biography
                  <textarea
                    className="profile-input"
                    {...register("biography")}
                    disabled={!editMode}
                    placeholder="Biography"
                  />
                </label>
              </div>
            </form>
          </div>
        </div>
      )}
      <ToastContainer />
    </div>
  );
}
