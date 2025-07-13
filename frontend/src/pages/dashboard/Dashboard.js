import React, { useEffect, useState } from "react";
import { FaUsers, FaBook, FaAward, FaCalendarAlt } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import "./Dashboard.css";

export default function Dashboard() {
  const { t } = useTranslation();
  const [stats, setStats] = useState({
    users: 0,
    courses: 0,
    appraisals: 0,
    cycles: 0,
  });

  useEffect(() => {
    // Substitui por chamada real à API
    fetch("/api/dashboard/stats")
      .then((res) => res.json())
      .then(setStats)
      .catch(() => {});
  }, []);

  return (
    <div className="dashboard-grid">
      <div className="dashboard-card">
        <FaUsers className="dashboard-card-icon" />
        <div className="dashboard-card-value">{stats.users}</div>
        <div className="dashboard-card-label">{t("dashboardUsers")}</div>
      </div>
      <div className="dashboard-card">
        <FaBook className="dashboard-card-icon" />
        <div className="dashboard-card-value">{stats.courses}</div>
        <div className="dashboard-card-label">{t("dashboardCourses")}</div>
      </div>
      <div className="dashboard-card">
        <FaAward className="dashboard-card-icon" />
        <div className="dashboard-card-value">{stats.appraisals}</div>
        <div className="dashboard-card-label">{t("dashboardAppraisals")}</div>
      </div>
      <div className="dashboard-card">
        <FaCalendarAlt className="dashboard-card-icon" />
        <div className="dashboard-card-value">{stats.cycles}</div>
        <div className="dashboard-card-label">{t("dashboardCycles")}</div>
      </div>
    </div>
  );
}
