/**
 * UserProfilePieChart module.
 * Renders a pie chart showing the distribution of user profile completion (complete, incomplete).
 * Uses Recharts for visualization and supports internationalization.
 * @module UserProfilePieChart
 */
import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";

/**
 * UserProfilePieChart component for displaying user profile completion as a pie chart.
 * @param {Object} props - Component props
 * @param {number} props.complete - Number of complete profiles
 * @param {number} props.incomplete - Number of incomplete profiles
 * @param {function} props.t - Translation function
 * @returns {JSX.Element} The rendered pie chart
 */
export default function UserProfilePieChart({ complete, incomplete, t }) {
  /**
   * Prepares the data for the pie chart.
   * @returns {Array} Array of profile completion objects
   */
  const data = [
    { name: t("users.accountStateComplete"), value: complete },
    { name: t("users.accountStateIncomplete"), value: incomplete },
  ];
  const COLORS = ["#9C2F31", "#FF8042"];

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
        {t("dashboard.users")}
      </h3>
      <PieChart width={264} height={220}>
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
