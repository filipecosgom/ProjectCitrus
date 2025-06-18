import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import "./Profile.css";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import ProfilePhoto from "../../assets/photos/teresamatos.png";
import ManagerPhoto from "../../assets/photos/joseferreira.png";
import Spinner from "../../components/spinner/spinner";
import handleGetUserInformation from "../../handles/handleGetUserInformation";
import { avatarsUrl, apiBaseUrl } from "../../config";
import axios from "axios";
import { handleUpdateUser } from "../../handles/handleUpdateUser";
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
  const onSubmit = (data) => {
    handleUpdateUser(userId, user, data);
    setUser(data);
    setShowAddressFields(false);
    setEditMode(false);
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
            <div className="profile-card">
              <img
                src={user.avatar ? `${avatarsUrl}${user.avatar}` : ProfilePhoto}
                alt="Profile"
              />
              <div className="profile-label">
                <strong>
                  {user.name} {user.surname}
                </strong>
                <span>{user.role ? user.role.replace(/_/g, " ") : ""}</span>
              </div>
            </div>
            <form className="profile-form" onSubmit={handleSubmit(onSubmit)}>
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
                          <option key={role} value={role.charAt(0).toUpperCase() + role.slice(1).toLowerCase()}>
                            {role.replace(/_/g, " ")}
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
                            {office.replace(/_/g, " ")}
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
