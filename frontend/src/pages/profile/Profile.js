import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import "./Profile.css";
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
import Spinner from "../../components/spinner/spinner";
import handleGetUserInformation from "../../handles/handleGetUserInformation";
import handleGetUserAvatar from "../../handles/handleGetUserAvatar";
import template_backup from "../../assets/templates/template_backup.png";
import { handleUpdateUserInfo } from "../../handles/handleUpdateUser";
import { handleGetRoles, handleGetOffices } from "../../handles/handleGetEnums";
import useAuthStore from "../../stores/useAuthStore";
import handleNotification from "../../handles/handleNotification";
import { useTranslation } from "react-i18next";
import AppraisalsTab from "./AppraisalsTab";
import TrainingTab from "./TrainingTab";
import { generateInitialsAvatar } from "../../components/userOffcanvas/UserOffcanvas";

export default function Profile() {
  const { t } = useTranslation();
  const location = useLocation();
  const navigate = useNavigate();
  const userId = new URLSearchParams(location.search).get("id");
  // Read tab from URL, default to 'profile'
  const tabFromUrl = new URLSearchParams(location.search).get("tab") || "profile";
  const [user, setUser] = useState(null);
  const [userAvatar, setUserAvatar] = useState(null);
  const [managerAvatar, setManagerAvatar] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [roleOptions, setRoleOptions] = useState([]);
  const [officeOptions, setOfficeOptions] = useState([]);
  const [showAddressFields, setShowAddressFields] = useState(false);
  const [activeTab, setActiveTab] = useState(tabFromUrl);
  const setUserAndExpiration = useAuthStore(
    (state) => state.setUserAndExpiration
  );
  const setStoreAvatar = useAuthStore((state) => state.setAvatar);

  //Avatar
  const [avatarPreview, setAvatarPreview] = useState(null);
  const [avatarFile, setAvatarFile] = useState(null);

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
        if (userInfo.manager) {
          if (userInfo.manager.hasAvatar) {
            const managerAvatar = await handleGetUserAvatar(
              userInfo.manager.id
            );
            if (managerAvatar) {
              setManagerAvatar(managerAvatar.avatar);
            }
          }
        }
      } catch (error) {
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
    console.log(data);
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
          {t(
            tab === "profile"
              ? "profileTabProfile"
              : tab === "appraisals"
              ? "profileTabAppraisals"
              : "profileTabTraining"
          )}
        </span>
      </button>
    );
  };

  // Keep activeTab in sync with URL
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (activeTab !== params.get("tab")) {
      params.set("tab", activeTab);
      navigate({ search: params.toString() }, { replace: true });
    }
    // eslint-disable-next-line
  }, [activeTab]);

  // Update activeTab if URL changes (e.g., browser navigation)
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const tab = params.get("tab") || "profile";
    if (tab !== activeTab) setActiveTab(tab);
    // eslint-disable-next-line
  }, [location.search]);

  if (loading) return <Spinner />;

  return (
    <div className="user-profile">
      <div className="profile-tabs-row">
        {user ? <UserIcon user={user} status={user.status} /> : null}

        {/* ✅ ADICIONAR título do perfil */}
        {user && (
          <div className="profile-title">
            Perfil de {user.name} {user.surname}
          </div>
        )}

        <div className="tabs">
          {renderTab("profile")}
          {renderTab("appraisals")}
          {renderTab("training")}
        </div>
        <div className="profile-actions">
          {/* Só mostra os botões de edição se a tab ativa for "profile" */}
          {activeTab === "profile" && (
            <>
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
                    {t("profileCancel")}
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
                    {t("profileSave")}
                  </>
                ) : (
                  <>
                    <FaPen style={{ marginRight: 8 }} />
                    {t("profileEdit")}
                  </>
                )}
              </button>
              {/* Botões mobile (ícones) */}
              {editMode && (
                <button
                  className="icon-btn edit-cancel-btn"
                  type="button"
                  title={t("profileCancel")}
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
                title={editMode ? t("profileSave") : t("profileEdit")}
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
            </>
          )}
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
                    e.target.src = generateInitialsAvatar(
                      user.name,
                      user.surname
                    );
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
              {user.manager ? (
                <div className="manager-card show-desktop">
                  {managerAvatar ? (
                    <img
                      src={managerAvatar}
                      alt="Manager"
                    />
                  ) : (
                    <img
                      src={user.manager.avatar}
                      alt="Manager"
                    />
                  )}
                  <div className="profile-label small">
                    <strong>
                      {user.manager.name} {user.manager.surname}
                    </strong>
                    <div>{user.manager.role}</div>
                  </div>
                </div>
              ) : (
                <div className="manager-card show-desktop">
                  <div className="profile-label small">No manager assigned</div>
                </div>
              )}

              {/* Manager card compacto mobile */}
              <div className="manager-compact-card show-mobile">
                <span className="manager-compact-role">
                  Manager
                  <br />
                </span>
                <div className="manager-compact-avatar">
                  <img
                    src={managerAvatar
                      ? managerAvatar
                      : (user.manager?.avatar || generateInitialsAvatar(user.manager?.name, user.manager?.surname))}
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
                {/* First Name */}
                <label>
                  {t("profileFirstName")}
                  <input
                    className="profile-input"
                    {...register("name", {
                      required: true,
                      minLength: 2,
                      pattern: /^[A-Za-zÀ-ÿ\s'-]+$/,
                    })}
                    disabled={!editMode}
                    placeholder={t("profilePlaceholderNA")}
                  />
                  {errors.name?.type === "required" && (
                    <span className="error-message">
                      {t("profileErrorFirstNameRequired")}
                    </span>
                  )}
                  {errors.name?.type === "minLength" && (
                    <span className="error-message">
                      {t("profileErrorFirstNameRequired")}
                    </span>
                  )}
                  {errors.name?.type === "pattern" && (
                    <span className="error-message">
                      {t("profileErrorFirstNameInvalid")}
                    </span>
                  )}
                </label>
                {/* Last Name */}
                <label>
                  {t("profileLastName")}
                  <input
                    className="profile-input"
                    {...register("surname", {
                      required: true,
                      minLength: 2,
                      pattern: /^[A-Za-zÀ-ÿ\s'-]+$/,
                    })}
                    disabled={!editMode}
                    placeholder={t("profilePlaceholderNA")}
                  />
                  {errors.surname?.type === "required" && (
                    <span className="error-message">
                      {t("profileErrorLastNameRequired")}
                    </span>
                  )}
                  {errors.surname?.type === "minLength" && (
                    <span className="error-message">
                      {t("profileErrorLastNameRequired")}
                    </span>
                  )}
                  {errors.surname?.type === "pattern" && (
                    <span className="error-message">
                      {t("profileErrorLastNameInvalid")}
                    </span>
                  )}
                </label>
                {/* Birth Date */}
                <label>
                  {t("profileBirthDate")}
                  <input
                    type="date"
                    className="profile-input"
                    {...register("birthdate", {
                      required: true,
                      validate: (value) => {
                        const today = new Date().toISOString().split("T")[0];
                        if (value > today) {
                          return "future";
                        }
                        const minDate = new Date();
                        minDate.setFullYear(minDate.getFullYear() - 16);
                        if (value > minDate.toISOString().split("T")[0]) {
                          return "tooyoung";
                        }
                        return true;
                      },
                    })}
                    disabled={!editMode}
                  />
                  {errors.birthdate?.type === "required" && (
                    <span className="error-message">
                      {t("profileErrorBirthDateRequired")}
                    </span>
                  )}
                  {errors.birthdate?.message === "future" && (
                    <span className="error-message">
                      {t("profileErrorBirthDateFuture")}
                    </span>
                  )}
                  {errors.birthdate?.message === "tooyoung" && (
                    <span className="error-message">
                      {t("profileErrorBirthDateTooYoung")}
                    </span>
                  )}
                </label>
                {/* Role */}
                <label>
                  {t("profileRole")}
                  {editMode ? (
                    <div className="select-wrapper">
                      <select
                        className="profile-input"
                        {...register("role", {
                          required: true,
                        })}
                      >
                        <option value="">{t("selectRole")}</option>
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
                  {errors.role?.type === "required" && (
                    <span className="error-message">
                      {t("profileErrorRoleRequired")}
                    </span>
                  )}
                </label>
                {/* Workplace */}
                <label>
                  {t("profileWorkplace")}
                  {editMode ? (
                    <div className="select-wrapper">
                      <select
                        className="profile-input"
                        {...register("office", {
                          required: true,
                        })}
                      >
                        <option value="">{t("selectOffice")}</option>
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
                  {errors.office?.type === "required" && (
                    <span className="error-message">
                      {t("profileErrorWorkplaceRequired")}
                    </span>
                  )}
                </label>
                {/* Phone */}
                <label>
                  {t("profilePhone")}
                  <input
                    className="profile-input"
                    {...register("phone", {
                      required: true,
                      pattern: /^[0-9]{9}$/,
                    })}
                    disabled={!editMode}
                    placeholder={t("profilePlaceholderNA")}
                  />
                  {errors.phone?.type === "required" && (
                    <span className="error-message">
                      {t("profileErrorPhoneRequired")}
                    </span>
                  )}
                  {errors.phone?.type === "pattern" && (
                    <span className="error-message">
                      {t("errorPhoneNumberInvalid")}
                    </span>
                  )}
                </label>
                {/* Address fields */}
                <div className="address-container">
                  <label>
                    {t("profileAddress")}
                    <input
                      className="profile-input"
                      value={[user.street, user.postalCode, user.municipality]
                        .filter(Boolean)
                        .join(", ")}
                      disabled
                      placeholder={t("profilePlaceholderNA")}
                    />
                  </label>
                  {editMode && (
                    <div
                      className={`address-edit-fields${
                        showAddressFields ? " slide-in" : ""
                      }`}
                    >
                      {/* Street */}
                      <label>
                        {t("profileAddressStreet")}
                        <input
                          className="profile-input"
                          {...register("street", {
                            required: true,
                            minLength: 3,
                            pattern: /^[A-Za-zÀ-ÿ0-9\s'-,.]+$/,
                          })}
                          placeholder={t("profileAddressStreet")}
                        />
                        {errors.street?.type === "required" && (
                          <span className="error-message">
                            {t("profileErrorAddressStreetRequired")}
                          </span>
                        )}
                        {errors.street?.type === "minLength" && (
                          <span className="error-message">
                            {t("profileErrorAddressStreetRequired")}
                          </span>
                        )}
                        {errors.street?.type === "pattern" && (
                          <span className="error-message">
                            {t("profileErrorAddressStreetInvalid")}
                          </span>
                        )}
                      </label>
                      {/* Postal Code */}
                      <label>
                        {t("profileAddressPostalCode")}
                        <input
                          className="profile-input"
                          {...register("postalCode", {
                            required: true,
                            pattern: /^[0-9]{4}-[0-9]{3}$/,
                          })}
                          placeholder={t("profileAddressPostalCode")}
                        />
                        {errors.postalCode?.type === "required" && (
                          <span className="error-message">
                            {t("profileErrorAddressPostalCodeRequired")}
                          </span>
                        )}
                        {errors.postalCode?.type === "pattern" && (
                          <span className="error-message">
                            {t("errorPostalCodeInvalid")}
                          </span>
                        )}
                      </label>
                      {/* Municipality */}
                      <label>
                        {t("profileAddressMunicipality")}
                        <input
                          className="profile-input"
                          {...register("municipality", {
                            required: true,
                            minLength: 2,
                            pattern: /^[A-Za-zÀ-ÿ\s'-]+$/,
                          })}
                          placeholder={t("profileAddressMunicipality")}
                        />
                        {errors.municipality?.type === "required" && (
                          <span className="error-message">
                            {t("profileErrorAddressMunicipalityRequired")}
                          </span>
                        )}
                        {errors.municipality?.type === "minLength" && (
                          <span className="error-message">
                            {t("profileErrorAddressMunicipalityRequired")}
                          </span>
                        )}
                        {errors.municipality?.type === "pattern" && (
                          <span className="error-message">
                            {t("profileErrorAddressMunicipalityInvalid")}
                          </span>
                        )}
                      </label>
                    </div>
                  )}
                </div>
                {/* Biography */}
                <label>
                  {t("profileBiography")}
                  <textarea
                    className="profile-input"
                    {...register("biography")}
                    disabled={!editMode}
                    placeholder={t("profileBiography")}
                  />
                </label>
              </div>
            </form>
          </div>
        </div>
      )}
      {activeTab === "appraisals" && <AppraisalsTab userId={userId} />}
      {activeTab === "training" && <TrainingTab userId={userId} />}
    </div>
  );
}
