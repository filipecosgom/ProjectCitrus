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

const mockUser = {
  name: "Teresa",
  surname: "Matos",
  birthdate: "1990-01-01",
  role: "Developer",
  office: "Lisbon Office",
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
  const navigate = useNavigate();


  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleEditToggle = () => {
    if (editMode) {
      // Mock save
      //setUser(formData);
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
  if(formData) {
    console.log("Form data updated:", formData);
  }
}, [formData]);




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
              <img src={`${avatarsUrl}${formData.avatar}`} alt="Profile" />
              <div className="profile-label">
                <strong>
                  {formData.name} {formData.surname}
                              </strong>
                <span>{formData.role}</span>
              </div>
            </div>

            <div className="form-fields">
              <label>
                First Name
                <input
                  name="firstName"
                  value={formData.name}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Last Name
                <input
                  name="lastName"
                  value={formData.surname}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Date of Birth
                <input
                  type="date"
                  name="dob"
                  value={formData.birthdate}
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
                  value={formData.office}
                  onChange={handleChange}
                  disabled={!editMode}
                />
              </label>
              <label>
                Address
                <input
                  name="address"
                  value={formData.street + ", " + formData.municipality + ", " + formData.postalCode}
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
              <img src={formData.manager.image} alt="Manager" />
              <div className="profile-label small">
                <strong>
                  {formData.manager.firstName} {formData.manager.lastName}
                </strong>
                <span>{formData.manager.role}</span>
              </div>
            </div>
          </div>
        </div>
      )}

      <ToastContainer />
    </div>
  );
}
