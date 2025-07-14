/**
 * AppraisalBarChart module.
 * Renders a bar chart showing the distribution of appraisals by state (in progress, completed, closed).
 * Uses Recharts for visualization and supports internationalization.
 * @module AppraisalBarChart
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
 * AppraisalBarChart component for displaying appraisal state distribution as a bar chart.
 * @param {Object} props - Component props
 * @param {number} props.inProgress - Number of appraisals in progress
 * @param {number} props.completed - Number of completed appraisals
 * @param {number} props.closed - Number of closed appraisals
 * @param {function} props.t - Translation function
 * @returns {JSX.Element} The rendered bar chart
 */
export default function AppraisalBarChart({
  inProgress,
  completed,
  closed,
  t,
}) {
  /**
   * Prepares the data for the bar chart.
   * @returns {Array} Array of appraisal state objects
   */
  const data = [
    { name: t("appraisalStateInProgress"), value: inProgress },
    { name: t("appraisalStateCompleted"), value: completed },
    { name: t("appraisalStateClosed"), value: closed },
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
        {t("dashboard.appraisals")}
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
