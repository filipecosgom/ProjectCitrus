/**
 * UserRolePieChart module.
 * Renders a pie chart showing the distribution of user roles (admin, manager, user).
 * Uses Recharts for visualization and supports internationalization.
 * @module UserRolePieChart
 */
import { PieChart, Pie, Cell, Tooltip, Legend } from "recharts";

/**
 * UserRolePieChart component for displaying user role distribution as a pie chart.
 * @param {Object} props - Component props
 * @param {Object} props.roles - Object with role counts (admin, manager, user)
 * @param {function} props.t - Translation function
 * @returns {JSX.Element} The rendered pie chart
 */
export default function UserRolePieChart({ roles, t }) {
  /**
   * Prepares the data for the pie chart.
   * @returns {Array} Array of role objects
   */
  const data = [
    { name: t("admin"), value: roles.admin },
    { name: t("manager"), value: roles.manager },
    { name: t("user"), value: roles.user },
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
