import React, { useEffect, useState } from "react";
import { api } from "../../api/api"; // Usa o objeto api configurado!
import { apiBaseUrl } from "../../config";
import { FaUsers, FaBook, FaAward, FaCalendarAlt } from "react-icons/fa";
import { useTranslation } from "react-i18next";
import UserProfilePieChart from "../../components/charts/UserProfilePieChart";
import UserRolePieChart from "../../components/charts/UserRolePieChart";
import AppraisalBarChart from "../../components/charts/AppraisalBarChart";
import CycleBarChart from "../../components/charts/CycleBarChart";
import CourseBarChart from "../../components/charts/CourseBarChart";
import "./Dashboard.css";

export default function Dashboard() {
  const { t } = useTranslation();
  const [stats, setStats] = useState({
    users: {
      total: 0,
      profileCompletion: { complete: 0, incomplete: 0 },
      roles: { admin: 0, manager: 0, user: 0 },
    },
    courses: { total: 0, active: 0, inactive: 0 },
    appraisals: { total: 0, inProgress: 0, completed: 0, closed: 0 },
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
        console.log("Users response:", users.data);
        console.log("Courses response:", courses.data);
        console.log("Appraisals response:", appraisals.data);
        console.log("Cycles response:", cycles.data);
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

  return (
    <div>
      <div className="dashboard-grid">
        <div className="dashboard-card">
          <FaUsers className="dashboard-card-icon" />
          <div className="dashboard-card-value">{stats.users.total}</div>
          <div className="dashboard-card-label">{t("dashboard.users")}</div>
        </div>
        <div className="dashboard-card">
          <FaBook className="dashboard-card-icon" />
          <div className="dashboard-card-value">{stats.courses.active}</div>
          <div className="dashboard-card-label">{t("dashboard.courses")}</div>
        </div>
        <div className="dashboard-card">
          <FaAward className="dashboard-card-icon" />
          <div className="dashboard-card-value">
            {stats.appraisals.inProgress}
          </div>
          <div className="dashboard-card-label">
            {t("dashboard.appraisals")}
          </div>
        </div>
        <div className="dashboard-card">
          <FaCalendarAlt className="dashboard-card-icon" />
          <div className="dashboard-card-value">{stats.cycles.open}</div>
          <div className="dashboard-card-label">{t("dashboard.cycles")}</div>
        </div>
      </div>
      {/* Secção de gráficos */}
      <div
        className="dashboard-charts"
        style={{ display: "flex", gap: "32px", margin: "32px 80px" }}
      >
        <UserProfilePieChart
          complete={stats.users.profileCompletion.complete}
          incomplete={stats.users.profileCompletion.incomplete}
          t={t}
        />
        <UserRolePieChart roles={stats.users.roles} t={t} />
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
    </div>
  );
}
