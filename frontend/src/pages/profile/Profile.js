import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./Profile.css";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import ProfilePhoto from "../../assets/photos/teresamatos.png";
import ManagerPhoto from "../../assets/photos/joseferreira.png";
import useAuthStore from "../../stores/useAuthStore";

const mockUser = {
  firstName: "Teresa",
  lastName: "Matos",
  dob: "1990-01-01",
  role: "Developer",
  workplace: "Lisbon Office",
  address: "Rua das Flores, 123",
  biography: "Sou uma programadora dedicada e adoro React!",
  profileImage: ProfilePhoto,
  manager: {
    firstName: "José",
    lastName: "Ferreira",
    role: "Manager",
    image: "/images/jose.jpg",
  },
};

export default function Profile() {
  const userId = new URLSearchParams(useLocation().search).get("id");
  const [user, setUser] = useState(mockUser);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState(user);
  const [activeTab, setActiveTab] = useState("profile");

  const { remainingTime } = useAuthStore();

const formatTime = (ms) => {
  const minutes = Math.floor(ms / 60000);
  const seconds = Math.floor((ms % 60000) / 1000);
  return `${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;
};

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleEditToggle = () => {
    if (editMode) {
      // Mock save
      setUser(formData);
      toast.success("Perfil atualizado com sucesso!");
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
    console.log("Fetching user information for ID:", userId);
  }, []);

  return (
    <div className="user-profile">
      <div className="tabs">
        {renderTab("profile")}
        {renderTab("appraisals")}
        {renderTab("training")}
      </div>

      {activeTab === "profile" && (
        <div className="profile-section">
          <div className="profile-header">
            <div className="profile-card">
              <img src={user.profileImage} alt="Profile" />
              <div className="profile-label">
                <strong>
                  {user.firstName} {user.lastName}
                              </strong>
                <span>{user.role}</span>
              </div>
            </div>

            <div className="form-fields">
              <label>
                First Name
                <input
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Last Name
                <input
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Date of Birth
                <input
                  type="date"
                  name="dob"
                  value={formData.dob}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Role
                <input
                  name="role"
                  value={formData.role}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Workplace
                <input
                  name="workplace"
                  value={formData.workplace}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Address
                <input
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Biography
                <textarea
                  name="biography"
                  value={formData.biography}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
            </div>

            <div className="manager-card">
              <img src={user.manager.image} alt="Manager" />
              <div className="profile-label small">
                <strong>
                  {user.manager.firstName} {user.manager.lastName}
                </strong>
                <span>{user.manager.role}</span>
              </div>
            </div>
          </div>

          <button className="edit-btn" onClick={handleEditToggle}>
            {editMode ? "Save" : "Edit"}
          </button>
          <p>Session expires in: {formatTime(remainingTime)}</p>;
        </div>
      )}

      <ToastContainer />
    </div>
  );
}
