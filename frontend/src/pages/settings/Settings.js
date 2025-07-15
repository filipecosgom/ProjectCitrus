import React, { useState } from "react";
import {
  IoSettings,
  IoSave,
  IoShieldCheckmark,
  IoRefresh,
} from "react-icons/io5";
import "./Settings.css";
import AdminPermissionsOffcanvas from "../../components/settings/AdminPermissionsOffcanvas";
import { useTranslation } from "react-i18next";
import { updateTwoFactorAuth } from "../../api/settingsApi";
import {
  showSuccessToast,
  showErrorToast,
} from "../../utils/toastConfig/toastConfig";

const Settings = () => {
  const { t } = useTranslation();
  const [settings, setSettings] = useState({
    emailNotifications: true,
    systemMaintenance: false,
    disableGoogleAuthenticator: false,
  });

  const [showAdminOffcanvas, setShowAdminOffcanvas] = useState(false);

  const handleSettingChange = (key, value) => {
    setSettings((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  const handleSave = async () => {
    try {
      await updateTwoFactorAuth(!settings.disableGoogleAuthenticator);
      showSuccessToast(
        "Configuração de autenticação 2 fatores atualizada com sucesso!"
      );
    } catch (error) {
      showErrorToast("Erro ao atualizar autenticação 2 fatores.");
    }
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
        {/* --- Admin Permissions section moved to the top --- */}
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

        {/* --- 2-Factor Authentication section --- */}
        <div className="settings-section">
          <h2>{t("twoFactorAuthentication")}</h2>
          <div className="setting-item">
            <div className="setting-label">
              <h3>{t("disableGoogleAuthenticator")}</h3>
              <p>{t("disableGoogleAuthenticatorDesc")}</p>
            </div>
            <label className="setting-toggle">
              <input
                type="checkbox"
                checked={settings.disableGoogleAuthenticator}
                onChange={(e) =>
                  handleSettingChange(
                    "disableGoogleAuthenticator",
                    e.target.checked
                  )
                }
              />
              <span className="toggle-slider"></span>
            </label>
          </div>
        </div>

        {/* --- General Settings section --- */}
        <div className="settings-section">
          <h2>{t("generalSettings")}</h2>

          <div className="setting-item">
            <div className="setting-label">
              <h3>{t("emailNotifications")}</h3>
              <p>{t("emailNotificationsDesc")}</p>
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
