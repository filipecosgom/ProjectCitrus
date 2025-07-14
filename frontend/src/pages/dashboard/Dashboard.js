/**
 * @file Dashboard.js
 * @module Dashboard
 * @description React page component for displaying system-wide statistics and visualizations.
 * Provides summary cards for users, courses, appraisals, and cycles, as well as interactive charts.
 * Integrates with backend statistics endpoints and supports navigation to management pages.
 * @author Project Citrus Team
 */

/**
 * Dashboard Page Component.
 *
 * Renders the dashboard interface, including:
 * - Summary cards for users, courses, appraisals, and cycles
 * - Pie and bar charts for user profiles, roles, appraisal states, courses, and cycles
 * - Quick navigation to management pages via card clicks
 * - Responsive layout for charts and statistics
 * - Integration with backend statistics endpoints
 *
 * @returns {JSX.Element} The rendered dashboard page
 *
 */

import React, { useEffect, useState } from "react";
import { api } from "../../api/api";
import { FaUsers, FaBook, FaAward, FaCalendarAlt } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import UserProfilePieChart from "../../components/charts/UserProfilePieChart";
import UserRolePieChart from "../../components/charts/UserRolePieChart";
import AppraisalBarChart from "../../components/charts/AppraisalBarChart";
import CycleBarChart from "../../components/charts/CycleBarChart";
import CourseBarChart from "../../components/charts/CourseBarChart";
import AppraisalStatePieChart from "../../components/charts/AppraisalStatePieChart";
import "./Dashboard.css";
import { useNavigate } from "react-router-dom";

export default function Dashboard() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    users: {
      total: 0,
      profileCompletion: { complete: 0, incomplete: 0 },
      roles: { admin: 0, manager: 0, user: 0 },
    },
    courses: { total: 0, active: 0, inactive: 0, expiringSoon: [] },
    appraisals: {
      total: 0,
      inProgress: 0,
      completed: 0,
      closed: 0,
      closingSoon: [],
    },
    cycles: { total: 0, open: 0, closed: 0 },
  });

  useEffect(() => {
    Promise.all([
      api.get("/stats/users"),
      api.get("/stats/courses"),
      api.get("/stats/appraisals"),
      api.get("/stats/cycles"),
    ])
      .then(([users, courses, appraisals, cycles]) => {
        setStats({
          users: users.data.data,
          courses: courses.data.data,
          appraisals: appraisals.data.data,
          cycles: cycles.data.data,
        });
      })
      .catch((err) => {
        console.error("Erro ao obter estatísticas:", err);
      });
  }, []);

  // Percentagem de perfis completos
  const totalProfiles =
    stats.users.profileCompletion.complete +
    stats.users.profileCompletion.incomplete;
  const percentComplete =
    totalProfiles > 0
      ? Math.round(
          (stats.users.profileCompletion.complete / totalProfiles) * 100
        )
      : 0;

  return (
    <div>
      <div className="dashboard-grid">
        <div
          className="dashboard-card"
          style={{ cursor: "pointer" }}
          onClick={() => navigate("/users")}
        >
          <FaUsers className="dashboard-card-icon" />
          <div className="dashboard-card-value">{stats.users.total}</div>
          <div className="dashboard-card-label">{t("dashboard.users")}</div>
        </div>
        <div
          className="dashboard-card"
          style={{ cursor: "pointer" }}
          onClick={() => navigate("/courses")}
        >
          <FaBook className="dashboard-card-icon" />
          <div className="dashboard-card-value">{stats.courses.active}</div>
          <div className="dashboard-card-label">{t("dashboard.courses")}</div>
        </div>
        <div
          className="dashboard-card"
          style={{ cursor: "pointer" }}
          onClick={() => navigate("/appraisals")}
        >
          <FaAward className="dashboard-card-icon" />
          <div className="dashboard-card-value">{stats.appraisals.total}</div>
          <div className="dashboard-card-label">
            {t("dashboard.totalAppraisals")}
          </div>
        </div>
        <div
          className="dashboard-card"
          style={{ cursor: "pointer" }}
          onClick={() => navigate("/users?accountState=COMPLETE")}
        >
          <FaUsers className="dashboard-card-icon" />
          <div className="dashboard-card-value">{percentComplete}%</div>
          <div className="dashboard-card-label">
            {t("dashboard.percentProfilesComplete")}
          </div>
        </div>
        <div
          className="dashboard-card"
          style={{ cursor: "pointer" }}
          onClick={() => navigate("/cycles")}
        >
          <FaCalendarAlt className="dashboard-card-icon" />
          <div className="dashboard-card-value">{stats.cycles.open}</div>
          <div className="dashboard-card-label">{t("dashboard.cycles")}</div>
        </div>
      </div>
      {/* Secção de gráficos */}
      <div
        className="dashboard-charts"
        style={{
          display: "flex",
          gap: "32px",
          margin: "32px 80px",
          flexWrap: "wrap",
        }}
      >
        <UserProfilePieChart
          complete={stats.users.profileCompletion.complete}
          incomplete={stats.users.profileCompletion.incomplete}
          t={t}
          className="dashboard-profile-piechart"
        />
        <UserRolePieChart
          roles={stats.users.roles}
          t={t}
          className="dashboard-role-piechart"
        />
        <AppraisalStatePieChart
          inProgress={stats.appraisals.inProgress}
          completed={stats.appraisals.completed}
          closed={stats.appraisals.closed}
          t={t}
          className="dashboard-role-piechart"
        />
        <CourseBarChart
          active={stats.courses.active}
          inactive={stats.courses.inactive}
          t={t}
        />
        <AppraisalBarChart
          inProgress={stats.appraisals.inProgress}
          completed={stats.appraisals.completed}
          closed={stats.appraisals.closed}
          t={t}
        />
        <CycleBarChart
          open={stats.cycles.open}
          closed={stats.cycles.closed}
          t={t}
        />
      </div>
      {/* Listas rápidas */}
      <div
        className="dashboard-lists"
        style={{
          display: "flex",
          gap: "32px",
          margin: "32px 80px",
          flexWrap: "wrap",
        }}
      ></div>
    </div>
  );
}
