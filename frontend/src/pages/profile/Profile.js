import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import "./Profile.css";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {
  FaPen,
  FaUserCircle,
  FaAward,
  FaBook,
  FaCheck,
  FaTimes,
} from "react-icons/fa";
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
import { useIntl } from "react-intl";

import { toast } from "react-toastify";

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
  const setStoreAvatar = useAuthStore((state) => state.setAvatar);

  //Avatar
  const [avatarPreview, setAvatarPreview] = useState(null);
  const [avatarFile, setAvatarFile] = useState(null);

  const navigate = useNavigate();

  // Internacionalização
  const intl = useIntl();

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
        if (response?.success) {
          if (avatarFile && avatarPreview) {
            updatedUser.hasAvatar = true;
            setUserAvatar(avatarPreview);
            setStoreAvatar(avatarPreview, avatarFile);
            setAvatarPreview(null);
            setAvatarFile(null);
          }
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
        <span>
          {intl.formatMessage({
            id:
              tab === "profile"
                ? "profileTabProfile"
                : tab === "appraisals"
                ? "profileTabAppraisals"
                : "profileTabTraining",
          })}
        </span>
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
        <div className="profile-actions">
          {editMode && (
            <>
              {/* Botão desktop/tablet: texto + ícone */}
              <button
                className="edit-btn edit-cancel-btn"
                type="button"
                onClick={() => {
                  setEditMode(false);
                  setShowAddressFields(false);
                  reset(user);
                  setAvatarPreview(null);
                  setAvatarFile(null);
                }}
              >
                <FaTimes style={{ marginRight: 8 }} />
                {intl.formatMessage({ id: "profileCancel" })}
              </button>
            </>
          )}
          <button
            className={`edit-btn${editMode ? " edit-save-btn" : ""}`}
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
            {editMode ? (
              <>
                <FaCheck style={{ marginRight: 8 }} />
                {intl.formatMessage({ id: "profileSave" })}
              </>
            ) : (
              <>
                <FaPen style={{ marginRight: 8 }} />
                {intl.formatMessage({ id: "profileEdit" })}
              </>
            )}
          </button>
          {/* Botões mobile (ícones) */}
          {editMode && (
            <button
              className="icon-btn edit-cancel-btn"
              type="button"
              title={intl.formatMessage({ id: "profileCancel" })}
              onClick={() => {
                setEditMode(false);
                setShowAddressFields(false);
                reset(user);
                setAvatarPreview(null);
                setAvatarFile(null);
              }}
            >
              <FaTimes />
            </button>
          )}
          <button
            className={`icon-btn edit-save-btn`}
            type={editMode ? "submit" : "button"}
            title={
              editMode
                ? intl.formatMessage({ id: "profileSave" })
                : intl.formatMessage({ id: "profileEdit" })
            }
            onClick={
              editMode
                ? handleSubmit(onSubmit)
                : () => {
                    setEditMode(true);
                    setShowAddressFields(true);
                  }
            }
          >
            {editMode ? <FaCheck /> : <FaPen />}
          </button>
        </div>
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
                  {intl.formatMessage({ id: "profileFirstName" })}
                  <input
                    className="profile-input"
                    {...register("name", {
                      required: intl.formatMessage({
                        id: "profileErrorFirstNameRequired",
                      }),
                      minLength: {
                        value: 2,
                        message: intl.formatMessage({
                          id: "profileErrorFirstNameRequired",
                        }),
                      },
                      pattern: {
                        value: /^[A-Za-zÀ-ÿ\s'-]+$/,
                        message: intl.formatMessage({
                          id: "profileErrorFirstNameInvalid",
                        }), // NOVO ID
                      },
                    })}
                    disabled={!editMode}
                    placeholder={intl.formatMessage({
                      id: "profilePlaceholderNA",
                    })}
                  />
                  {errors.name && (
                    <span className="error-message">{errors.name.message}</span>
                  )}
                </label>
                <label>
                  {intl.formatMessage({ id: "profileLastName" })}
                  <input
                    className="profile-input"
                    {...register("surname", {
                      required: intl.formatMessage({
                        id: "profileErrorLastNameRequired",
                      }),
                      minLength: {
                        value: 2,
                        message: intl.formatMessage({
                          id: "profileErrorLastNameRequired",
                        }),
                      },
                      pattern: {
                        value: /^[A-Za-zÀ-ÿ\s'-]+$/,
                        message: intl.formatMessage({
                          id: "profileErrorLastNameInvalid",
                        }), // NOVO ID
                      },
                    })}
                    disabled={!editMode}
                    placeholder={intl.formatMessage({
                      id: "profilePlaceholderNA",
                    })}
                  />
                  {errors.surname && (
                    <span className="error-message">
                      {errors.surname.message}
                    </span>
                  )}
                </label>
                <label>
                  {intl.formatMessage({ id: "profileBirthDate" })}
                  <input
                    type="date"
                    className="profile-input"
                    {...register("birthdate", {
                      required: intl.formatMessage({
                        id: "profileErrorBirthDateRequired",
                      }),
                      validate: (value) => {
                        const today = new Date().toISOString().split("T")[0];
                        if (value > today) {
                          return intl.formatMessage({
                            id: "profileErrorBirthDateFuture",
                          }); // NOVO ID
                        }
                        const minDate = new Date();
                        minDate.setFullYear(minDate.getFullYear() - 16);
                        if (value > minDate.toISOString().split("T")[0]) {
                          return intl.formatMessage({
                            id: "profileErrorBirthDateTooYoung",
                          }); // NOVO ID
                        }
                        return true;
                      },
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
                  {intl.formatMessage({ id: "profileRole" })}
                  {editMode ? (
                    <div className="select-wrapper">
                      <select
                        className="profile-input"
                        {...register("role", {
                          required: intl.formatMessage({
                            id: "profileErrorRoleRequired",
                          }),
                        })}
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
                  {intl.formatMessage({ id: "profileWorkplace" })}
                  {editMode ? (
                    <div className="select-wrapper">
                      <select
                        className="profile-input"
                        {...register("office", {
                          required: intl.formatMessage({
                            id: "profileErrorWorkplaceRequired",
                          }),
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
                  {intl.formatMessage({ id: "profilePhone" })}
                  <input
                    className="profile-input"
                    {...register("phone", {
                      required: intl.formatMessage({
                        id: "profileErrorPhoneRequired",
                      }),
                      pattern: {
                        value: /^[0-9]{9}$/,
                        message: intl.formatMessage({
                          id: "errorPhoneNumberInvalid",
                        }), // JÁ EXISTE
                      },
                    })}
                    disabled={!editMode}
                    placeholder={intl.formatMessage({
                      id: "profilePlaceholderNA",
                    })}
                  />
                  {errors.phone && (
                    <span className="error-message">
                      {errors.phone.message}
                    </span>
                  )}
                </label>
                <div className="address-container">
                  <label>
                    {intl.formatMessage({ id: "profileAddress" })}
                    <input
                      className="profile-input"
                      value={[user.street, user.postalCode, user.municipality]
                        .filter(Boolean)
                        .join(", ")}
                      disabled
                      placeholder={intl.formatMessage({
                        id: "profilePlaceholderNA",
                      })}
                    />
                  </label>
                  {editMode && (
                    <div
                      className={`address-edit-fields${
                        showAddressFields ? " slide-in" : ""
                      }`}
                    >
                      <label>
                        {intl.formatMessage({ id: "profileAddressStreet" })}
                        <input
                          className="profile-input"
                          {...register("street", {
                            required: intl.formatMessage({
                              id: "profileErrorAddressStreetRequired",
                            }),
                            minLength: {
                              value: 3,
                              message: intl.formatMessage({
                                id: "profileErrorAddressStreetRequired",
                              }),
                            },
                            pattern: {
                              value: /^[A-Za-zÀ-ÿ0-9\s'-,.]+$/,
                              message: intl.formatMessage({
                                id: "profileErrorAddressStreetInvalid",
                              }), // NOVO ID
                            },
                          })}
                          placeholder={intl.formatMessage({
                            id: "profileAddressStreet",
                          })}
                        />
                        {errors.street && (
                          <span className="error-message">
                            {errors.street.message}
                          </span>
                        )}
                      </label>
                      <label>
                        {intl.formatMessage({ id: "profileAddressPostalCode" })}
                        <input
                          className="profile-input"
                          {...register("postalCode", {
                            required: intl.formatMessage({
                              id: "profileErrorAddressPostalCodeRequired",
                            }),
                            pattern: {
                              value: /^[0-9]{4}-[0-9]{3}$/,
                              message: intl.formatMessage({
                                id: "errorPostalCodeInvalid",
                              }), // NOVO ID
                            },
                          })}
                          placeholder={intl.formatMessage({
                            id: "profileAddressPostalCode",
                          })}
                        />
                        {errors.postalCode && (
                          <span className="error-message">
                            {errors.postalCode.message}
                          </span>
                        )}
                      </label>
                      <label>
                        {intl.formatMessage({
                          id: "profileAddressMunicipality",
                        })}
                        <input
                          className="profile-input"
                          {...register("municipality", {
                            required: intl.formatMessage({
                              id: "profileErrorAddressMunicipalityRequired",
                            }),
                            minLength: {
                              value: 2,
                              message: intl.formatMessage({
                                id: "profileErrorAddressMunicipalityRequired",
                              }),
                            },
                            pattern: {
                              value: /^[A-Za-zÀ-ÿ\s'-]+$/,
                              message: intl.formatMessage({
                                id: "profileErrorAddressMunicipalityInvalid",
                              }), // NOVO ID
                            },
                          })}
                          placeholder={intl.formatMessage({
                            id: "profileAddressMunicipality",
                          })}
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
                  {intl.formatMessage({ id: "profileBiography" })}
                  <textarea
                    className="profile-input"
                    {...register("biography")}
                    disabled={!editMode}
                    placeholder={intl.formatMessage({ id: "profileBiography" })}
                  />
                </label>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
