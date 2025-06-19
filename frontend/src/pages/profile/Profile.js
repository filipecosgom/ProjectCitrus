import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import "./Profile.css";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { FaPen } from "react-icons/fa";
import UserIcon from "../../components/userIcon/UserIcon";
import ProfilePhoto from "../../assets/photos/teresamatos.png";
import ManagerPhoto from "../../assets/photos/joseferreira.png";
import Spinner from "../../components/spinner/spinner";
import handleGetUserInformation from "../../handles/handleGetUserInformation";
import { avatarsUrl } from "../../config";
import axios from "axios";
import { handleUpdateUserInfo } from "../../handles/handleUpdateUser";
import { handleGetRoles, handleGetOffices } from "../../handles/handleGetEnums";

export default function Profile() {
  const userId = new URLSearchParams(useLocation().search).get("id");
  const [user, setUser] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [roleOptions, setRoleOptions] = useState([]);
  const [officeOptions, setOfficeOptions] = useState([]);
  const [showAddressFields, setShowAddressFields] = useState(false);
  const [activeTab, setActiveTab] = useState("profile");

  //Avatar
  const [avatarPreview, setAvatarPreview] = useState(null);
  const [avatarFile, setAvatarFile] = useState(null);

  const navigate = useNavigate();

  // react-hook-form setup
  const {
    register,
    handleSubmit,
    setValue,
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

  // Handler de submit - ao clicar em "Save"
  const onSubmit = async (data) => {
    // Ensure avatar is a File object if uploaded
    if (data.avatar && data.avatar.length > 0) {
      data.avatar = data.avatar[0]; // 👈 get the actual file
    } else {
      delete data.avatar; // optional: prevent sending empty file
    }

    const response = await handleUpdateUserInfo(userId, user, data);
    if (response) {
      setUser(data);
      setShowAddressFields(false);
      setEditMode(false);
    }
    setShowAddressFields(false);
    setEditMode(false);
  };

  // Handler para upload de avatar
  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAvatarFile(file);
      setAvatarPreview(URL.createObjectURL(file));
    }
  };

  const renderTab = (tab) => (
    <button
      className={`tab ${activeTab === tab ? "active" : ""}`}
      onClick={() => setActiveTab(tab)}
      type="button"
    >
      {tab.charAt(0).toUpperCase() + tab.slice(1)}
    </button>
  );

  if (loading) return <Spinner />;

  return (
    <div className="user-profile">
      <div className="profile-tabs-row">
        {user ? <UserIcon avatar={user.avatar} status="check" /> : null}
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
                {editMode && (
                  <>
                    <input
                      type="file"
                      accept="image/*"
                      id="avatar-upload"
                      {...register("avatar")}
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
                <img
                  src={
                    avatarPreview
                      ? avatarPreview
                      : user.avatar
                      ? `${avatarsUrl}${user.avatar}`
                      : ProfilePhoto
                  }
                  alt="Profile"
                  onError={(e) => {
                    e.target.onerror = null; // prevent infinite loop
                    e.target.src = ProfilePhoto;
                  }}
                />
                <div className="profile-label">
                  <strong>
                    {user.name} {user.surname}
                  </strong>
                  <span>{user.role ? user.role.replace(/_/g, " ") : ""}</span>
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
            <div className="manager-card">
              <img src={user.manager?.avatar || ManagerPhoto} alt="Manager" />
              <div className="profile-label small">
                <strong>
                  {user.manager?.name} {user.manager?.surname}
                </strong>
                <span>{user.manager?.role}</span>
              </div>
            </div>
          </div>
        </div>
      )}
      <ToastContainer />
    </div>
  );
}
