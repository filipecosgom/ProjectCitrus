/**
 * CourseBarChart module.
 * Renders a bar chart showing the distribution of courses by state (active, inactive).
 * Uses Recharts for visualization and supports internationalization.
 * @module CourseBarChart
 */
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

/**
 * CourseBarChart component for displaying course state distribution as a bar chart.
 * @param {Object} props - Component props
 * @param {number} props.active - Number of active courses
 * @param {number} props.inactive - Number of inactive courses
 * @param {function} props.t - Translation function
 * @returns {JSX.Element} The rendered bar chart
 */
export default function CourseBarChart({ active, inactive, t }) {
  /**
   * Prepares the data for the bar chart.
   * @returns {Array} Array of course state objects
   */
  const data = [
    { name: t("courses.filterActive"), value: active },
    { name: t("courses.filterInactive"), value: inactive },
  ];

  return (
    <div
      style={{
        background: "#fff",
        borderRadius: 10,
        padding: 24,
        boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
        minWidth: 312, // aumentado 20%
      }}
    >
      <h3 style={{ textAlign: "center", marginBottom: 12 }}>
        {t("dashboard.courses")}
      </h3>
      <ResponsiveContainer width="100%" height={220}>
        <BarChart data={data}>
          <XAxis dataKey="name" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Legend />
          <Bar dataKey="value" fill="#9C2F31" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
