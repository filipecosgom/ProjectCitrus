import React, { useState, useEffect, use } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./Profile.css";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import ProfilePhoto from "../../assets/photos/teresamatos.png";
import ManagerPhoto from "../../assets/photos/joseferreira.png";
import useAuthStore from "../../stores/useAuthStore";
import Spinner from "../../components/spinner/spinner";
import handleGetUserInformation from "../../handles/handleGetUserInformation";
import handleNotification from "../../handles/handleNotification";
import { set } from "react-hook-form";
import { avatarsUrl } from "../../config";
import axios from "axios";
import { apiBaseUrl } from "../../config";

const mockUser = {
  name: "Teresa",
  surname: "Matos",
  birthdate: "1990-01-01",
  role: "SOFTWARE_ENGINEER", // <-- valor igual ao enum do backend
  office: "LISBON_OFFICE", // <-- valor igual ao enum do backend
  street: "Rua das Flores, 123",
  municipality: "Coimbra",
  postalCode: "3000-123",
  biography: "Sou uma programadora dedicada e adoro React!",
  avatar: ProfilePhoto,
  managerId: {
    firstName: "José",
    lastName: "Ferreira",
    role: "Manager",
    image: ManagerPhoto,
  },
};

export default function Profile() {
  const userId = new URLSearchParams(useLocation().search).get("id");
  const [user, setUser] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState(user);
  const [activeTab, setActiveTab] = useState("profile");
  const [loading, setLoading] = useState(true);
  const [roleOptions, setRoleOptions] = useState([]);
  const [officeOptions, setOfficeOptions] = useState([]);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleEditToggle = () => {
    if (editMode) {
      // Simula envio para backend
      console.log("Dados enviados para atualização:", formData);
      toast.success("Perfil atualizado com sucesso!");
      setUser(formData);
    }
    setEditMode(!editMode);
  };

  const renderTab = (tab) => (
    <button
      className={`tab ${activeTab === tab ? "active" : ""}`}
      onClick={() => setActiveTab(tab)}
    >
      {tab.charAt(0).toUpperCase() + tab.slice(1)}
    </button>
  );

  useEffect(() => {
    const fetchUserInformation = async () => {
      try {
        const userInfo = await handleGetUserInformation(userId);
        if (userInfo) {
          console.log("User Information:", userInfo);
          setUser(userInfo);
        } else {
          console.log("No user information found.");
        }
      } catch (error) {
        navigate("/");
      } finally {
        setLoading(false);
      }
    };
    fetchUserInformation();
  }, [userId]);

  useEffect(() => {
    if (user) {
      console.log("User data loaded:", user);
      setFormData(user);
    }
  }, [user]);

  useEffect(() => {
    if (formData) {
      console.log("Form data updated:", formData);
    }
  }, [formData]);

  useEffect(() => {
    axios
      .get(`${apiBaseUrl}/enums/roles`)
      .then((res) => setRoleOptions(res.data));
    axios
      .get(`${apiBaseUrl}/enums/offices`)
      .then((res) => setOfficeOptions(res.data));
  }, []);

  if (loading) return <Spinner />;

  return (
    <div className="user-profile">
      <div className="profile-tabs-row">
        <div className="tabs">
          {renderTab("profile")}
          {renderTab("appraisals")}
          {renderTab("training")}
        </div>
        <button className="edit-btn" onClick={handleEditToggle}>
          {editMode ? "Save" : "Edit"}
        </button>
      </div>
      {activeTab === "profile" && (
        <div className="profile-section">
          <div className="profile-header">
            <div className="profile-card">
              <img src={`${avatarsUrl}${user.avatar}`} alt="Profile" />
              <div className="profile-label">
                <strong>
                  {user.name} {user.surname}
                </strong>
                <span>{user.role}</span>
              </div>
            </div>

            <div className="form-fields">
              <label>
                First Name
                <input
                  name="firstName"
                  value={user.name}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Last Name
                <input
                  name="lastName"
                  value={user.surname}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Date of Birth
                <input
                  type="date"
                  name="dob"
                  value={user.birthdate}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Role
                {editMode ? (
                  <div className="select-wrapper">
                    <select
                      name="role"
                      value={formData.role}
                      onChange={handleChange}
                    >
                      <option value="">Select role</option>
                      {roleOptions.map((role) => (
                        <option key={role} value={role}>
                          {role.replace(/_/g, " ")}
                        </option>
                      ))}
                    </select>
                    <span className="select-arrow">
                      {/* Seta SVG */}
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
                    name="role"
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
                      name="office"
                      value={formData.office}
                      onChange={handleChange}
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
                    name="office"
                    value={user.office ? user.office.replace(/_/g, " ") : ""}
                    disabled
                  />
                )}
              </label>
              <label>
                Address {/*Tem de se dividir em 3 campos*/}
                <input
                  name="address"
                  value={
                    user.street +
                    ", " +
                    user.municipality +
                    ", " +
                    user.postalCode
                  }
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Biography
                <textarea
                  name="biography"
                  value={user.biography}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
            </div>

            <div className="manager-card">
              <img src={user.manager.avatar} alt="Manager" />
              <div className="profile-label small">
                <strong>
                  {user.manager.name} {user.manager.surname}
                </strong>
                <span>{user.manager.role}</span>
              </div>
            </div>
          </div>
        </div>
      )}

      <ToastContainer />
    </div>
  );
}
