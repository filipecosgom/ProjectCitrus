/**
 * AppraisalStatePieChart module.
 * Renders a pie chart showing the distribution of appraisals by state (in progress, completed, closed).
 * Uses Recharts for visualization and supports internationalization.
 * @module AppraisalStatePieChart
 */
import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";

/**
 * AppraisalStatePieChart component for displaying appraisal state distribution as a pie chart.
 * @param {Object} props - Component props
 * @param {number} props.inProgress - Number of appraisals in progress
 * @param {number} props.completed - Number of completed appraisals
 * @param {number} props.closed - Number of closed appraisals
 * @param {function} props.t - Translation function
 * @returns {JSX.Element} The rendered pie chart
 */
export default function AppraisalStatePieChart({
  inProgress,
  completed,
  closed,
  t,
}) {
  /**
   * Prepares the data for the pie chart.
   * @returns {Array} Array of appraisal state objects
   */
  const data = [
    { name: t("appraisalStateInProgress"), value: inProgress },
    { name: t("appraisalStateCompleted"), value: completed },
    { name: t("appraisalStateClosed"), value: closed },
  ];
  const COLORS = ["#E57373", "#FF8042", "#9C2F31"];

  return (
    <div
      style={{
        background: "#fff",
        borderRadius: 10,
        padding: 24,
        boxShadow: "0 4px 16px rgba(0,0,0,0.08)",
      }}
    >
      <h3 style={{ textAlign: "center", marginBottom: 12 }}>
        {t("dashboard.appraisalsDistribution")}
      </h3>
      <PieChart width={264} height={230}>
        <Pie
          data={data}
          cx="50%"
          cy="50%"
          innerRadius={60}
          outerRadius={90}
          fill="#9C2F31"
          paddingAngle={5}
          dataKey="value"
          startAngle={90}
          endAngle={450}
        >
          {data.map((entry, index) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip />
        <Legend />
      </PieChart>
    </div>
  );
}
