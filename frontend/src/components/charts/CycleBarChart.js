/**
 * CycleBarChart module.
 * Renders a bar chart showing the distribution of cycles by state (open, closed).
 * Uses Recharts for visualization and supports internationalization.
 * @module CycleBarChart
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
 * CycleBarChart component for displaying cycle state distribution as a bar chart.
 * @param {Object} props - Component props
 * @param {number} props.open - Number of open cycles
 * @param {number} props.closed - Number of closed cycles
 * @param {function} props.t - Translation function
 * @returns {JSX.Element} The rendered bar chart
 */
export default function CycleBarChart({ open, closed, t }) {
  /**
   * Prepares the data for the bar chart.
   * @returns {Array} Array of cycle state objects
   */
  const data = [
    { name: t("cycles.statusOpen"), value: open },
    { name: t("cycles.statusClosed"), value: closed },
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
        {t("dashboard.cycles")}
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
