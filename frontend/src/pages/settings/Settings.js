import React, { useState } from "react";
import {
  IoSettings,
  IoSave,
  IoRefresh,
  IoShieldCheckmark,
} from "react-icons/io5";
import "./Settings.css";
import AdminPermissionsOffcanvas from "../../components/settings/AdminPermissionsOffcanvas";
import { useTranslation } from "react-i18next";

const Settings = () => {
  const { t } = useTranslation();
  const [settings, setSettings] = useState({
    emailNotifications: true,
    autoCloseExpiredCycles: false,
    defaultCycleDuration: 90,
    systemMaintenance: false,
  });

  const [showAdminOffcanvas, setShowAdminOffcanvas] = useState(false);

  const handleSettingChange = (key, value) => {
    setSettings((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  const handleSave = () => {
    // TODO: Save to API
  };

  return (
    <div className="settings-page">
      <div className="settings-header">
        <div className="settings-title">
          <IoSettings className="settings-icon" />
          <h1>{t("systemSettings")}</h1>
        </div>
        <button className="btn-save-settings" onClick={handleSave}>
          <IoSave /> {t("saveChanges")}
        </button>
      </div>

      <div className="settings-content">
        <div className="settings-section">
          <h2>{t("generalSettings")}</h2>

          <div className="setting-item">
            <div className="setting-label">
              <h3>Email Notifications</h3>
              <p>Send email notifications for important events</p>
            </div>
            <label className="setting-toggle">
              <input
                type="checkbox"
                checked={settings.emailNotifications}
                onChange={(e) =>
                  handleSettingChange("emailNotifications", e.target.checked)
                }
              />
              <span className="toggle-slider"></span>
            </label>
          </div>

          <div className="setting-item">
            <div className="setting-label">
              <h3>Auto-close Expired Cycles</h3>
              <p>Automatically close cycles that have passed their end date</p>
            </div>
            <label className="setting-toggle">
              <input
                type="checkbox"
                checked={settings.autoCloseExpiredCycles}
                onChange={(e) =>
                  handleSettingChange(
                    "autoCloseExpiredCycles",
                    e.target.checked
                  )
                }
              />
              <span className="toggle-slider"></span>
            </label>
          </div>

          <div className="setting-item">
            <div className="setting-label">
              <h3>Default Cycle Duration</h3>
              <p>Default duration in days for new performance cycles</p>
            </div>
            <div className="setting-input">
              <input
                type="number"
                value={settings.defaultCycleDuration}
                onChange={(e) =>
                  handleSettingChange(
                    "defaultCycleDuration",
                    parseInt(e.target.value)
                  )
                }
                min="30"
                max="365"
              />
              <span>days</span>
            </div>
          </div>
        </div>

        <div className="settings-section">
          <h2>System Maintenance</h2>

          <div className="setting-item">
            <div className="setting-label">
              <h3>Maintenance Mode</h3>
              <p>Enable maintenance mode to restrict access during updates</p>
            </div>
            <label className="setting-toggle">
              <input
                type="checkbox"
                checked={settings.systemMaintenance}
                onChange={(e) =>
                  handleSettingChange("systemMaintenance", e.target.checked)
                }
              />
              <span className="toggle-slider"></span>
            </label>
          </div>

          <div className="setting-item">
            <button className="btn-maintenance-action">
              <IoRefresh /> Clear System Cache
            </button>
          </div>
        </div>

        <div className="settings-section">
          <h2>{t("adminPermissions")}</h2>

          <div className="setting-item">
            <div className="setting-label">
              <h3>
                <IoShieldCheckmark
                  style={{ marginRight: 8, color: "#2e7d2e" }}
                />
                {t("manageAdminPermissions")}
              </h3>
              <p>{t("manageAdminPermissionsDesc")}</p>
            </div>
            <button
              className="btn-save-settings"
              style={{ background: "#2e7d2e" }}
              onClick={() => setShowAdminOffcanvas(true)}
            >
              {t("manageAdminPermissions")}
            </button>
          </div>
        </div>

        <AdminPermissionsOffcanvas
          show={showAdminOffcanvas}
          onClose={() => setShowAdminOffcanvas(false)}
        />
      </div>
    </div>
  );
};

export default Settings;
